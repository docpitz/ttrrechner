package de.ssp.ttr_rechner.ui.foundedplayer;

import android.content.Context;
import android.util.Log;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ssp.ttr_rechner.service.caller.ServiceCallerSearchPlayer;
import de.ssp.ttr_rechner.service.caller.ServiceFinish;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorSearchPlayer;

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

    /*

    private void callServiceSearchPlayerIfNessecary(String query, boolean shureRun)
    {
        if(query.isEmpty())
        {
            listAdapter.updateData(new ChooseablePlayer[]{});
        }

        if(!shureRun && serviceCallerSearchPlayer != null && serviceCallerSearchPlayer.isServiceRunning())
        {
            return;
        }

        SearchPlayer searchPlayer = new SearchPlayer();
        String name[] = query.split(" ");
        if(name.length > 1 && name[1].length() > 2 &&
                (searchedPlayers.isEmpty() || lastServiceQueryFound.length() > query.length() || ! query.contains(lastServiceQueryFound)))
        {
            searchPlayer.setFirstname(name[0]);
            searchPlayer.setLastname(name[1]);
            serviceCallerSearchPlayer = new ServiceCallerSearchPlayer(context, this, searchPlayer, false);
            serviceCallerSearchPlayer.callService();
            txtHinweistext.setText("Suche läuft...");
            pnlImageView.setVisibility(View.VISIBLE);
            lastServiceQueryTry = query;
        }
        else if(!(name.length > 1 && name[1].length() > 2))
        {
            searchedPlayers = ChooseablePlayer.convertFromPlayers(null);
            listAdapter.updateData(ChooseablePlayer.getPlayers(searchedPlayers));
            txtHinweistext.setText(R.string.bitte_mit_vorname_nachname_suchen);
        }
        else
        {
            listAdapter.getFilter().filter(query);
            if(listAdapter.getCount() == 0 && name.length > 1 && name[1].length() > 2)
            {
                txtHinweistext.setText("Es wurden keine Spieler gefunden.");
            }
        }
    }

    /*
    @Override
    public void serviceFinished(boolean success, List<Player> playerList, String errorMessage)
    {
        String queryNow = txtSearchField.getQuery().toString();
        pnlImageView.setVisibility(View.GONE);
        if(success && playerList.size() > 0 && queryNow.contains(lastServiceQueryTry))
        {
            searchedPlayers = ChooseablePlayer.convertFromPlayers((ArrayList<Player>) playerList);
            listAdapter.updateData(ChooseablePlayer.getPlayers(searchedPlayers));
            listAdapter.getFilter().filter(txtSearchField.getQuery());
            lastServiceQueryFound = lastServiceQueryTry;
            txtHinweistext.setText("Es wurden keine Spieler gefunden.");
        }
        else
        {
            if(errorMessage != null)
            {
                txtHinweistext.setText(errorMessage);
            }
            else {
                txtHinweistext.setText("Es wurden keine Spieler gefunden.");
            }
            searchedPlayers = ChooseablePlayer.convertFromPlayers(null);
            listAdapter.updateData(new ChooseablePlayer[]{});

            if((lastServiceQueryTry.length() < queryNow.length() || ! queryNow.contains(lastServiceQueryTry))
                    && (ParserEvaluatorSearchPlayer.ZU_VIELE_SPIELER_GEFUNDEN.equals(errorMessage) || (playerList.isEmpty() && errorMessage == null)))
            {
                callServiceSearchPlayerIfNessecary(queryNow, true);
            }
        }
    }

     */
}
