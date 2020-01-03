package de.ssp.ttr_rechner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.User;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.ssp.ttr_rechner.model.Match;
import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.model.TTRKonstante;
import de.ssp.ttr_rechner.model.Wettkampf;
import de.ssp.ttr_rechner.rechner.TTRRechnerUtil;
import de.ssp.ttr_rechner.service.ServiceErrorAlertDialogHelper;
import de.ssp.ttr_rechner.service.caller.ServiceCallerRealNameAndPoints;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

public class TTRechnerActivity extends AppCompatActivity implements ServiceReady<User>
{
    public static int REQUEST_CODE_SEARCH = 1;
    public static String PUT_EXTRA_RESULT_PLAYERS = "RESULT_PLAYERS";
    protected @BindView(R.id.txtMeinTTRWert) EditText txtMeinTTRWert;
    protected @BindView(R.id.txtNeueTTRPunkte) TextView txtNeueTTRPunkte;
    protected @BindView(R.id.pnlMatchList) LinearLayout pnlMatchList;
    protected @BindView(R.id.horizontalButtonScrollView) HorizontalScrollView scrButtonView;
    protected @BindView(R.id.txtMeinTTRWertHint) TextInputLayout txtMeinTTRWertHint;
    protected @BindView(R.id.btnCallTTRPoints) Button btnCallTTRPoints;
    protected @BindView(R.id.btnSearchAndAddMatch) Button btnSearchAndAddMatch;
    private Toast anzahlGegnerToast;
    private Wettkampf wettkampf;
    private MyTischtennisCredentials credentials;
    private boolean recalculateNeuerTTRWert;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttrrechner_activity_scrolling);
        anzahlGegnerToast = new Toast(this);
        recalculateNeuerTTRWert = false;
        // erstmal nicht so wichtig
        ButterKnife.bind(this);
        activateToolbar();
        wettkampf = new Wettkampf(this);
        credentials = new MyTischtennisCredentials(this);

        scrButtonView.post(new Runnable() {
            @Override
            public void run() {
                scrButtonView.fullScroll(View.FOCUS_RIGHT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        proccessMyTischtennisLoginPossible(credentials.isMyTischtennisLoginPossible());
        restoreView(wettkampf);
        int angezeigteNeueTTRPunkte = -1;
        try {
            angezeigteNeueTTRPunkte = Integer.valueOf(txtNeueTTRPunkte.getText().toString());
        }
        catch (NumberFormatException e)
        {
            Log.e(this.getLocalClassName(), e.getMessage());
        }
        if(calculateNeueTTRPunkte() != angezeigteNeueTTRPunkte && angezeigteNeueTTRPunkte != -1)
        {
            pressBtnCalculatePoints();
            Toast.makeText(this, "Neue TTR-Punkte wurden neu berechnet.", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.btnCallTTRPoints)
    public void pressBtnCallTTRPoints()
    {
        if(! showCredentialsNotSetIfNecessary())
        {
            txtMeinTTRWert.setText("");
            ServiceCallerRealNameAndPoints realNameAndPointsCaller = new ServiceCallerRealNameAndPoints(this, this);
            realNameAndPointsCaller.callService();
        }
    }

    private boolean showCredentialsNotSetIfNecessary()
    {
        if(! credentials.isSet())
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Anmeldedaten erforderlich")
                    .setMessage("Derzeit wurden noch keine Anmeldedaten hinterlegt. In den Einstellungen kannst du deine Anmeldedaten für myTischtennis hinterlegen!")
                    .setPositiveButton(R.string.menu_settings, (dialog, which) -> callSettingsActivity(true))
                    .setNegativeButton("Deaktivieren", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            proccessMyTischtennisLoginPossible(false);
                            credentials.setCredentials(null, null, false);
                        }
                    });

            dialogBuilder.create().show();
        }
        return !credentials.isSet();
    }

    private void proccessMyTischtennisLoginPossible(boolean isPossible)
    {
        int myTischtennisFunctionVisiblity = isPossible ? TextView.VISIBLE : TextView.GONE;
        btnCallTTRPoints.setVisibility(myTischtennisFunctionVisiblity);
        btnSearchAndAddMatch.setVisibility(myTischtennisFunctionVisiblity);
    }

    @Override
    public void serviceReady(boolean success, User user, String errorMessage)
    {
        if(ServiceErrorAlertDialogHelper.showErrorDialog(this, success, errorMessage))
        {
            return;
        }

        if(user != null)
        {
            txtMeinTTRWert.setText(String.valueOf(user.getPoints()));
            String realName = user.getRealName() != null && ! user.getRealName().isEmpty() ? " (" + user.getRealName() +")" : "";
            txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte) + realName);
            wettkampf.meinName = realName;
        }
    }

    @OnClick(R.id.btnCalculatePoints)
    public void pressBtnCalculatePoints()
    {
        String strAnzeigeErgebnis = "-";
        int intEndergebnis = calculateNeueTTRPunkte();
        if (intEndergebnis > 0)
        {
            strAnzeigeErgebnis = String.valueOf(intEndergebnis);
        }
        txtNeueTTRPunkte.setText(strAnzeigeErgebnis);
    }

    @OnClick(R.id.btnRestore)
    public void pressBtnRestore()
    {
       pnlMatchList.removeAllViews();
       txtMeinTTRWert.setText("");
       this.resetNeueTTRPunkte();
       addMatchView(null);
    }

    @OnClick(R.id.btnSearchAndAddMatch)
    public void pressBtnSearchAndAddMatch()
    {
        if(! showCredentialsNotSetIfNecessary())
        {
            Intent intentForSearchPlayerActivity = new Intent(this, SearchPlayerActivity.class);
            startActivityForResult(intentForSearchPlayerActivity, REQUEST_CODE_SEARCH);
        }
    }

    @OnTextChanged(R.id.txtMeinTTRWert)
    public void changeTxtMeinTTRWert(CharSequence text)
    {
        txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte));
        wettkampf.meinName = null;
        resetNeueTTRPunkte();
    }

    private int calculateNeueTTRPunkte()
    {
        int intEndergebnis = -1;
        int intMeinTTR = getMeinTTR();

        if(intMeinTTR > 0)
        {
            TTRKonstante ttrKonstanteModel = new TTRKonstante(this);
            TTRRechnerUtil calculator = new TTRRechnerUtil(intMeinTTR, ttrKonstanteModel.getTTRKonstante());
            int intAenderungTTR = (int) calculator.berechneTTRAenderung(getMatches());
            intEndergebnis = intMeinTTR + intAenderungTTR;

        }
        return intEndergebnis;
    }

    @OnClick(R.id.btnAddMatch)
    public void pressBtnAddMatch()
    {
        addMatchView(null);
        showToastAnzahlGegner();
    }

    private void addMatchView(Match match)
    {
        final View pnlSingleMatch = getLayoutInflater().inflate(R.layout.tttrechner_match_row_element, null);
        if(match != null)
        {
            EditText txtTTRGegner = pnlSingleMatch.findViewById(R.id.txtTTRGegner);
            TextInputLayout txtHintTTRGegner = pnlSingleMatch.findViewById(R.id.txtHintTTRGegner);
            Switch chkSieg = pnlSingleMatch.findViewById(R.id.chkSieg);
            if(match.getGegnerischerTTRWert() > 0)
            {
                if(match.getNameVerein() != null)
                {
                    txtHintTTRGegner.setHint(match.getNameVerein());
                }
                else
                {
                    txtHintTTRGegner.setHint(getString(R.string.hint_ttr_gegner));
                }

                txtTTRGegner.setText(String.valueOf(match.getGegnerischerTTRWert()));
            }
            chkSieg.setChecked(match.isGewonnen());
        }

        new PnlSingleMatchViewHolder(pnlSingleMatch);
        pnlMatchList.addView(pnlSingleMatch);
        updateRemoveButton();
    }

    class PnlSingleMatchViewHolder
    {
        protected @BindView(R.id.pnlMatchView) LinearLayout pnlSingleMatchView;
        protected @BindView(R.id.txtHintTTRGegner) TextInputLayout txtHintTTRGegner;
        PnlSingleMatchViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.btnRemoveMatch)
        public void pressBtnRemoveMatch(AppCompatImageButton button)
        {
            TTRechnerActivity.this.pnlMatchList.removeView(pnlSingleMatchView);
            updateRemoveButton();
            showToastAnzahlGegner();
            resetNeueTTRPunkte();
        }

        @OnCheckedChanged(R.id.chkSieg)
        public void changeChkSieg(CompoundButton switchButton, boolean checked)
        {
            resetNeueTTRPunkte();
        }

        @OnTextChanged(R.id.txtTTRGegner)
        public void changeTxtTTRGegner(CharSequence text)
        {
            txtHintTTRGegner.setHint(getString(R.string.hint_ttr_gegner));
            resetNeueTTRPunkte();
        }
    }

    private void restoreView(Wettkampf wettkampf)
    {
        Log.d(TTRechnerActivity.class.toString(), "Löschen aller MatchViews");

        pnlMatchList.removeAllViews();
        for(Match match : wettkampf.matches)
        {
            addMatchView(match);
        }
        if(pnlMatchList.getChildCount() == 0)
        {
            addMatchView(null);
        }
        if(wettkampf.meinTTRWert > 0)
        {
            String realName = wettkampf.meinName != null && ! wettkampf.meinName.isEmpty() ? " (" + wettkampf.meinName +")" : "";
            txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte) + realName);
            txtMeinTTRWert.setText(String.valueOf(wettkampf.meinTTRWert));
        }
    }

    private void updateRemoveButton()
    {
        if(pnlMatchList.getChildCount() > 1)
        {
            for (int i = 0; i < pnlMatchList.getChildCount(); i++)
            {
                pnlMatchList.getChildAt(i).findViewById(R.id.btnRemoveMatch).setVisibility(View.VISIBLE);
            }
        }
        if(pnlMatchList.getChildCount() == 1)
        {
            pnlMatchList.getChildAt(0).findViewById(R.id.btnRemoveMatch).setVisibility(View.INVISIBLE);
        }
    }

    private void showToastAnzahlGegner()
    {
        if(anzahlGegnerToast != null)
        {
            anzahlGegnerToast.cancel();
        }
        anzahlGegnerToast = Toast.makeText(this, "Anzahl Gegner: " + pnlMatchList.getChildCount(), Toast.LENGTH_SHORT);
        anzahlGegnerToast.show();
    }

    private void activateToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private int getMeinTTR()
    {
        String strMeinTTRWert = txtMeinTTRWert.getText().toString();

        if (! "".equals(strMeinTTRWert))
        {
            return Integer.parseInt(strMeinTTRWert);
        }
        return -1;
    }

    private ArrayList<Match> getMatches()
    {
        int count = pnlMatchList.getChildCount();
        ArrayList<Match> matches = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            LinearLayout singleMatch = (LinearLayout) pnlMatchList.getChildAt(i);

            EditText txtTTRGegner = singleMatch.findViewById(R.id.txtTTRGegner);
            TextInputLayout txtHintTTRGegner = singleMatch.findViewById(R.id.txtHintTTRGegner);
            String strTTRGegner = txtTTRGegner.getText().toString();

            int intTTRGegner = -1;
            if (!"".equals(strTTRGegner))
            {
                intTTRGegner = Integer.parseInt(strTTRGegner);
            }
            Switch switchMatch = singleMatch.findViewById(R.id.chkSieg);
            Boolean gewonnen = switchMatch.isChecked();
            String nameVerein = txtHintTTRGegner.getHint() != null ? txtHintTTRGegner.getHint().toString() : null;
            Match modelMatch = new Match(intTTRGegner, gewonnen, nameVerein);
            matches.add(modelMatch);
        }
        return matches;
    }

    @Override
    protected void onPause() {
        super.onPause();
        wettkampf.save(getMatches(),getMeinTTR());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflatet das Menü; Dies fügt Menüpunkte hinzu, wenn das Menü dargestellt wird
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_call_ttr_konstante)
        {
            callSettingsActivity(false);
            return true;
        }
        if (id == R.id.action_call_licence)
        {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.licence));
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnTTRSettings)
    public void pressBtnTTRSettings()
    {
        callSettingsActivity(false);
    }

    private void callSettingsActivity(boolean focusOnCredentials)
    {
        recalculateNeuerTTRWert = true;
        Intent intentForTTRKonstanteActivity = new Intent(this, SettingsActivity.class);
        intentForTTRKonstanteActivity.putExtra(MyTischtennisCredentials.FOCUS_ON_CREDENTIALS, focusOnCredentials);
        startActivity(intentForTTRKonstanteActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_CODE_SEARCH && resultCode == RESULT_OK)
        {
            ArrayList<Player> playerArrayList = (ArrayList<Player>) intent.getSerializableExtra(PUT_EXTRA_RESULT_PLAYERS);

            if(wettkampf.matches.size() == 1 && wettkampf.matches.get(0).getGegnerischerTTRWert() == -1 && playerArrayList.size() > 0)
            {
                wettkampf.matches.remove(0);
            }

            for (Player player: playerArrayList)
            {
                Match match = new Match(player.getTtrPoints(), false, player.getFirstname() + " " + player.getLastname(), player.getClub());
                wettkampf.matches.add(match);
            }
        }

    }

    private void resetNeueTTRPunkte()
    {
        txtNeueTTRPunkte.setText("-");
        if(recalculateNeuerTTRWert)
        {
            pressBtnCalculatePoints();
            recalculateNeuerTTRWert = false;
        }
    }
}
