package de.ssp.ttr_rechner.service.caller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jmelzer.myttr.User;

import de.ssp.ttr_rechner.service.asynctask.MyTischtennisLoginService;

public class ServiceCallerLogin implements ServiceCaller
{
    private String username;
    private String password;
    private Context context;
    private ServiceReady<User> serviceReadyLogin;
    private MyTischtennisLoginService serviceLogin;

    public ServiceCallerLogin(Context context, ServiceReady<User> serviceReadyLogin, String username, String password)
    {
        this.context = context;
        this.username = username;
        this.password = password;
        this.serviceReadyLogin = serviceReadyLogin;
    }

    @Override
    public void callService()
    {
        serviceLogin = new MyTischtennisLoginService(context, serviceReadyLogin, username, password);
        Log.d(this.toString(), "Service started");
        serviceLogin.execute();
    }

    @Override
    public void cancelService() {
        Log.d(this.toString(), "Service stopped");
        if(serviceLogin != null)
        {
            serviceLogin.cancel(true);
        }
    }

    @Override
    public boolean isServiceRunning() {
        return serviceLogin != null && serviceLogin.getStatus() == AsyncTask.Status.RUNNING;
    }
}