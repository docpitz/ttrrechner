package de.ssp.ttr_rechner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.User;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import de.ssp.ttr_rechner.model.Match;
import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.model.TTRKonstante;
import de.ssp.ttr_rechner.model.Wettkampf;
import de.ssp.ttr_rechner.rechner.TTRRechnerUtil;
import de.ssp.ttr_rechner.service.ServiceErrorAlertDialogHelper;
import de.ssp.ttr_rechner.service.caller.ServiceCallerIsPremiumAccount;
import de.ssp.ttr_rechner.service.caller.ServiceCallerRealNameAndPoints;
import de.ssp.ttr_rechner.service.caller.ServiceFinish;
import de.ssp.ttr_rechner.ui.calculator.PanelMatchViewHolder;
import de.ssp.ttr_rechner.ui.calculator.PanelSingleMatchViewHolder;
import de.ssp.ttr_rechner.ui.calculator.TTRCalculatorInteractor;
import de.ssp.ttr_rechner.ui.util.TTRAnimationsUtils;

public class TTRCalculatorActivity extends AppCompatActivity implements TTRCalculatorInteractor
{
    public static int REQUEST_CODE_SEARCH = 1;
    public static int REQUEST_CODE_SETTINGS = 2;
    public static String PUT_EXTRA_RESULT_PLAYERS = "RESULT_PLAYERS";

    protected @BindView(R.id.txtMeinTTRWert) EditText txtMeinTTRWert;
    protected @BindView(R.id.txtNeueTTRPunkte) TextView txtNeueTTRPunkte;
    protected @BindView(R.id.txtDiffTTRPunkte) TextView txtDiffTTRPunkte;
    protected @BindView(R.id.txtMeinTTRWertHint) TextInputLayout txtMeinTTRWertHint;
    protected @BindView(R.id.pnlPunkte) LinearLayout pnlPunkte;
    protected @BindView(R.id.horizontalButtonScrollView) HorizontalScrollView scrButtonView;
    protected @BindView(R.id.btnCallMyTTRPoints) Button btnCallMyTTRPoints;
    protected @BindView(R.id.btnSearchDetailPlayers) Button btnSearchPlayersDetail;
    protected @BindView(R.id.btnSearchFastPlayers) Button btnSearchPlayersFast;
    protected @BindView(R.id.btnCalculatePoints) FloatingActionButton btnCalculatePoints;
    protected @BindView(R.id.btnSearchForMyTTRPoints) Button btnSearchForMyTTRPoints;
    protected @BindView(R.id.toolbar) Toolbar toolbar;

    private Toast anzahlGegnerToast;
    private Wettkampf wettkampf;
    private MyTischtennisCredentials credentials;
    private PanelMatchViewHolder panelMatchViewHolder;
    private boolean isPremiumAccountCalled = false;

    public class ServiceFinishUser implements ServiceFinish<Void, User>
    {
        @Override
        public void serviceFinished(Void returnValue, boolean success, User user, String errorMessage)
        {
            if(ServiceErrorAlertDialogHelper.showErrorDialog(TTRCalculatorActivity.this, success, errorMessage))
            {
                return;
            }

            if(user != null)
            {
                setMyTTRPunkteToView(user.getPoints(), user.getRealName());
            }
        }
    }

    public class ServiceFinishIsPremiumAccountForDetailSearchPlayerActivity implements ServiceFinish<Void, Boolean>
    {
        protected boolean isSingleChooseActive;
        public ServiceFinishIsPremiumAccountForDetailSearchPlayerActivity(boolean isSingleChooseActive)
        {
            this.isSingleChooseActive = isSingleChooseActive;
        }

        @Override
        public void serviceFinished(Void returnValue, boolean success, Boolean value, String errorMessage)
        {
            if(ServiceErrorAlertDialogHelper.showErrorDialog(TTRCalculatorActivity.this, success, errorMessage))
            {
                return;
            }

            if(success && errorMessage == null)
            {
                isPremiumAccountCalled = true;
                credentials.setIsPremiumAccount(value);
                proccessMyTischtennisLoginPossible(credentials.isMyTischtennisLoginPossible(), credentials.isPremiumAccount());
                callSearchPlayerDetailActivity(isSingleChooseActive);
            }
        }
    }

    public class ServiceFinishIsPremiumAccountForFastSearchPlayerActivity implements ServiceFinish<Void, Boolean>
    {
        protected boolean isSingleChooseActive;
        public ServiceFinishIsPremiumAccountForFastSearchPlayerActivity(boolean isSingleChooseActive)
        {
            this.isSingleChooseActive = isSingleChooseActive;
        }

