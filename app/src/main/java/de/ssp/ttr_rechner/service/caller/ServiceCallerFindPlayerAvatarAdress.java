package de.ssp.ttr_rechner.service.caller;

import android.content.Context;
import android.os.AsyncTask;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorFindPlayerAvatarAdresse;

public class ServiceCallerFindPlayerAvatarAdress extends MyTischtennisEnsureLoginCaller<String, String>
{
    protected String playersId;
    public ServiceCallerFindPlayerAvatarAdress(Context context, ServiceFinish<String, String> serviceFinish, String playersId)
    {
        super(context, null, serviceFinish, AsyncTask.THREAD_POOL_EXECUTOR);
        this.playersId = playersId;
    }

    @Override
    protected ParserEvaluator<String, String> getParserEvaluator() {
        return new ParserEvaluatorFindPlayerAvatarAdresse(playersId);
    }
}
