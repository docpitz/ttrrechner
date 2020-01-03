package de.ssp.ttr_rechner.model;

import android.content.Context;
import android.content.SharedPreferences;

public class MyTischtennisCredentials {

    public static final String FOCUS_ON_CREDENTIALS = "FOCUS_ON_CREDENTIALS";
    
    private static final String MY_TISCHTENNIS_CREDENTIALS_SAVE_NAME = "MY_TISCHTENNIS_CREDENTIALS_SAVE_NAME";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String MY_TISCHTENNIS_POSSIBLE = "MY_TISCHTENNIS_POSSIBLE";

    private SharedPreferences myTischtennisCredentials;

    /**
     * Konstruktor für TTRKonstante, lädt automatisch aus den Einstellungen die Werte aus.
     *
     * @param context
     */
    public MyTischtennisCredentials(Context context)
    {
        myTischtennisCredentials = context.getSharedPreferences(MY_TISCHTENNIS_CREDENTIALS_SAVE_NAME, Context.MODE_PRIVATE);
    }

    public void setCredentials(String username, String password, boolean isMyTischtennisLoginPossible)
    {
        save(username, password, isMyTischtennisLoginPossible);
    }

    public String getUsername()
    {
        return myTischtennisCredentials.getString(USERNAME, "");
    }

    public String getPassword()
    {
        return myTischtennisCredentials.getString(PASSWORD, "");
    }

    public boolean isMyTischtennisLoginPossible()
    {
        return myTischtennisCredentials.getBoolean(MY_TISCHTENNIS_POSSIBLE, true);
    }

    public boolean isSet()
    {
        return !getUsername().isEmpty() && !getPassword().isEmpty();
    }

    private void save(String username, String password, boolean isMyTischtennisLoginPossible)
    {
        SharedPreferences.Editor myTischtennisCredentialsEdit = myTischtennisCredentials.edit();
        myTischtennisCredentialsEdit.putBoolean(MY_TISCHTENNIS_POSSIBLE, isMyTischtennisLoginPossible);
        if(username != null && username != null)
        {
            myTischtennisCredentialsEdit.putString(USERNAME, username);
            myTischtennisCredentialsEdit.putString(PASSWORD, password);
        }
        else
        {
            myTischtennisCredentialsEdit.remove(USERNAME);
            myTischtennisCredentialsEdit.remove(PASSWORD);
        }

        myTischtennisCredentialsEdit.apply();
    }

}
