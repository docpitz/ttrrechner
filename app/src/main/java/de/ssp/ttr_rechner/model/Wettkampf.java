package de.ssp.ttr_rechner.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Wettkampf
{
    private static final String TTR_WETTKAMPF_SAVE_NAME = "TTR_WETTKAMPF";

    private static final String MATCHES = "MATCHES";
    private static final String MEIN_TTR_WERT = "MEIN_TTR_WERT";

    public ArrayList<Match> matches;
    public int meinTTRWert;
    private Context context;
    private SharedPreferences ttrWettkampfSettings;

    public Wettkampf(Context context)
    {
        this.context = context;
        load();
    }

    public void save(ArrayList<Match> matches, int meinTTRWert)
    {
        this.matches = matches;
        this.meinTTRWert = meinTTRWert;
        ttrWettkampfSettings = context.getSharedPreferences(TTR_WETTKAMPF_SAVE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ttrWettkampfSettingsEditor = ttrWettkampfSettings.edit();
        Gson gson = new Gson();
        ttrWettkampfSettingsEditor.putString(MATCHES, gson.toJson(matches));
        ttrWettkampfSettingsEditor.putInt(MEIN_TTR_WERT, meinTTRWert);

        ttrWettkampfSettingsEditor.commit();
    }

    public void load()
    {
        ttrWettkampfSettings = context.getSharedPreferences(TTR_WETTKAMPF_SAVE_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Match>>(){}.getType();
        String jsonMatches = ttrWettkampfSettings.getString(MATCHES, gson.toJson(new ArrayList<Match>()));
        matches = gson.fromJson(jsonMatches, listType);
        meinTTRWert = ttrWettkampfSettings.getInt(MEIN_TTR_WERT, -1);
    }
}
