package de.ssp.ttr_rechner.ui.foundedplayer;

import android.content.Intent;

import com.jmelzer.myttr.Player;

import java.util.ArrayList;

import de.ssp.ttr_rechner.model.PlayerChooseable;

public interface SearchPlayerFastView extends FloatingActionButtonView
{
    public void showErrorMessage(String errorMessage);
    public void showResults(ArrayList<Player> playerList, String query);
    public void showMessageMoreSearchWords();
    public void showServiceIsRunning();
    public void resetView();
    public void addChip(PlayerChooseable player);
    public void finishActivity(int result, Intent intent);
    public void showConsultationForBackButton();
}
