package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.Player;

import java.util.List;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorFindPlayersByTeam;

public class ServiceCallerFindPlayersByTeam extends MyTischtennisEnsureLoginCaller<String, List<Player>>
{
    private String id;
    public ServiceCallerFindPlayersByTeam(Context context, ServiceFinish<String, List<Player>> serviceFinish, String id)
    {
        super(context, "Suche Spieler aus dem Team", serviceFinish);
        this.id = id;
    }

    @Override
    protected ParserEvaluator<String, List<Player>> getParserEvaluator() {
        return new ParserEvaluatorFindPlayersByTeam(id);
    }
}
