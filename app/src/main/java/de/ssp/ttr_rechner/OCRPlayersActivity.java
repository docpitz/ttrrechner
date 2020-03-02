package de.ssp.ttr_rechner;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.model.SearchPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.model.PlayerChooseable;
import de.ssp.ttr_rechner.model.SearchPlayerResults;
import de.ssp.ttr_rechner.model.SearchPlayerUtil;
import de.ssp.ttr_rechner.ocr.playerrecognition.PlayerRecognitionProcessor;
import de.ssp.ttr_rechner.ocr.playerrecognition.PlayerRecognitionProcessorFinisher;
import de.ssp.ttr_rechner.service.caller.ServiceCallerSearchPlayers;
import de.ssp.ttr_rechner.service.caller.ServiceFinish;
import de.ssp.ttr_rechner.ui.ocr.FoundedPlayerRowViewHolder;
import de.ssp.ttr_rechner.ui.ocr.SearchResultsSection;
import de.ssp.ttr_rechner.ui.util.FloatingActionButtonUtil;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class OCRPlayersActivity extends AppCompatActivity implements SearchResultsSection.ClickListener, PlayerRecognitionProcessorFinisher, ServiceFinish<List<SearchPlayer>, List<SearchPlayerResults>>
{
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int REQUEST_CHOOSE_IMAGE = 1002;

    private SectionedRecyclerViewAdapter sectionedAdapter;
    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.fab) FloatingActionButton fab;
    protected @BindView(R.id.recyclerview) RecyclerView recyclerView;
    public static String PUT_EXTRA_IS_CAMERA_USING;
    protected ArrayList<PlayerChooseable> players;
    protected boolean isPlayerImageShown;

    private Uri imageUri;
    private PlayerRecognitionProcessor imageProcessor;
    private boolean isStartedService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        isPlayerImageShown = new MyTischtennisCredentials(this).isPlayersImageShow();

        setContentView(R.layout.ocr_founded_activity);
        ButterKnife.bind(this);

        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sectionedAdapter = new SectionedRecyclerViewAdapter();

        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionedAdapter);

        players = new ArrayList<>();
        setFabVisibility();

        startOCRScanning(getIntent().getBooleanExtra(PUT_EXTRA_IS_CAMERA_USING, false));
    }

    @Override
    public void onItemRootViewClicked(@NonNull PlayerChooseable playerChooesed, FoundedPlayerRowViewHolder viewHolder)
    {
        playerChooesed.isChecked = !playerChooesed.isChecked;
        viewHolder.setCheckBoxIsChoosed(playerChooesed.isChecked);
        setFabVisibility();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            if(isOnePlayerChoosed())
            {
                showConsultationForBackButton();
            }
            else
            {
                setResult(RESULT_CANCELED);
                this.finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    protected void calcResultAndFinishActivity()
    {
        showConsulationForSelfPlayer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            tryReloadAndDetectInImage();
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = intent.getData();
            tryReloadAndDetectInImage();
        }
        else if(resultCode == RESULT_CANCELED)
        {
            showActionNoPlayersFound();
        }
    }

    private String getTreffer(List<PlayerChooseable> list)
    {
        if(list.isEmpty())
        {
            return " (keine Treffer)";
        }
        else if(list.size() == 1)
        {
            return " (gefunden)";
        }
        else
        {
            return " (" + list.size() + " Treffer)";
        }
    }

    private void startCameraIntentForResult() {
        // Clean up last time's image
        imageUri = null;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE);
    }

    private void tryReloadAndDetectInImage()
    {
        try {
            if (imageUri == null) {
                return;
            }
            Bitmap imageBitmap;
            if (Build.VERSION.SDK_INT < 29) {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                imageBitmap = ImageDecoder.decodeBitmap(source);
            }

            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) imageBitmap.getWidth() / (float) targetWidth,
                            (float) imageBitmap.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            imageBitmap,
                            (int) (imageBitmap.getWidth() / scaleFactor),
                            (int) (imageBitmap.getHeight() / scaleFactor),
                            true);

            //preview.setImageBitmap(resizedBitmap);
            imageProcessor = new PlayerRecognitionProcessor(this, this);
            imageProcessor.process(resizedBitmap);
        } catch (IOException e) {
            // Log.e(TAG, "Error retrieving saved image");
        }
    }

    private Pair<Integer, Integer> getTargetedWidthHeight() {
        boolean isLandScape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        int targetWidth = isLandScape ? 640 : 480;
        int targetHeight = isLandScape ? 480 : 640;
        return new Pair<>(targetWidth, targetHeight);
    }

    @Override
    public void ocrFinished(ArrayList<SearchPlayer> searchPlayers)
    {
        if(isStartedService) return;

        if(searchPlayers.isEmpty())
        {
            showActionNoPlayersFound();
        }
        else
        {
            for (SearchPlayer searchPlayer : searchPlayers) {
                SearchPlayerUtil searchPlayerUtil = new SearchPlayerUtil(searchPlayer);
                String title =  searchPlayerUtil.getFullNameWithClub();

                sectionedAdapter.addSection(new SearchResultsSection(title, null, SearchResultsSection.LOADING, null, isPlayerImageShown, this, recyclerView));
                sectionedAdapter.notifyDataSetChanged();
            }

            isStartedService = true;
            ServiceCallerSearchPlayers serviceCallerSearchPlayers = new ServiceCallerSearchPlayers(this, this, searchPlayers, true);
            serviceCallerSearchPlayers.callService();
        }
        setFabVisibility();
    }

    @Override
    public void serviceFinished(List<SearchPlayer> searchObject, boolean success, List<SearchPlayerResults> searchPlayerResults, String errorMessage)
    {
        isStartedService = false;
        sectionedAdapter.removeAllSections();
        players.removeAll(players);
        for (SearchPlayerResults results : searchPlayerResults) {
            SearchPlayerUtil searchPlayerUtil = new SearchPlayerUtil(results.getSearchPlayer());
            String title = searchPlayerUtil.getFullNameWithClub() + getTreffer(results.getSearchPlayerResultsList());
            sectionedAdapter.addSection(new SearchResultsSection(title, results.getSearchPlayerResultsList(), results.getError(), this, isPlayerImageShown, this, recyclerView));
            sectionedAdapter.notifyDataSetChanged();

            if(results.getSearchPlayerResultsList() != null) {
                players.addAll(results.getSearchPlayerResultsList());
            }
            if(results.getSearchPlayerResultsList().size() == 1)
            {
                results.getSearchPlayerResultsList().get(0).isChecked = true;
            }
        }
        setFabVisibility();
    }

    protected void showActionNoPlayersFound()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Bilderkennung erfolglos")
                .setMessage("Leider konnten keine Spieler gefunden werden. Versuchen Sie es erneut.")
                .setCancelable(false)
                .setPositiveButton(R.string.kamera, (dialog, which) -> startOCRScanning(true))
                .setNegativeButton(R.string.album, (dialg, which) -> startOCRScanning(false))
                .setNeutralButton(R.string.cancel, (dialogInterface, which) -> {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                });

        dialogBuilder.create().show();
    }

    protected ArrayList<Player> getChoosedPlayers()
    {
        ArrayList<Player> playerArrayList = new ArrayList<>();
        for (PlayerChooseable player: players)
        {
            if(player.isChecked)
            {
                playerArrayList.add(player.player);
            }
        }
        return playerArrayList;
    }

    private void startOCRScanning(boolean isCameraUsing)
    {
        if(isCameraUsing)
        {
            startCameraIntentForResult();
        }
        else
        {
            startChooseImageIntentForResult();
        }
    }

    private void setFabVisibility()
    {

        if(isOnePlayerChoosed())
        {
            FloatingActionButtonUtil.showFloatingActionButton(fab);
        }
        else
        {
            FloatingActionButtonUtil.hideFloatingActionButton(fab);
        }
    }

    protected boolean isOnePlayerChoosed()
    {
        boolean isOneChooesed = false;
        for (PlayerChooseable playerChooseable: players)
        {
            if(playerChooseable.isChecked)
            {
                isOneChooesed = true;
            }
        }
        return isOneChooesed;
    }

    protected void showConsultationForBackButton()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Hinweis")
                .setMessage("Willst du die ausgewählten Spieler zur Berechnung übernehmen?")
                .setPositiveButton("Übernehmen", (dialog, which) -> showConsulationForSelfPlayer())
                .setNegativeButton("Verwerfen", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .setNeutralButton("Abbrechen", null);
        dialogBuilder.create().show();
    }

    protected void showConsulationForSelfPlayer()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bitte Spieler für 'Meine TTR-Punkte' auswählen");

        List<Player> choosedPlayers = getChoosedPlayers();
        List<String> playerNames = new ArrayList<String>();
        playerNames.add("Keinen auswählen");
        for (Player player: choosedPlayers)
        {
            String fullName = player.getLastname() + ", " + player.getFirstname() + "\n";
            String clubName = player.getClub() != null ? player.getClub() : "";
            String fullNameWithClub = fullName + clubName;
            playerNames.add(fullNameWithClub);
        }

        String[] playerNamesArray = playerNames.toArray(new String[playerNames.size()]);

        builder.setItems(playerNamesArray, (dialog, which) -> {
            Player myTTRPointsPlayer = null;
            List<Player> otherTTRPlayers = getChoosedPlayers();
            switch (which) {
                case 0:
                {
                    break;
                }
                default:
                {
                    int toChangeValue = which - 1;
                    myTTRPointsPlayer = otherTTRPlayers.get(toChangeValue);
                    otherTTRPlayers.remove(toChangeValue);
                    break;
                }
            }
            finishActivityAndReturnResults(otherTTRPlayers, myTTRPointsPlayer);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void finishActivityAndReturnResults(List<Player> players, Player myTTRPointsPlayer)
    {
        Intent intent = new Intent();
        if(myTTRPointsPlayer != null)
        {
            intent.putExtra(TTRCalculatorActivity.PUT_EXTRA_RESULT_IAM_PLAYER, myTTRPointsPlayer);
        }
        if(players != null)
        {
            intent.putExtra(TTRCalculatorActivity.PUT_EXTRA_RESULT_OTHER_PLAYERS, (ArrayList)players);
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
