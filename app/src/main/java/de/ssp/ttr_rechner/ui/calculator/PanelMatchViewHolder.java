package de.ssp.ttr_rechner.ui.calculator;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;

import de.ssp.service.mytischtennis.model.Match;

public class PanelMatchViewHolder
{
    protected LinearLayout pnlMatchList;
    protected List<PanelSingleMatchViewHolder> panelSingleMatchViewHolderList;

    public PanelMatchViewHolder(LinearLayout pnlMatchList)
    {
        this.pnlMatchList = pnlMatchList;
        panelSingleMatchViewHolderList = new ArrayList<>();
    }

    public ArrayList<Match> getMatches()
    {
        ArrayList<Match> matches = new ArrayList<>();
        for (PanelSingleMatchViewHolder panelSingleMatchViewHolder: panelSingleMatchViewHolderList)
        {
            matches.add(panelSingleMatchViewHolder.getMatch());
        }
        return matches;
    }

    public void updateRemoveButton()
    {
        if(panelSingleMatchViewHolderList.size() > 1)
        {
            for (PanelSingleMatchViewHolder panelSingleMatchViewHolder: panelSingleMatchViewHolderList)
            {
                panelSingleMatchViewHolder.setDeleteButtonVisibility(View.VISIBLE);
            }
        }

        if(panelSingleMatchViewHolderList.size() == 1)
        {
            panelSingleMatchViewHolderList.get(0).setDeleteButtonVisibility(View.INVISIBLE);
        }
    }

    public void restoreView(List<Match> matches, Activity activity, TTRCalculatorInteractor ttrCalculatorInteractor)
    {
        this.clearMatchesOnView();
        for(Match match : matches)
        {
            addMatch(match, activity, ttrCalculatorInteractor);
        }
        if(panelSingleMatchViewHolderList.size() == 0)
        {
            addMatch(null, activity, ttrCalculatorInteractor);
        }
    }

    public void reset(Activity activity, TTRCalculatorInteractor ttrCalculatorInteractor)
    {
        this.clearMatchesOnView();
        addMatch(null, activity, ttrCalculatorInteractor);
    }

    public void addMatch(Match match, Activity activity, TTRCalculatorInteractor ttrCalculatorInteractor)
    {
        PanelSingleMatchViewHolder panelSingleMatchViewHolder = new PanelSingleMatchViewHolder(activity, match, ttrCalculatorInteractor);
        panelSingleMatchViewHolderList.add(panelSingleMatchViewHolder);
        pnlMatchList.addView(panelSingleMatchViewHolder.getView());
        updateRemoveButton();
        YoYo.with(Techniques.FadeIn)
                .duration(200)
                .playOn(panelSingleMatchViewHolder.getView());
    }

    public void removeMatch(PanelSingleMatchViewHolder panelSingleMatchViewHolder)
    {
        pnlMatchList.removeView(panelSingleMatchViewHolder.pnlSingleMatchView);
        panelSingleMatchViewHolderList.remove(panelSingleMatchViewHolder);
        updateRemoveButton();
    }

    protected void clearMatchesOnView()
    {
        pnlMatchList.removeAllViews();
        panelSingleMatchViewHolderList.clear();
    }
}