        @Override
        public void serviceFinished(Void returnValue, boolean success, Boolean value, String errorMessage)
        {
            if(ServiceErrorAlertDialogHelper.showErrorDialog(TTRCalculatorActivity.this, success, errorMessage))
            {
                return;
            }

            if(success && errorMessage == null)
            {
                isPremiumAccountCalled = true;
                credentials.setIsPremiumAccount(value);
                proccessMyTischtennisLoginPossible(credentials.isMyTischtennisLoginPossible(), credentials.isPremiumAccount());
                callSearchPlayerFastActivity(isSingleChooseActive);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttrrechner_activity_scrolling);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        anzahlGegnerToast = new Toast(this);
        wettkampf = new Wettkampf(this);
        credentials = new MyTischtennisCredentials(this);
        panelMatchViewHolder = new PanelMatchViewHolder(findViewById(R.id.pnlMatchList));

        scrButtonView.post(() -> scrButtonView.fullScroll(View.FOCUS_RIGHT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        proccessMyTischtennisLoginPossible(credentials.isMyTischtennisLoginPossible(), credentials.isPremiumAccount());
        restoreView(wettkampf);
        txtNeueTTRPunkte.setText(R.string.leer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wettkampf.save(panelMatchViewHolder.getMatches(),getMeinTTR());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_CODE_SEARCH && resultCode == RESULT_OK)
        {
            onActivityResultSearch(intent);
        }
        else if(requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_OK)
        {
            onActivityResultMyTischtennisCredentials(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.ttrcalculator_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_call_mytischtennis_credentials)
        {
            callMyTischtennisCredentialsActivity(false);
            return true;
        }
        if (id == R.id.action_call_ttr_konstante)
        {
            callTTRConstantsActivity();
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

    @OnFocusChange(R.id.txtMeinTTRWert)
    public void changeTxtMeinTTRWert(AppCompatEditText editText)
    {
        int actualValue = getMeinTTR();
        int oldValue = wettkampf.meinTTRWert;
        if(actualValue != oldValue)
        {
            txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte));
            wettkampf.meinName = null;
            resetNeueTTRPunkte();
        }
    }

    @OnClick(R.id.btnCallMyTTRPoints)
    public void pressBtnCallTTRPoints(Button button)
    {
        TTRAnimationsUtils.standardAnimationButtonPress(button);

        if(! showCredentialsNotSetIfNecessary())
        {
            txtMeinTTRWert.setText(null);
            txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte));
            ServiceCallerRealNameAndPoints realNameAndPointsCaller = new ServiceCallerRealNameAndPoints(this, new ServiceFinishUser());
            realNameAndPointsCaller.callService();
        }
    }

    @OnClick(R.id.btnSearchForMyTTRPoints)
    public void pressBtnSearchForMyTTRPoints(Button button)
    {
        TTRAnimationsUtils.standardAnimationButtonPress(button);

        if(! showCredentialsNotSetIfNecessary() && ! callServiceIsPremiumAccountIfNeccessaryForNormalSearch(true))
        {
            callSearchPlayerDetailActivity(true);
        }
    }

    @OnClick(R.id.btnMyTischtennisCredentials)
    public void pressBtnMyTischtennisCredentials()
    {
        callMyTischtennisCredentialsActivity(false);
    }

    @OnClick(R.id.btnTTRKonstant)
    public void pressBtnTTRConstants()
    {
        callTTRConstantsActivity();
    }

    @OnClick(R.id.btnReset)
    public void pressBtnReset()
    {
       txtMeinTTRWert.setText(null);
       txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte));
       resetNeueTTRPunkte();
       panelMatchViewHolder.reset(this, this);
    }

    @OnClick(R.id.btnSearchFastPlayers)
    public void pressBtnSearchFast()
    {
        if(! showCredentialsNotSetIfNecessary() && ! callServiceIsPremiumAccountIfNeccessaryForFastSearch(false))
        {
            callSearchPlayerFastActivity(false);
        }
    }

    @OnClick(R.id.btnSearchDetailPlayers)
    public void pressBtnSearchDetail()
    {
        if(! showCredentialsNotSetIfNecessary() && ! callServiceIsPremiumAccountIfNeccessaryForNormalSearch(false))
        {
            callSearchPlayerDetailActivity(false);
        }
    }

    @OnClick(R.id.btnAddMatch)
    public void pressBtnAddMatch()
    {
        wettkampf.matches.add(new Match(Match.NO_MATCH, false, null));
        panelMatchViewHolder.addMatch(null, this, this);
        showToastAnzahlGegner();
    }

