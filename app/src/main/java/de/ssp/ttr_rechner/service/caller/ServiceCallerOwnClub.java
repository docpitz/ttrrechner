package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.Club;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorOwnClub;

public class ServiceCallerOwnClub extends MyTischtennisEnsureLoginCaller<Club>
{

    public ServiceCallerOwnClub(Context context, ServiceReady<Club> serviceReady)
    {
        super(context, "Lade eigenen Verein", serviceReady);
    }

    @Override
    protected ParserEvaluator<Club> getParserEvaluator() {
        return new ParserEvaluatorOwnClub(context);
    }
}
