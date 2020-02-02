package de.ssp.ttr_rechner.ui.foundedplayer;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

public interface ServiceSearchPlayerCallback
{
    public void serviceFinished(SearchPlayer searchObject, boolean success, List<Player> playerList, String errorMessage, boolean serviceNextTry);
}