    @OnClick(R.id.btnCalculatePoints)
    public void pressBtnCalculatePoints()
    {
        YoYo.with(Techniques.Pulse)
                .duration(800)
                .playOn(pnlPunkte);

        TTRRechnerUtil ttrRechnerUtil = initalizeTTRRechnerUtil();
        if (ttrRechnerUtil != null)
        {
            txtNeueTTRPunkte.setText(String.valueOf(ttrRechnerUtil.berechneNeueTTRPunkte()));
            txtDiffTTRPunkte.setText(getTextDiffTTRPunkteChange(ttrRechnerUtil.berechneTTRAenderung()));
        }
        else
        {
            resetNeueTTRPunkte();
        }
    }

    @Override
    public void showToastAnzahlGegner()
    {
        if(anzahlGegnerToast != null)
        {
            anzahlGegnerToast.cancel();
        }
        anzahlGegnerToast = Toast.makeText(this, "Anzahl Gegner: " + wettkampf.matches.size(), Toast.LENGTH_SHORT);
        anzahlGegnerToast.show();
    }

    @Override
    public void removeMatch(PanelSingleMatchViewHolder panelSingleMatchViewHolder)
    {
        panelMatchViewHolder.removeMatch(panelSingleMatchViewHolder);
        wettkampf.matches = panelMatchViewHolder.getMatches();
        showToastAnzahlGegner();
    }

    @Override
    public void resetNeueTTRPunkte()
    {
        txtDiffTTRPunkte.setText("");
        txtNeueTTRPunkte.setText("-");
    }

