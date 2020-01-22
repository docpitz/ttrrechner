package de.ssp.ttr_rechner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.jmelzer.myttr.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.ssp.ttr_rechner.model.Alter;
import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.model.TTRKonstante;
import de.ssp.ttr_rechner.service.caller.ServiceCallerLogin;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

public class SettingsActivity extends AppCompatActivity implements ServiceReady<User>
{
    public static String PUT_EXTRA_IS_LOGIN_DATA_CHANGED = "IS_LOGIN_DATA_CHANGED";

    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.chkKonstante1JahrOhneSpiel) Switch chkKonstante1JahrOhneSpiel;
    protected @BindView(R.id.chkKonstanteWeniger15Spiele) Switch chkKonstanteWeniger15Spiele;
    protected @BindView(R.id.rbUnter16) RadioButton rbUnter16;
    protected @BindView(R.id.rbUnter21) RadioButton rbUnter21;
    protected @BindView(R.id.rbUeber21) RadioButton rbUeber21;
    protected @BindView(R.id.txtTTRKonstante) TextView txtTTRKonstante;
    protected @BindView(R.id.txtUsername) EditText txtUsername;
    protected @BindView(R.id.txtPassword) EditText txtPassword;
    protected @BindView(R.id.chkMyttLoginPossible) Switch chkMyttLoginPossible;
    protected @BindView(R.id.pnlLogin) LinearLayout pnlLogin;
    protected @BindView(R.id.chkShowPlayersImage) Switch chkShowPlayersImage;
    protected TTRKonstante ttrKonstanteModel;
    protected MyTischtennisCredentials myTischtennisCredentialsModel;
    protected boolean startIsFinished = false;
    protected boolean isLoginDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ButterKnife.bind(this);

        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeTTRKonstante();
        initializeMyTischtennisCredentials();
        if(getIntent().getBooleanExtra(MyTischtennisCredentials.FOCUS_ON_CREDENTIALS, false))
        {
            txtUsername.requestFocus();
        }
        startIsFinished = true;
        isLoginDataChanged = false;
    }

    @Override
    public void onBackPressed()
    {
        finishActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnCheckedChanged({R.id.chkKonstanteWeniger15Spiele, R.id.chkKonstante1JahrOhneSpiel})
    public void change15SpieleUnd1JahrOhneSpielSwitch(CompoundButton switchButton, boolean checked)
    {
        if(! startIsFinished) return;

        ttrKonstanteModel.setUeberEinJahrOhneSpiel(chkKonstante1JahrOhneSpiel.isChecked());
        ttrKonstanteModel.setWenigerAls15Spiele(chkKonstanteWeniger15Spiele.isChecked());
        updateTTRKonstante();
    }

    @OnCheckedChanged(R.id.rbUnter16)
    public void toggleUnter16(CompoundButton button, boolean checked)
    {
        changeAlter(Alter.UNTER_16, checked);
    }

    @OnCheckedChanged(R.id.rbUnter21)
    public void toggleUnter18(CompoundButton button, boolean checked)
    {
        changeAlter(Alter.UNTER_21, checked);
    }

    @OnCheckedChanged(R.id.rbUeber21)
    public void toggleUeber18(CompoundButton button, boolean checked)
    {
        changeAlter(Alter.UEBER_21, checked);
    }

    @OnCheckedChanged(R.id.chkShowPlayersImage)
    public void changeShowPlayersImage(CompoundButton switchButton, boolean checked)
    {
        if(! startIsFinished) return;

        if(checked)
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Hoher Datenverbrauch")
                    .setMessage("Die Anzeige von Spielerbildern führt zu einem hohen Datenverbrauch. Bisher haben nur wenige Spieler bisher ein Bild eingestellt.")
                    .setPositiveButton("Aktivieren", (dialog, which) -> myTischtennisCredentialsModel.setPlayersImageShow(true))
                    .setNegativeButton("Abbrechen", (dialog, which) -> chkShowPlayersImage.setChecked(false));
            dialogBuilder.show();
        }
        else
        {
            myTischtennisCredentialsModel.setPlayersImageShow(false);
        }
    }

    @OnCheckedChanged(R.id.chkMyttLoginPossible)
    public void toggleMyTTLoginPossible(CompoundButton button, boolean checked)
    {
        if(! startIsFinished) return;

        pnlLogin.setVisibility(checked ? TextView.VISIBLE : TextView.INVISIBLE);
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();

        myTischtennisCredentialsModel.setCredentials(null, null, checked);
    }

    @OnClick (R.id.btnLogin)
    public void pressBtnTestLogin()
    {
        isLoginDataChanged = true;
        ServiceCallerLogin loginServiceCaller = new ServiceCallerLogin(this, this, txtUsername.getText().toString(), txtPassword.getText().toString());
        loginServiceCaller.callService();
    }

    @OnClick(R.id.btnLoginDelete)
    public void pressBtnDeleteLogin()
    {
        isLoginDataChanged = true;
        txtUsername.setText("");
        txtPassword.setText("");
        myTischtennisCredentialsModel.setCredentials(null, null, chkMyttLoginPossible.isChecked());
    }

    @Override
    public void serviceReady(boolean success, User user, String errorMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if(user != null)
        {
            myTischtennisCredentialsModel.setCredentials(user.getUsername(), user.getPassword(), chkMyttLoginPossible.isChecked());
            dialogBuilder.setTitle("Anmeldung erfolgreich")
                    .setMessage("Die Anmeldung war erfolgreich, ihre Login-Daten wurden gespeichert!")
                    .setPositiveButton("Ok", null);

        }
        else
        {
            myTischtennisCredentialsModel.setCredentials(null, null, chkMyttLoginPossible.isChecked());
            dialogBuilder.setTitle("Anmeldung fehlgeschlagen")
                    .setMessage("Die Anmeldung ist fehlgeschlagen, ihre Login-Daten wurden gelöscht!" +
                            "\n\nFehler: \n" + errorMessage)
                    .setNegativeButton("Ok", null);
        }
        dialogBuilder.create().show();
    }

    private void initializeMyTischtennisCredentials()
    {
        myTischtennisCredentialsModel = new MyTischtennisCredentials(this);
        pnlLogin.setVisibility(myTischtennisCredentialsModel.isMyTischtennisLoginPossible() ? TextView.VISIBLE : TextView.INVISIBLE);
        chkMyttLoginPossible.setChecked(myTischtennisCredentialsModel.isMyTischtennisLoginPossible());
        txtUsername.setText(myTischtennisCredentialsModel.getUsername());
        txtPassword.setText(myTischtennisCredentialsModel.getPassword());
        chkShowPlayersImage.setChecked(myTischtennisCredentialsModel.isPlayersImageShow());
    }

    private void initializeTTRKonstante()
    {
        ttrKonstanteModel = new TTRKonstante(this);
        chkKonstante1JahrOhneSpiel.setChecked(ttrKonstanteModel.getUeberEinJahrOhneSpiel());
        chkKonstanteWeniger15Spiele.setChecked(ttrKonstanteModel.getWenigerAls15Spiele());
        switch (ttrKonstanteModel.getAlter())
        {
            case UNTER_16:
                rbUnter16.toggle();
                break;
            case UNTER_21:
                rbUnter21.toggle();
                break;
            case UEBER_21:
                rbUeber21.toggle();
                break;
            default:
                rbUnter16.toggle();
                break;
        }
        updateTTRKonstante();
    }

    private void updateTTRKonstante()
    {
        txtTTRKonstante.setText(String.valueOf(ttrKonstanteModel.getTTRKonstante()));
    }

    private void changeAlter(Alter alter, boolean checked)
    {
        if(! startIsFinished) return;

        if(checked)
        {
            ttrKonstanteModel.setAlter(alter);
            updateTTRKonstante();
        }
    }

    private void finishActivity()
    {
        Intent intent = new Intent();
        intent.putExtra(SettingsActivity.PUT_EXTRA_IS_LOGIN_DATA_CHANGED, isLoginDataChanged);
        setResult(RESULT_OK, intent);
        finish();
    }

}
