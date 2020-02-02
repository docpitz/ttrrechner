package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorIsPremiumAccount;

public class ServiceCallerIsPremiumAccount extends MyTischtennisEnsureLoginCaller<Void, Boolean>
{
    public ServiceCallerIsPremiumAccount(Context context, ServiceFinish<Void, Boolean> serviceFinish)
    {
        super(context, "Suche nach Premium-Account", serviceFinish);
    }

    @Override
    protected ParserEvaluator<Void, Boolean> getParserEvaluator() {
        return new ParserEvaluatorIsPremiumAccount();
    }
}
