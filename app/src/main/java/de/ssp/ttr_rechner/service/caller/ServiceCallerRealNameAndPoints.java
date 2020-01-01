package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.User;

import de.ssp.ttr_rechner.service.asynctask.MyTischtennisService;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorRealNameAndPoints;

public class ServiceCallerRealNameAndPoints extends MyTischtennisEnsureLoginCaller<User>
{
    public ServiceCallerRealNameAndPoints(Context context, ServiceReady<User> serviceReady)
    {
        super(context, serviceReady);
    }

    @Override
    public void callLoggedInService()
    {
        ParserEvaluatorRealNameAndPoints parserEvaluatorRealNameAndPoints = new ParserEvaluatorRealNameAndPoints();
        MyTischtennisService<User> myTischtennisService = new MyTischtennisService<>(context, parserEvaluatorRealNameAndPoints, serviceReady);
        myTischtennisService.execute();
    }
}
