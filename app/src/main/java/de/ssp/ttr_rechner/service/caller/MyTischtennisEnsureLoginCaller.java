package de.ssp.ttr_rechner.service.caller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;

import java.util.concurrent.Executor;

import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.service.asynctask.MyTischtennisService;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;

public abstract class MyTischtennisEnsureLoginCaller<T> implements ServiceCaller, ServiceReady<User>
{
    protected ServiceReady<T> serviceReady;
    protected Context context;
    protected String dialogMessage;
    protected Executor executor;
    protected MyTischtennisService<T> myTischtennisService;
    protected ServiceCallerLogin loginCaller;

    public MyTischtennisEnsureLoginCaller(Context context, String dialogMessage, ServiceReady<T> serviceReady, Executor executor)
    {
        this.context = context;
        this.dialogMessage = dialogMessage;
        this.serviceReady = serviceReady;
        this.executor = executor;
    }

    public MyTischtennisEnsureLoginCaller(Context context, String dialogMessage, ServiceReady<T> serviceReady)
    {
        this(context, dialogMessage, serviceReady, null);
    }

    protected void callLoggedInService()
    {
        myTischtennisService = new MyTischtennisService<>(context, getParserEvaluator(), dialogMessage, serviceReady);
        Log.d(this.toString(), "Service started");
        if(executor != null)
        {
            myTischtennisService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            myTischtennisService.execute();
        }
    }

    public void cancelService()
    {
        if(isServiceRunning())
        {
            Log.d(this.toString(), "Service stopped");
            myTischtennisService.cancel(true);
        }
        if(loginCaller != null && loginCaller.isServiceRunning())
        {
            Log.d(loginCaller.toString(), "Service stopped");
            loginCaller.cancelService();
        }
    }

    public boolean isServiceRunning()
    {
        return myTischtennisService != null && myTischtennisService.getStatus() == AsyncTask.Status.RUNNING;
    }

    protected abstract ParserEvaluator<T> getParserEvaluator();

    private boolean loginIfNecessary()
    {
        if(!LoginManager.existLoginCookie() || LoginManager.isLoginExpired() || MyTischtennisService.isLoginNecessary())
        {
            MyTischtennisCredentials credentials = new MyTischtennisCredentials(context);
            loginCaller = new ServiceCallerLogin(context, this, credentials.getUsername(), credentials.getPassword());
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
