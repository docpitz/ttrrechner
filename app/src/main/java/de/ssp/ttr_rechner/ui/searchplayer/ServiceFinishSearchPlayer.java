package de.ssp.ttr_rechner.ui.searchplayer;

import android.app.Activity;
import android.content.Intent;

import com.jmelzer.myttr.Player;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import de.ssp.ttr_rechner.FoundedPlayerActivity;
import de.ssp.ttr_rechner.TTRCalculatorActivity;
import de.ssp.ttr_rechner.service.ServiceErrorAlertDialogHelper;
import de.ssp.ttr_rechner.service.caller.ServiceFinish;

public class ServiceFinishSearchPlayer implements ServiceFinish<List<Player>>
{
    protected Activity activity;
    protected boolean isSingleChooseActiv;
    public ServiceFinishSearchPlayer(Activity activity, boolean isSingleChooseActiv)
    {
        this.activity = activity;
        this.isSingleChooseActiv = isSingleChooseActiv;
    }

    @Override
    public void serviceFinished(boolean success, List<Player> playerList, String errorMessage)
    {
        if(! ServiceErrorAlertDialogHelper.showErrorDialog(activity, success, errorMessage))
        {
            if(playerList != null && playerList.size() > 0)
            {
                Intent intent = new Intent(activity, FoundedPlayerActivity.class);
                intent.putExtra(FoundedPlayerActivity.PUT_EXTRA_PLAYER_LIST, (ArrayList) playerList);
                intent.putExtra(FoundedPlayerActivity.PUT_EXTRA_IS_SINGLE_CHOOSE_ACTIV, isSingleChooseActiv);
                activity.startActivityForResult(intent, TTRCalculatorActivity.REQUEST_CODE_SEARCH);
            }
            else
            {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                dialogBuilder.setTitle("Keine Spieler gefunden")
                        .setMessage("Mit den angegebenen Suchkriterien wurden keine Spieler gefunden.")
                        .setPositiveButton("Ok", null);
                dialogBuilder.create().show();
            }
        }
    }
}
