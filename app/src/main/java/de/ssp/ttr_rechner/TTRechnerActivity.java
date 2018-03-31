package de.ssp.ttr_rechner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class TTRechnerActivity extends AppCompatActivity
{
    @BindView(R.id.txtMeinTTRWert) EditText txtMeinTTRWert;
    @BindView(R.id.txtNeueTTRPunkte) TextView txtNeueTTRPunkte;
    @BindView(R.id.pnlMatchList) LinearLayout pnlMatchList;
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

    @OnClick(R.id.btnAdd)
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
