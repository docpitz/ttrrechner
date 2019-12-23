package de.ssp.ttr_rechner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.ssp.ttr_rechner.model.TTRKonstante;

public class TTRKonstanteActivity extends AppCompatActivity
{
    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.chkKonstante1JahrOhneSpiel) Switch chkKonstante1JahrOhneSpiel;
    protected @BindView(R.id.chkKonstanteUnter16) Switch chkKonstanteUnter16;
    protected @BindView(R.id.chkKonstanteUnter21) Switch chkKonstanteUnter21;
    protected @BindView(R.id.chkKonstanteWeniger15Spiele) Switch chkKonstanteWeniger15Spiele;
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
        chkKonstanteUnter16.setChecked(ttrKonstanteModel.getUnter16Jahre());
        chkKonstanteUnter21.setChecked(ttrKonstanteModel.getUnter21Jahre());
        chkKonstanteWeniger15Spiele.setChecked(ttrKonstanteModel.getWenigerAls15Spiele());
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

    @OnCheckedChanged(R.id.chkKonstanteUnter16)
    public void changeUnter16JahreSwitch(CompoundButton switchButton, boolean checked)
    {
        if (startIsFinished)
        {
            ttrKonstanteModel.setUnter16Jahre(chkKonstanteUnter16.isChecked());
            if(chkKonstanteUnter16.isChecked())
            {
                ttrKonstanteModel.setUnter21Jahre(true);
                chkKonstanteUnter21.setChecked(true);
            }
            updateTTRKonstante();
        }
    }

    @OnCheckedChanged(R.id.chkKonstanteUnter21)
    public void changeUnter21JahreSwitch(CompoundButton switchButton, boolean checked)
    {
        if(startIsFinished)
        {
            if(chkKonstanteUnter16.isChecked() && !chkKonstanteUnter21.isChecked())
            {
                chkKonstanteUnter21.setChecked(true);
                Toast.makeText(this, "Wenn du unter 16 Jahre alt bist, bist du gleichzeitig unter 21 Jahre alt!", Toast.LENGTH_LONG).show();
            }
            else
            {
                ttrKonstanteModel.setUnter21Jahre(chkKonstanteUnter21.isChecked());
            }
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
