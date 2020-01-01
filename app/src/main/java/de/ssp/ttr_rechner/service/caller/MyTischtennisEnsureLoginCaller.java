package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;

import de.ssp.ttr_rechner.model.MyTischtennisCredentials;

public abstract class MyTischtennisEnsureLoginCaller<T> implements ServiceCaller, ServiceReady<User>
{
    protected ServiceReady<T> serviceReady;
    protected Context context;

    public MyTischtennisEnsureLoginCaller(Context context, ServiceReady<T> serviceReady)
    {
        this.context = context;
        this.serviceReady = serviceReady;
    }

    protected abstract void callLoggedInService();

    private boolean loginIfNecessary()
    {
        if(!LoginManager.existLoginCookie() || LoginManager.isLoginExpired())
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
