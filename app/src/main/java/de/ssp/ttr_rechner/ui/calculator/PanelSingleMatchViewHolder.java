package de.ssp.ttr_rechner.ui.calculator;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.ssp.service.mytischtennis.model.Match;
import de.ssp.ttr_rechner.R;

public class PanelSingleMatchViewHolder
{
    protected Context activity;
    protected TTRCalculatorInteractor ttrCalculatorInteractor;
    protected @BindView(R.id.pnlMatchView) LinearLayout pnlSingleMatchView;
    protected @BindView(R.id.txtHintTTRGegner) TextInputLayout txtHintTTRGegner;
    protected @BindView(R.id.txtTTRGegner) EditText txtTTRGegner;
    protected @BindView(R.id.chkSieg) Switch chkSieg;
    protected @BindView(R.id.btnRemoveMatch) ImageButton btnRemoveMatch;

    public PanelSingleMatchViewHolder(Activity activity, Match newMatch, TTRCalculatorInteractor ttrCalculatorInteractor)
    {
        View pnlSingleMatch = activity.getLayoutInflater().inflate(R.layout.tttrechner_match_row_element, null);
        ButterKnife.bind(this, pnlSingleMatch);
        this.ttrCalculatorInteractor = ttrCalculatorInteractor;
        this.activity = activity;
        createMatch(newMatch);
    }

    @OnTextChanged(R.id.txtTTRGegner)
    public void changeTxtTTRGegner(CharSequence text)
    {
        txtHintTTRGegner.setHint(activity.getString(R.string.hint_ttr_gegner));
        ttrCalculatorInteractor.resetNeueTTRPunkte();
    }

    @OnCheckedChanged(R.id.chkSieg)
    public void changeChkSieg(CompoundButton switchButton, boolean checked)
    {
        ttrCalculatorInteractor.resetNeueTTRPunkte();
    }

    @OnClick(R.id.btnRemoveMatch)
    public void pressBtnRemoveMatch(ImageButton button)
    {

        YoYo.with(Techniques.Pulse)
                .duration(200)
                .onStart(animator -> button.setAlpha(0.5f))
                .onEnd(animator ->
                {
                    ttrCalculatorInteractor.removeMatch(PanelSingleMatchViewHolder.this);
                    ttrCalculatorInteractor.showToastAnzahlGegner();
                    ttrCalculatorInteractor.resetNeueTTRPunkte();
                })
                .playOn(btnRemoveMatch);
    }

    public Match getMatch()
    {
        String strTTRGegner = txtTTRGegner.getText().toString();

        int intTTRGegner = Match.NO_MATCH;
        if (!"".equals(strTTRGegner))
        {
            intTTRGegner = Integer.parseInt(strTTRGegner);
        }

        Boolean gewonnen = chkSieg.isChecked();
        String nameVerein = txtHintTTRGegner.getHint() != null ? txtHintTTRGegner.getHint().toString() : null;
        Match modelMatch = new Match(intTTRGegner, gewonnen, nameVerein);
        return modelMatch;
    }

    public LinearLayout getView()
    {
        return pnlSingleMatchView;
    }

    public void setDeleteButtonVisibility(int visibility)
    {
        btnRemoveMatch.setVisibility(visibility);
    }

    private void createMatch(Match match)
    {
        if(match != null)
        {
            if(match.getGegnerischerTTRWert() > 0)
            {
                txtTTRGegner.setText(String.valueOf(match.getGegnerischerTTRWert()));
                if(match.getNameVerein() != null)
                {
                    txtHintTTRGegner.setHint(match.getNameVerein());
                }
                else
                {
                    txtHintTTRGegner.setHint(activity.getString(R.string.hint_ttr_gegner));
                }
            }
            chkSieg.setChecked(match.isGewonnen());
        }
    }
}
