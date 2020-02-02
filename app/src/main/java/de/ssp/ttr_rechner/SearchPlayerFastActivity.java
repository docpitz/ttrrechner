package de.ssp.ttr_rechner;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmelzer.myttr.Player;
import com.robertlevonyan.views.chip.Chip;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.ssp.ttr_rechner.model.ChooseablePlayer;
import de.ssp.ttr_rechner.ui.foundedplayer.FoundedPlayersListAdapter;
import de.ssp.ttr_rechner.ui.foundedplayer.PlayersImageLoader;
import de.ssp.ttr_rechner.ui.foundedplayer.SearchPlayerFastPresenter;
import de.ssp.ttr_rechner.ui.foundedplayer.SearchPlayerFastView;
import de.ssp.ttr_rechner.ui.util.FloatingActionButtonUtil;

public class SearchPlayerFastActivity extends AppCompatActivity implements SearchPlayerFastView
{
    public enum ViewState
    {
        QUERY_TOO_SMALL,
        SERVICE_IS_RUNNING,
        TOO_MUCH_DATA,
        ERROR,
        SHOW_RESULTS
    }
    public static String PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV = "IS_SINGLE_CHOOSE_ACTIV";

    protected FoundedPlayersListAdapter listAdapter;
    protected @BindView(R.id.listPlayer) ListView listViewPlayer;
    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.fab) FloatingActionButton floatingActionButton;
    protected @BindView(R.id.pnlPlayersList) LinearLayout pnlPlayersList;
    protected @BindView(R.id.horizontalButtonScrollView) HorizontalScrollView horizontalScrollView;
    protected @BindView(R.id.empty_view) LinearLayout pnlEmptyView;
    protected @BindView(R.id.txtHinweistext) TextView txtHinweistext;
    protected @BindView(R.id.imgSearching) ImageView imgSearching;
    protected @BindView(R.id.pnlImageView) LinearLayout pnlImageView;
    protected SearchPlayerFastPresenter presenter;

    protected SearchView txtSearchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fastsearch_activity);
        ButterKnife.bind(this);
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean isSingleChooseActiv = getIntent().getBooleanExtra(PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, false);
        FloatingActionButtonUtil.hideFloatingActionButton(floatingActionButton);
        presenter = new SearchPlayerFastPresenter(this, this, isSingleChooseActiv);

        listAdapter = new FoundedPlayersListAdapter(this, new ChooseablePlayer[]{}, true, null);
        listViewPlayer.setAdapter(listAdapter);
        listViewPlayer.setItemsCanFocus(true);
        listViewPlayer.setEmptyView(pnlEmptyView);
        imgSearching.setImageDrawable(PlayersImageLoader.getNewCircularProgressDrawable(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fastsearch_menu, menu);
        MenuItem appBarSearch = menu.findItem(R.id.app_bar_search);
        txtSearchField = (SearchView) MenuItemCompat.getActionView(appBarSearch);
        txtSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                txtSearchField.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                presenter.changeQuery(query);
                return false;
            }
        });

        // Trick um die Searchbar mit leichter Verspätung damit alles klappt
        YoYo.with(Techniques.Pulse)
                .duration(1)
                .onEnd(animator -> menu.performIdentifierAction(R.id.app_bar_search, 0))
                .playOn(appBarSearch.getActionView());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem actionRestart = (MenuItem) findViewById(R.id.app_bar_search);
        //onOptionsItemSelected(actionRestart);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnItemClick(R.id.listPlayer)
    public void choosePlayer(AdapterView<?> parent, View view, int position, long id)
    {
        presenter.addChoosedPlayer(listAdapter.getItem(position));
        horizontalScrollView.postDelayed(() -> horizontalScrollView.fullScroll(View.FOCUS_RIGHT), 290);
    }

    @OnClick(R.id.fab)
    public void onPressedDone(View view)
    {
        presenter.pressedDone();
    }

    public void addChip(ChooseablePlayer chooseablePlayer)
    {
        Player player = chooseablePlayer.player;
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.fastsearch_choosen_player, null);
        chip.setClosable(true);
        chip.setChipBackgroundColor(getResources().getColor(R.color.white));
        chip.setText(player.getFirstname() + " " + player.getLastname() + ": " + player.getTtrPoints());
        chip.setTag(chooseablePlayer);
        chip.setChipIcon(chooseablePlayer.playersAvatar instanceof BitmapDrawable ? chooseablePlayer.playersAvatar : null);
        chip.setOnCloseClickListener(v -> {
            pnlPlayersList.removeView(chip);
            presenter.removeChoosedPlayer(chooseablePlayer);
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chip.setLayoutParams(params);
        pnlPlayersList.addView(chip);
        final ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams) chip.getLayoutParams();
        lpt.setMargins(5,5,5,5);
    }

    @Override
    public void showErrorMessage(String errorMessage)
    {
        Log.d(this.getClass().toString(), "showErrorMessage");
        clearPlayerList();
        showText(errorMessage);
    }

    public void showFloatingActionButton(boolean show)
    {
        if(show) {
            FloatingActionButtonUtil.showFloatingActionButton(floatingActionButton);
        }
        else {
            FloatingActionButtonUtil.hideFloatingActionButton(floatingActionButton);
        }
    }

    @Override
    public void showResults(ArrayList<Player> playerList, String searchedQuery)
    {
        Log.d(this.getClass().toString(), "showResults");
        String actualQuery = txtSearchField.getQuery().toString();
        if(actualQuery.equals(searchedQuery))
        {
            if(playerList.size() > 0)
            {
                listAdapter.updateData(ChooseablePlayer.getPlayers(ChooseablePlayer.convertFromPlayers(playerList)));
                listAdapter.getFilter().filter(searchedQuery);
                showText("Keine Spieler gefunden...");
            }
            else
            {
                clearPlayerList();
                showText("Keine Spieler gefunden...");
            }
        }
        else
        {
            presenter.changeQuery(actualQuery);
        }
    }

    @Override
    public void showMessageMoreSearchWords()
    {
        Log.d(this.getClass().toString(), "showMessageMoreSearchWords");
        clearPlayerList();
        txtHinweistext.setText(R.string.bitte_mit_vorname_nachname_suchen);
        pnlImageView.setVisibility(View.GONE);
    }

    @Override
    public void showServiceIsRunning()
    {
        Log.d(this.getClass().toString(), "showServiceIsRunning");
        clearPlayerList();
        showTextServiceSearching();
    }

    @Override
    public void resetView()
    {
        Log.d(this.getClass().toString(), "resetView");
        txtSearchField.setQuery("", false);
        listAdapter.updateData(new ChooseablePlayer[]{});
        showMessageMoreSearchWords();
    }

    @Override
    public void finishActivity(Intent intent)
    {
        setResult(RESULT_OK, intent);
        finish();
    }

    private void clearPlayerList()
    {
        listAdapter.updateData(new ChooseablePlayer[]{});
        listAdapter.notifyDataSetChanged();
    }

    private void showText(String text)
    {
        imgSearching.setImageDrawable(null);
        pnlImageView.setVisibility(View.GONE);
        txtHinweistext.setText(text);
    }

    private void showTextServiceSearching()
    {
        txtHinweistext.setText(R.string.suche_laeuft);
        imgSearching.setImageDrawable(PlayersImageLoader.getNewCircularProgressDrawable(this));
        pnlImageView.setVisibility(View.VISIBLE);
    }

}

