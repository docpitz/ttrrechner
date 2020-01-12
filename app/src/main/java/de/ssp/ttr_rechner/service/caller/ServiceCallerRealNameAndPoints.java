package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.User;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorRealNameAndPoints;

public class ServiceCallerRealNameAndPoints extends MyTischtennisEnsureLoginCaller<User>
{
    public ServiceCallerRealNameAndPoints(Context context, ServiceReady<User> serviceReady)
    {
        super(context, "Lade eigene Spielerdaten", serviceReady);
    }

    @Override
    protected ParserEvaluator<User> getParserEvaluator() {
        return new ParserEvaluatorRealNameAndPoints();
    }
}
