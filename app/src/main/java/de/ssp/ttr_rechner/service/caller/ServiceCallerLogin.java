package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.User;

import de.ssp.ttr_rechner.service.asynctask.MyTischtennisLoginService;

public class ServiceCallerLogin implements ServiceCaller
{
    private String username;
    private String password;
    private Context context;
    private ServiceReady<User> serviceReadyLogin;

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
        MyTischtennisLoginService serviceLogin = new MyTischtennisLoginService(context, serviceReadyLogin, username, password);
        serviceLogin.execute();
    }
}