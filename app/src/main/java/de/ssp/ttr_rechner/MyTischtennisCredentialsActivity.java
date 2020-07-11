package de.ssp.ttr_rechner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.jmelzer.myttr.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.ssp.service.mytischtennis.caller.ServiceCallerLogin;
import de.ssp.service.mytischtennis.caller.ServiceFinish;
import de.ssp.service.mytischtennis.model.MyTischtennisCredentials;

public class MyTischtennisCredentialsActivity extends AppCompatActivity implements ServiceFinish<String, User>
{
    public static String PUT_EXTRA_IS_LOGIN_DATA_CHANGED = "IS_LOGIN_DATA_CHANGED";

    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.txtUsername) EditText txtUsername;
    protected @BindView(R.id.txtPassword) EditText txtPassword;
    protected @BindView(R.id.chkMyttLoginPossible) Switch chkMyttLoginPossible;
    protected @BindView(R.id.pnlLogin) LinearLayout pnlLogin;
    protected @BindView(R.id.chkShowPlayersImage) Switch chkShowPlayersImage;
    protected @BindView(R.id.btnLoginDelete) Button btnLoginDelete;
    protected @BindView(R.id.btnLogin) Button btnLogin;
    protected MyTischtennisCredentials myTischtennisCredentialsModel;
    protected boolean startIsFinished = false;
    protected boolean isLoginDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytischtennis_credentials_activity);

        ButterKnife.bind(this);

        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        enableView(checked);
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
    public void serviceFinished(String username, boolean success, User user, String errorMessage) {
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
        chkMyttLoginPossible.setChecked(myTischtennisCredentialsModel.isMyTischtennisLoginPossible());
        txtUsername.setText(myTischtennisCredentialsModel.getUsername());
        txtPassword.setText(myTischtennisCredentialsModel.getPassword());
        chkShowPlayersImage.setChecked(myTischtennisCredentialsModel.isPlayersImageShow());
        enableView(myTischtennisCredentialsModel.isMyTischtennisLoginPossible());
    }

    private void enableView(boolean enable)
    {
        txtUsername.setEnabled(enable);
        txtPassword.setEnabled(enable);
        btnLogin.setEnabled(enable);
        btnLoginDelete.setEnabled(enable);
        chkShowPlayersImage.setEnabled(enable);
        if(! enable)
        {
            chkShowPlayersImage.setChecked(false);
        }
    }

    private void finishActivity()
    {
        Intent intent = new Intent();
        intent.putExtra(MyTischtennisCredentialsActivity.PUT_EXTRA_IS_LOGIN_DATA_CHANGED, isLoginDataChanged);
        setResult(RESULT_OK, intent);
        finish();
    }
}
