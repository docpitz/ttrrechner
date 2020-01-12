package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;

import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.service.asynctask.MyTischtennisService;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;

public abstract class MyTischtennisEnsureLoginCaller<T> implements ServiceCaller, ServiceReady<User>
{
    protected ServiceReady<T> serviceReady;
    protected Context context;
    protected String dialogMessage;

    public MyTischtennisEnsureLoginCaller(Context context, String dialogMessage, ServiceReady<T> serviceReady)
    {
        this.context = context;
        this.dialogMessage = dialogMessage;
        this.serviceReady = serviceReady;
    }

    protected void callLoggedInService()
    {
        MyTischtennisService<T> myTischtennisService = new MyTischtennisService<>(context, getParserEvaluator(), dialogMessage, serviceReady);
        myTischtennisService.execute();
    }

    protected abstract ParserEvaluator<T> getParserEvaluator();

    private boolean loginIfNecessary()
    {
        if(!LoginManager.existLoginCookie() || LoginManager.isLoginExpired() || MyTischtennisService.isLoginNecessary())
        {
            MyTischtennisCredentials credentials = new MyTischtennisCredentials(context);
            ServiceCallerLogin loginCaller = new ServiceCallerLogin(context, this, credentials.getUsername(), credentials.getPassword());
            loginCaller.callService();
            return true;
        }
        return false;
    }

    @Override
    public void serviceReady(boolean success, User user, String errorMessage)
    {
        // Anmeldeservice ist erfolgt
        if(success && user != null)
        {
            callLoggedInService();
        }
        else
        {
            serviceReady.serviceReady(success, null, errorMessage);
        }
    }

    @Override
    public void callService()
    {
        if(! loginIfNecessary())
        {
            callLoggedInService();
        }
    }
}
