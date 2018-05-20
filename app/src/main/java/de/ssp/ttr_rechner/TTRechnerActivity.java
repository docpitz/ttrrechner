package de.ssp.ttr_rechner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import de.ssp.ttr_rechner.model.Match;
import de.ssp.ttr_rechner.rechner.TTRRechnerUtil;

public class TTRechnerActivity extends AppCompatActivity
{
    protected @BindView(R.id.txtMeinTTRWert) EditText txtMeinTTRWert;
    protected @BindView(R.id.txtNeueTTRPunkte) TextView txtNeueTTRPunkte;
    protected @BindView(R.id.pnlMatchList) LinearLayout pnlMatchList;
    private Toast anzahlGegnerToast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttrrechner_activity_scrolling);
        anzahlGegnerToast = new Toast(this);
        // erstmal nicht so wichtig
        ButterKnife.bind(this);
        activateToolbar();
    }

    @OnClick(R.id.btnCalculatePoints)
    public void pressBtnCalculatePoints()
    {
        String strAnzeigeErgebnis = "-";
        String strMeinTTRWert = txtMeinTTRWert.getText().toString();

        if (! "".equals(strMeinTTRWert))
        {
            int intMeinTTRWert = Integer.parseInt(strMeinTTRWert);
            TTRRechnerUtil calculator = new TTRRechnerUtil(intMeinTTRWert, 24);

            int count = pnlMatchList.getChildCount();
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                LinearLayout singleMatch = (LinearLayout) pnlMatchList.getChildAt(i);

                EditText txtTTRGegner = singleMatch.findViewById(R.id.txtTTRGegner);
                String strTTRGegner = txtTTRGegner.getText().toString();

                if (!"".equals(strTTRGegner)) {
                    int intTTRGegner = Integer.parseInt(strTTRGegner);

                    Switch switchMatch = singleMatch.findViewById(R.id.chkSieg);
                    Boolean gewonnen = switchMatch.isChecked();

                    Match modelMatch = new Match(intTTRGegner, gewonnen);
                    matches.add(modelMatch);
                }

            }

            int intAenderungTTR = (int) calculator.berechneTTRAenderung(matches);

            int intEndergebnis = intMeinTTRWert + intAenderungTTR;

            strAnzeigeErgebnis = String.valueOf(intEndergebnis);

        }
        txtNeueTTRPunkte.setText(strAnzeigeErgebnis);
    }

    @OnClick(R.id.btnAddMatch)
    public void pressBtnAddMatch()
    {
        final View pnlSingleMatch = getLayoutInflater().inflate(R.layout.ttrechner_single_match, null);
        new ButtonRemoveViewHolder(pnlSingleMatch);
        pnlMatchList.addView(pnlSingleMatch);
        showToastAnzahlGegner();
    }

    class ButtonRemoveViewHolder
    {
        ButtonRemoveViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }


        @OnClick(R.id.btnRemoveMatch)
        public void pressBtnRemoveMatch(AppCompatImageButton button)
        {
            LinearLayout pnlSingleMatch = (LinearLayout)button.getParent();
            TTRechnerActivity.this.pnlMatchList.removeView(pnlSingleMatch);
            showToastAnzahlGegner();
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

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
