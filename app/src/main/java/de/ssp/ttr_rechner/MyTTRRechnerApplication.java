package de.ssp.ttr_rechner;

import android.app.Application;

import com.jmelzer.myttr.db.DataBaseHelper;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;

public class MyTTRRechnerApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        //Datenbank anlegen
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
        dataBaseHelper.registerAdapter(new LoginDataBaseAdapter(this));
    }
}
