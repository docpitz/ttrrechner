package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.Player;

import java.util.List;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorFindCompletePlayer;

public class ServiceCallerFindCompletePlayer extends MyTischtennisEnsureLoginCaller<List<Player> ,List<Player>>
{
    protected List<Player> player;
    public ServiceCallerFindCompletePlayer(Context context, ServiceFinish<List<Player>, List<Player>> serviceFinish, List<Player> player)
    {
        super(context, "Suche TTR-Punkte von Spielern", serviceFinish);
        this.player = player;
    }

    @Override
    protected ParserEvaluator<List<Player>, List<Player>> getParserEvaluator() {
        return new ParserEvaluatorFindCompletePlayer(player);
    }
}
