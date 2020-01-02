package de.ssp.ttr_rechner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;

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
import de.ssp.ttr_rechner.service.caller.ServiceCallerRealNameAndPoints;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

public class TTRechnerActivity extends AppCompatActivity implements ServiceReady<User>
{
    protected @BindView(R.id.txtMeinTTRWert) EditText txtMeinTTRWert;
    protected @BindView(R.id.txtNeueTTRPunkte) TextView txtNeueTTRPunkte;
    protected @BindView(R.id.pnlMatchList) LinearLayout pnlMatchList;
    protected @BindView(R.id.horizontalButtonScrollView) HorizontalScrollView scrButtonView;
    protected @BindView(R.id.txtMeinTTRWertHint) TextInputLayout txtMeinTTRWertHint;
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
                    .setPositiveButton(R.string.menu_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callSettingsActivity(true);
                        }
                    }).setNegativeButton("Ok", null);
            dialogBuilder.create().show();
        }
        return !credentials.isSet();
    }

    @Override
    public void serviceReady(boolean success, User user, String errorMessage)
    {
        if(success && user != null)
        {
            txtMeinTTRWert.setText(String.valueOf(user.getPoints()));
            String realName = user.getRealName() != null && ! user.getRealName().isEmpty() ? " (" + user.getRealName() +")" : "";
            txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte) + realName);
        }
        else
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Anmeldung fehlgeschlagen")
                    .setMessage("Die Anmeldung ist fehlgeschlagen. \nFehler:\n" + errorMessage)
                    .setPositiveButton("Abbrechen", null);
            dialogBuilder.create().show();
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
            addMatchView(null);
            Intent intentForSearchPlayerActivity = new Intent(this, SearchPlayerActivity.class);
            startActivity(intentForSearchPlayerActivity);
        }
    }

    @OnTextChanged(R.id.txtMeinTTRWert)
    public void changeTxtMeinTTRWert(CharSequence text)
    {
        txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte));
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
        final View pnlSingleMatch = getLayoutInflater().inflate(R.layout.ttrechner_single_match, null);
        if(match != null)
        {
            EditText txtTTRGegner = pnlSingleMatch.findViewById(R.id.txtTTRGegner);
            TextInputLayout txtHintTTRGegner = pnlSingleMatch.findViewById(R.id.txtHintTTRGegner);
            Switch chkSieg = pnlSingleMatch.findViewById(R.id.chkSieg);
            if(match.getGegnerischerTTRWert() > 0)
            {
                txtHintTTRGegner.setHint(match.getNameAndVerein());
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

        @OnClick(R.id.btnSearchMatch)
        public void pressBtnSearchMatch(AppCompatImageButton button)
        {
            if(! showCredentialsNotSetIfNecessary())
            {
                // Hier muss die neue Maske aufgehen
            }
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
            String strTTRGegner = txtTTRGegner.getText().toString();

            int intTTRGegner = -1;
            if (!"".equals(strTTRGegner))
            {
                intTTRGegner = Integer.parseInt(strTTRGegner);
            }
            Switch switchMatch = singleMatch.findViewById(R.id.chkSieg);
            Boolean gewonnen = switchMatch.isChecked();

            Match modelMatch = new Match(intTTRGegner, gewonnen, null, null);
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
