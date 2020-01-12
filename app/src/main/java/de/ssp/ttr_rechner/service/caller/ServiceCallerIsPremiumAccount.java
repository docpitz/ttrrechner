package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorIsPremiumAccount;

public class ServiceCallerIsPremiumAccount extends MyTischtennisEnsureLoginCaller<Boolean>
{
    public ServiceCallerIsPremiumAccount(Context context, ServiceReady<Boolean> serviceReady)
    {
        super(context, "Suche nach Premium-Account", serviceReady);
    }

    @Override
    protected ParserEvaluator<Boolean> getParserEvaluator() {
        return new ParserEvaluatorIsPremiumAccount();
    }
}
