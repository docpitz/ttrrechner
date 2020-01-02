package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

import de.ssp.ttr_rechner.service.asynctask.MyTischtennisService;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorRealNameAndPoints;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorSearchPlayer;

public class ServiceCallerSearchPlayer extends MyTischtennisEnsureLoginCaller<List<Player>>
{
    private SearchPlayer searchPlayer;
    public ServiceCallerSearchPlayer(Context context, ServiceReady<List<Player>> serviceReady, SearchPlayer searchPlayer)
    {
        super(context, "Suche Spieler", serviceReady);
        this.searchPlayer = searchPlayer;
    }

    @Override
    protected ParserEvaluator<List<Player>> getParserEvaluator() {
        return new ParserEvaluatorSearchPlayer(searchPlayer);
    }
}