    private void setMyTTRPunkteToView(int ttrPoints, String username)
    {
        wettkampf.meinName = username != null ? username : "";
        wettkampf.meinTTRWert = ttrPoints;
        String realName = wettkampf.meinName != null && ! wettkampf.meinName.isEmpty() ? " (" + wettkampf.meinName +")" : "";
        txtMeinTTRWert.setText(String.valueOf(wettkampf.meinTTRWert));
        txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte) + realName);
    }

    private void callMyTischtennisCredentialsActivity(boolean focusOnCredentials)
    {
        Intent intentForTTRKonstanteActivity = new Intent(this, MyTischtennisCredentialsActivity.class);
        intentForTTRKonstanteActivity.putExtra(MyTischtennisCredentials.FOCUS_ON_CREDENTIALS, focusOnCredentials);
        startActivityForResult(intentForTTRKonstanteActivity, REQUEST_CODE_SETTINGS);
    }

    private void callTTRConstantsActivity()
    {
        Intent intentForTTRKonstanteActivity = new Intent(this, TTRConstantsActivity.class);
        startActivity(intentForTTRKonstanteActivity);
    }

    private void callSearchPlayerFastActivity(boolean isSingleChooseActive)
    {
        if(credentials.isPremiumAccount()) {
            Intent intentForSearchPlayerActivity = new Intent(this, SearchPlayerFastActivity.class);
            intentForSearchPlayerActivity.putExtra(FoundedPlayerActivity.PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, isSingleChooseActive);
            startActivityForResult(intentForSearchPlayerActivity, REQUEST_CODE_SEARCH);
        }
        else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Premium-Account erforderlich")
                    .setMessage("Diese Funktion steht leider nur myTischtennis-Usern mit Premuim-Account zur Verfügung. In MyTischtennis kann man ohne Premium-Account nicht nach Namen suchen.")
                    .setPositiveButton("ok", null);
            dialogBuilder.create().show();
        }
    }

    private void callSearchPlayerDetailActivity(boolean isSingleChooseActive)
    {
        Intent intentForSearchPlayerActivity = new Intent(this, SearchPlayerDetailActivity.class);
        intentForSearchPlayerActivity.putExtra(FoundedPlayerActivity.PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, isSingleChooseActive);
        intentForSearchPlayerActivity.putExtra(SearchPlayerDetailActivity.PUT_EXTRA_IS_PREMIUM_ACCOUNT, credentials.isPremiumAccount());
        startActivityForResult(intentForSearchPlayerActivity, REQUEST_CODE_SEARCH);
    }

    private boolean showCredentialsNotSetIfNecessary()
    {
        if(! credentials.isSet())
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Anmeldedaten erforderlich")
                    .setMessage("Derzeit wurden noch keine Anmeldedaten hinterlegt. In den Einstellungen kannst du deine Anmeldedaten für myTischtennis hinterlegen!")
                    .setPositiveButton(R.string.menu_settings, (dialog, which) -> callMyTischtennisCredentialsActivity(true))
                    .setNegativeButton("Deaktivieren", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            proccessMyTischtennisLoginPossible(false, false);
                            credentials.setCredentials(null, null, false);
                        }
                    });

            dialogBuilder.create().show();
        }
        return !credentials.isSet();
    }

    private void proccessMyTischtennisLoginPossible(boolean isPossible, boolean isPremiumAccount)
    {
        int myTischtennisFunctionVisiblity = isPossible ? TextView.VISIBLE : TextView.GONE;
        btnCallMyTTRPoints.setVisibility(myTischtennisFunctionVisiblity);
        btnSearchPlayersDetail.setVisibility(myTischtennisFunctionVisiblity);
        btnSearchForMyTTRPoints.setVisibility(myTischtennisFunctionVisiblity);

        int myTischtennisPremiumFunction = isPremiumAccount && isPossible ? TextView.VISIBLE : TextView.GONE;
        btnSearchPlayersFast.setVisibility(myTischtennisPremiumFunction);
    }

    private int getMeinTTR()
    {
        String strMeinTTRWert = txtMeinTTRWert.getText().toString();

        if (strMeinTTRWert.isEmpty())
        {
            return Match.NO_MATCH;
        }
        return Integer.parseInt(strMeinTTRWert);
    }

    private String getTextDiffTTRPunkteChange(long ttrAenderung)
    {
        String plus = "";
        if(ttrAenderung >= 0)
        {
            plus = "+";
        }
        return "(" + plus + ttrAenderung + ")";
    }

    private TTRRechnerUtil initalizeTTRRechnerUtil()
    {
        int intMeinTTR = getMeinTTR();

        if(intMeinTTR > 0)
        {
            TTRKonstante ttrKonstanteModel = new TTRKonstante(this);
            TTRRechnerUtil calculator = new TTRRechnerUtil(intMeinTTR, ttrKonstanteModel.getTTRKonstante(),panelMatchViewHolder.getMatches());
            return calculator;
        }
        return null;
    }

    private void restoreView(Wettkampf wettkampf)
    {
        Log.d(TTRCalculatorActivity.class.toString(), "Löschen aller MatchViews");
        panelMatchViewHolder.restoreView(wettkampf.matches, this, this);

        if(wettkampf.meinTTRWert > 0)
        {
            String realName = wettkampf.meinName != null && ! wettkampf.meinName.isEmpty() ? " - " + wettkampf.meinName : "";
            txtMeinTTRWertHint.setHint(getString(R.string.hint_meine_ttr_punkte) + realName);
            txtMeinTTRWert.setText(String.valueOf(wettkampf.meinTTRWert));
        }
    }

    private boolean callServiceIsPremiumAccountIfNeccessaryForNormalSearch(boolean isSingleChooseActive)
    {
        if(! isPremiumAccountCalled)
        {
            ServiceCallerIsPremiumAccount serviceCallerIsPremiumAccount = new ServiceCallerIsPremiumAccount(this, new ServiceFinishIsPremiumAccountForDetailSearchPlayerActivity(isSingleChooseActive));
            serviceCallerIsPremiumAccount.callService();
            return true;
        }
        return false;
    }

    private boolean callServiceIsPremiumAccountIfNeccessaryForFastSearch(boolean isSingleChooseActive)
    {
        if(! isPremiumAccountCalled)
        {
            ServiceCallerIsPremiumAccount serviceCallerIsPremiumAccount = new ServiceCallerIsPremiumAccount(this, new ServiceFinishIsPremiumAccountForFastSearchPlayerActivity(isSingleChooseActive));
            serviceCallerIsPremiumAccount.callService();
            return true;
        }
        return false;
    }

    private void onActivityResultSearch(Intent intent)
    {
        ArrayList<Player> playerArrayList = (ArrayList<Player>) intent.getSerializableExtra(PUT_EXTRA_RESULT_PLAYERS);
        boolean isMyTTRPunkteCalled = intent.getBooleanExtra(FoundedPlayerActivity.PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, false);
        if(isMyTTRPunkteCalled)
        {
            if(playerArrayList.size() == 1)
            {
                Player player = playerArrayList.get(0);
                setMyTTRPunkteToView(player.getTtrPoints(), player.getFirstname() + " " + player.getLastname());
            }
        }
        else
        {
            wettkampf.addMatches(playerArrayList);
            showToastAnzahlGegner();
        }
    }

    private void onActivityResultMyTischtennisCredentials(Intent intent)
    {
        boolean isLoginDataChanged = intent.getBooleanExtra(MyTischtennisCredentialsActivity.PUT_EXTRA_IS_LOGIN_DATA_CHANGED, true);
        if(isLoginDataChanged)
        {
            isPremiumAccountCalled = false;
            proccessMyTischtennisLoginPossible(credentials.isMyTischtennisLoginPossible(), credentials.isPremiumAccount());
        }
    }
}
