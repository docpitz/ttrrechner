package de.ssp.ttr_rechner.ui.foundedplayer;

import android.content.Context;
import android.util.Log;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ssp.service.mytischtennis.caller.ServiceCallerSearchPlayer;
import de.ssp.service.mytischtennis.caller.ServiceFinish;
import de.ssp.service.mytischtennis.parserEvaluator.ParserEvaluatorSearchPlayer;

public class ServiceSearchPlayerCache implements ServiceFinish<SearchPlayer, List<Player>>
{
    protected ServiceCallerSearchPlayer serviceCallerSearchPlayer;
    protected Map<String, ServicePlayerFinishModel> mapServicePlayerFinishModel;
    protected Context context;
    protected ServiceSearchPlayerCallback callBack;
    protected List<String> prepairedServiceCallQuery;
    protected boolean isServiceRunning;

    enum SearchPlayerCacheState
    {
        NO_QUERY,
        CACHE,
        SERVICE_CALL,
        SERVICE_RUNNING;
    }

    class ServicePlayerFinishModel
    {
        boolean success;
        List<Player> playerList;
        String errorMessage;

        ServicePlayerFinishModel(boolean success, List<Player> playerList, String errorMessage)
        {
            this.success = success;
            this.playerList = playerList;
            this.errorMessage = errorMessage;
        }
    }


    public ServiceSearchPlayerCache(Context context, ServiceSearchPlayerCallback callback)
    {
        this.prepairedServiceCallQuery = new ArrayList<>();
        this.context = context;
        this.callBack = callback;
        mapServicePlayerFinishModel = new HashMap<>();
    }

    protected synchronized boolean isInputParamEnough(String inputParam)
    {
        if (inputParam == null) return false;

        String name[] = inputParam.split(" ");
        if(name.length > 1)
        {
            String vorname = name[0];
            String nachname = name[1];
            return vorname.length() > 2 && nachname.length() > 2;
        }
        return false;
    }


    public synchronized SearchPlayerCacheState getServiceSearchCacheState(String query)
    {
        if(isInputParamEnough(query))
        {
            if(getCachedData(query) != null)
            {
                return SearchPlayerCacheState.CACHE;
            }
            else if(serviceCallerSearchPlayer != null && serviceCallerSearchPlayer.isServiceRunning())
            {
                return SearchPlayerCacheState.SERVICE_RUNNING;
            }
            else
            {
                return SearchPlayerCacheState.SERVICE_CALL;
            }
        }
        return SearchPlayerCacheState.NO_QUERY;
    }

    public synchronized ServicePlayerFinishModel getCachedData(String query)
    {
        ServicePlayerFinishModel returnModel = null;
        for(int i = 1; i <= query.length(); i++)
        {
            ServicePlayerFinishModel model = mapServicePlayerFinishModel.get(query.substring(0, i));
            if(model != null && (model.success || !ParserEvaluatorSearchPlayer.ZU_VIELE_SPIELER_GEFUNDEN.equals(model.errorMessage)))
            {
                returnModel = model;
                continue;
            }
        }
        if(returnModel == null)
        {
            ServicePlayerFinishModel model = mapServicePlayerFinishModel.get(query);
            if(model != null && ParserEvaluatorSearchPlayer.ZU_VIELE_SPIELER_GEFUNDEN.equals(model.errorMessage))
            {
                returnModel = model;
            }
        }
        return returnModel;
    }

    public synchronized void callServiceSearchedPlayer(String query)
    {
        isServiceRunning = true;
        serviceCallerSearchPlayer = new ServiceCallerSearchPlayer(context, this, getSearchPlayerByQuery(query), false);
        serviceCallerSearchPlayer.callService();
    }

    public synchronized void prepairServiceCall(String query)
    {
        this.prepairedServiceCallQuery.add(query);
    }

    @Override
    public synchronized void serviceFinished(SearchPlayer requestValue, boolean success, List<Player> playerList, String errorMessage)
    {
        isServiceRunning = false;
        String anzahl = playerList != null ? playerList.size()+"" : "0";
        Log.d(this.getClass().toString(), "Founded Players: " + anzahl);
        Log.d(this.getClass().toString(), "ErrorMessage: " + errorMessage);
        if(success || ParserEvaluatorSearchPlayer.ZU_VIELE_SPIELER_GEFUNDEN.equals(errorMessage))
        {
            mapServicePlayerFinishModel.put(requestValue.getFirstname() + " " + requestValue.getLastname(), new ServicePlayerFinishModel(success, playerList, errorMessage));
            Log.d(this.getClass().toString(), "Anzahl der gespeicherten Modelle: " + mapServicePlayerFinishModel.size());
        }

        boolean serviceNextTry = false;
        synchronized (this)
        {
            if(prepairedServiceCallQuery.size() > 0)
            {
                SearchPlayerCacheState state = getServiceSearchCacheState(getLastSavedQuery());
                if (state == SearchPlayerCacheState.SERVICE_CALL || state == SearchPlayerCacheState.SERVICE_RUNNING) {
                    callServiceSearchedPlayer(getLastSavedQuery());
                    serviceNextTry = true;
                }
                else if(state == SearchPlayerCacheState.NO_QUERY)
                {
                    playerList = null;
                    success = false;
                    requestValue = getSearchPlayerByQuery(getLastSavedQuery());
                }
                else if(state == SearchPlayerCacheState.CACHE)
                {
                    ServicePlayerFinishModel model = getCachedData(getLastSavedQuery());
                    playerList = model.playerList;
                    success = model.success;
                    errorMessage = model.errorMessage;
                    requestValue = getSearchPlayerByQuery(getLastSavedQuery());
                }
                prepairedServiceCallQuery.clear();
            }
        }
        callBack.serviceFinished(requestValue, success, playerList, errorMessage, serviceNextTry);
    }

    public void cancelService()
    {
        serviceCallerSearchPlayer.cancelService();
    }

    protected synchronized SearchPlayer getSearchPlayerByQuery(String query)
    {
        SearchPlayer searchPlayer = new SearchPlayer();
        if (query == null) return searchPlayer;

        String name[] = query.split(" ");
        if(name.length > 0)
        {
            searchPlayer.setFirstname(name[0]);
        }
        if(name.length > 1)
        {
            searchPlayer.setLastname(name[1]);
        }
        return searchPlayer;
    }

    protected synchronized String getFirstSavedQuery()
    {
        if(prepairedServiceCallQuery.size() > 0)
        {
            return prepairedServiceCallQuery.get(0);
        }
        return "";
    }

    protected synchronized String getLastSavedQuery()
    {
        if(prepairedServiceCallQuery.size() > 0)
        {
            return prepairedServiceCallQuery.get(prepairedServiceCallQuery.size()-1);
        }
        return null;
    }

    public boolean isServiceRunning() {
        return isServiceRunning;
    }
}
