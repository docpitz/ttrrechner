package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.Player;

import java.util.List;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorFindCompletePlayer;

public class ServiceCallerFindCompletePlayer extends MyTischtennisEnsureLoginCaller<List<Player>>
{
    protected List<Player> player;
    public ServiceCallerFindCompletePlayer(Context context, ServiceReady<List<Player>> serviceReady, List<Player> player)
    {
        super(context, "Suche TTR-Punkte von Spielern", serviceReady);
        this.player = player;
    }

    @Override
    protected ParserEvaluator<List<Player>> getParserEvaluator() {
        return new ParserEvaluatorFindCompletePlayer(player);
    }
}