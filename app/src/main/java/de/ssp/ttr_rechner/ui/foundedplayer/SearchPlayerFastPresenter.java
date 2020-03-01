package de.ssp.ttr_rechner.ui.foundedplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;
import java.util.List;

import de.ssp.ttr_rechner.TTRCalculatorActivity;
import de.ssp.ttr_rechner.model.PlayerChooseable;

public class SearchPlayerFastPresenter implements ServiceSearchPlayerCallback
{
    protected SearchPlayerFastView view;
    protected ServiceSearchPlayerCache serviceSearchPlayerCache;
    protected ArrayList<PlayerChooseable> choosedPlayers;
    protected Context context;
    protected boolean isSingleChooseActiv = false;

    public SearchPlayerFastPresenter(SearchPlayerFastView view, Context context, boolean isSingleChooseActive)
    {
        this.isSingleChooseActiv = isSingleChooseActive;
        this.context = context;
        this.view = view;
        choosedPlayers = PlayerChooseable.convertFromPlayers(null);
        this.serviceSearchPlayerCache = new ServiceSearchPlayerCache(context, this);
    }

    public synchronized void changeQuery(String query)
    {
        ServiceSearchPlayerCache.SearchPlayerCacheState state = serviceSearchPlayerCache.getServiceSearchCacheState(query);

        switch (state)
        {
            case NO_QUERY:
            {
                view.showMessageMoreSearchWords();
                break;
            }
            case SERVICE_CALL:
            {
                serviceSearchPlayerCache.callServiceSearchedPlayer(query);
                view.showServiceIsRunning();
                break;
            }
            case SERVICE_RUNNING:
            {
                serviceSearchPlayerCache.prepairServiceCall(query);
                view.showServiceIsRunning();
                break;
            }
            case CACHE:
            {
                ServiceSearchPlayerCache.ServicePlayerFinishModel model = serviceSearchPlayerCache.getCachedData(query);
                handleServicePlayerFinishModel(model.success, model.playerList, model.errorMessage, query, false);
                break;
            }
        }
    }

    @Override
    public synchronized void serviceFinished(SearchPlayer searchObject, boolean success, List<Player> playerList, String errorMessage, boolean serviceNextTry)
    {
        handleServicePlayerFinishModel(success, playerList, errorMessage, searchObject != null ? searchObject.getFirstname() + " " + searchObject.getLastname() : "", serviceNextTry);
    }

    protected synchronized void handleServicePlayerFinishModel(boolean success, List<Player> playerList, String errorMessage, String query, boolean serviceNextTry)
    {
        String lastQuery = serviceSearchPlayerCache.getLastSavedQuery();
        if(lastQuery == null || lastQuery.equals(query))
        {
            if(serviceSearchPlayerCache.isServiceRunning || serviceNextTry)
            {
                view.showServiceIsRunning();
                return;
            }
            else if(success && playerList != null)
            {
                view.showResults((ArrayList<Player>) playerList, query);
            }
            else
            {
                view.showErrorMessage(errorMessage);
            }
        }
        else
        {
            Log.d(this.getClass().toString(), "lastQuerySaved: " + lastQuery);
            Log.d(this.getClass().toString(), "nowQuery: " + query);
            changeQuery(lastQuery);
        }
    }

    public void addChoosedPlayer(PlayerChooseable chooseablePlayer)
    {
        serviceSearchPlayerCache.cancelService();
        choosedPlayers.add(chooseablePlayer);
        view.addChip(chooseablePlayer);
        view.resetView();
        if(isSingleChooseActiv)
        {
            calcResultAndFinishActivity();
        }
        else if(choosedPlayers.size() == 1)
        {
            view.showFloatingActionButton(true);
        }
    }

    public void removeChoosedPlayer(PlayerChooseable chooseablePlayer)
    {
        choosedPlayers.remove(chooseablePlayer);
        if(choosedPlayers.size() == 0)
        {
            view.showFloatingActionButton(false);
        }
    }

    public void calcResultAndFinishActivity()
    {
        ArrayList<Player> listPlayer = new ArrayList<>();
        for (PlayerChooseable choosedPlayer : choosedPlayers)
        {
            listPlayer.add(choosedPlayer.player);
        }
        Intent intent = new Intent();
        if(isSingleChooseActiv)
        {
            intent.putExtra(TTRCalculatorActivity.PUT_EXTRA_RESULT_IAM_PLAYER, listPlayer.get(0));
        }
        else
        {
            intent.putExtra(TTRCalculatorActivity.PUT_EXTRA_RESULT_OTHER_PLAYERS, listPlayer);
        }
        view.finishActivity(Activity.RESULT_OK, intent);
    }

    public void pressedDone()
    {
        calcResultAndFinishActivity();
    }

    public void pressedBack()
    {
        if(choosedPlayers.size() > 0)
        {
            view.showConsultationForBackButton();
        }
        else
        {
            view.finishActivity(Activity.RESULT_CANCELED, null);
        }
    }


}
