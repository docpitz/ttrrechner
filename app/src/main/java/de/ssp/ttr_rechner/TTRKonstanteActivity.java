package de.ssp.ttr_rechner;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.ssp.ttr_rechner.model.Alter;
import de.ssp.ttr_rechner.model.TTRKonstante;

public class TTRKonstanteActivity extends AppCompatActivity
{
    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.chkKonstante1JahrOhneSpiel) Switch chkKonstante1JahrOhneSpiel;
    protected @BindView(R.id.chkKonstanteWeniger15Spiele) Switch chkKonstanteWeniger15Spiele;
    protected @BindView(R.id.rbUnter16) RadioButton rbUnter16;
    protected @BindView(R.id.rbUnter21) RadioButton rbUnter21;
    protected @BindView(R.id.rbUeber21) RadioButton rbUeber21;
    protected @BindView(R.id.txtTTRKonstante) TextView txtTTRKonstante;
    protected TTRKonstante ttrKonstanteModel;
    protected boolean startIsFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttrkonstante);

        ButterKnife.bind(this);

        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        startIsFinished = true;
    }

    @OnCheckedChanged({R.id.chkKonstanteWeniger15Spiele, R.id.chkKonstante1JahrOhneSpiel})
    public void change15SpieleUnd1JahrOhneSpielSwitch(CompoundButton switchButton, boolean checked)
    {
        if(startIsFinished)
        {
            ttrKonstanteModel.setUeberEinJahrOhneSpiel(chkKonstante1JahrOhneSpiel.isChecked());
            ttrKonstanteModel.setWenigerAls15Spiele(chkKonstanteWeniger15Spiele.isChecked());
            updateTTRKonstante();
        }
    }

    @OnCheckedChanged(R.id.rbUnter16)
    public void toogleUnter16(CompoundButton button, boolean checked)
    {
        changeAlter(Alter.UNTER_16, checked);
    }

    @OnCheckedChanged(R.id.rbUnter21)
    public void toogleUnter18(CompoundButton button, boolean checked)
    {
        changeAlter(Alter.UNTER_21, checked);
    }

    @OnCheckedChanged(R.id.rbUeber21)
    public void toogleUeber18(CompoundButton button, boolean checked)
    {
        changeAlter(Alter.UEBER_21, checked);
    }

    private void changeAlter(Alter alter, boolean checked)
    {
        if(startIsFinished && checked)
        {
            ttrKonstanteModel.setAlter(alter);
            updateTTRKonstante();
        }
    }

    private void updateTTRKonstante()
    {
        txtTTRKonstante.setText(String.valueOf(ttrKonstanteModel.getTTRKonstante()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
