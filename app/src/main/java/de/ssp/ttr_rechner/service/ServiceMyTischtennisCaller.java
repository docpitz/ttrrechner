package de.ssp.ttr_rechner.service;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;

public class ServiceMyTischtennisCaller
{
    public User login()
    {
        LoginManager.isLoginExpired();
        return null;
    }

}
