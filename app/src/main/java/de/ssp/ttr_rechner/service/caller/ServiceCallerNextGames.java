package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import de.ssp.ttr_rechner.model.NextGame;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorNextGames;

public class ServiceCallerNextGames extends MyTischtennisEnsureLoginCaller<Void, NextGame[]>
{
    public ServiceCallerNextGames(Context context, ServiceFinish<Void, NextGame[]> serviceFinish)
    {
        super(context, "Lade Begegnungen", serviceFinish);
    }

    @Override
    protected ParserEvaluator<Void, NextGame[]> getParserEvaluator() {
        return new ParserEvaluatorNextGames(context);
    }
}
