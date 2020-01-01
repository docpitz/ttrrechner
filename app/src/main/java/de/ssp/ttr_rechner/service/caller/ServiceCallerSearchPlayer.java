package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

import de.ssp.ttr_rechner.service.asynctask.MyTischtennisService;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorRealNameAndPoints;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorSearchPlayer;

public class ServiceCallerSearchPlayer extends MyTischtennisEnsureLoginCaller<List<Player>>
{
    private SearchPlayer searchPlayer;
    public ServiceCallerSearchPlayer(Context context, ServiceReady<List<Player>> serviceReady, SearchPlayer searchPlayer)
    {
        super(context, serviceReady);
        this.searchPlayer = searchPlayer;
    }

    @Override
    public void callLoggedInService()
    {
        ParserEvaluatorSearchPlayer parserEvaluatorSearchPlayer = new ParserEvaluatorSearchPlayer(searchPlayer);
        MyTischtennisService<List<Player>> myTischtennisService = new MyTischtennisService<>(context, parserEvaluatorSearchPlayer, serviceReady);
        myTischtennisService.execute();
    }
}
