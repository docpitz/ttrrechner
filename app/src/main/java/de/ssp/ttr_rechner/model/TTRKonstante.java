package de.ssp.ttr_rechner.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Speichert die Werte für die TTRKonstante
 * Automatisches Speichern und Auslesen der Werte aus den SharedPreferences
 */
public class TTRKonstante {

    private static final int TTR_KONSTANTE = 16;
    private static final int TTRKONSTANTE_ERHOEHUNG = 4;

    private static final String TTR_KONSTANTE_SAVE_NAME = "TTR_KONSTANTE";

    private static final String UEBER_EIN_JAHR_OHNE_SPIEL = "UEBER_EIN_JAHR_OHNE_SPIEL";
    private static final String WENIGER_ALS_15_SPIELE = "WENIGER_ALS_15_SPIELE";
    private static final String UNTER_21_JAHRE = "UNTER_21_JAHRE";
    private static final String UNTER_16_JAHRE = "UNTER_16_JAHRE";

    private boolean ueberEinJahrOhneSpiel;
    private boolean wenigerAls15Spiele;
    private boolean unter21Jahre;
    private boolean unter16Jahre;

    private SharedPreferences ttrKonstanteSettings;

    /**
     * Konstruktor für TTRKonstante, lädt automatisch aus den Einstellungen die Werte aus.
     *
     * @param context
     */
    public TTRKonstante(Context context)
    {
        ttrKonstanteSettings = context.getSharedPreferences(TTR_KONSTANTE_SAVE_NAME, Context.MODE_PRIVATE);
        ueberEinJahrOhneSpiel = ttrKonstanteSettings.getBoolean(UEBER_EIN_JAHR_OHNE_SPIEL, false);
        wenigerAls15Spiele = ttrKonstanteSettings.getBoolean(WENIGER_ALS_15_SPIELE, false);
        unter16Jahre = ttrKonstanteSettings.getBoolean(UNTER_16_JAHRE, false);
        unter21Jahre = ttrKonstanteSettings.getBoolean(UNTER_21_JAHRE,false);
    }

    public boolean getUeberEinJahrOhneSpiel()
    {
        return ueberEinJahrOhneSpiel;
    }

    public boolean getWenigerAls15Spiele()
    {
        return wenigerAls15Spiele;
    }

    public boolean getUnter21Jahre()
    {
        return unter21Jahre;
    }

    public boolean getUnter16Jahre()
    {
        return unter16Jahre;
    }

    public void setUeberEinJahrOhneSpiel(boolean ueberEinJahrOhneSpiel)
    {
        this.ueberEinJahrOhneSpiel = ueberEinJahrOhneSpiel;
        save();
    }

    public void setWenigerAls15Spiele(boolean wenigerAls15Spiele)
    {
        this.wenigerAls15Spiele = wenigerAls15Spiele;
        save();
    }

    public void setUnter21Jahre(boolean unter21Jahre)
    {
        this.unter21Jahre = unter21Jahre;
        save();
    }

    public void setUnter16Jahre(boolean unter16Jahre)
    {
        this.unter16Jahre = unter16Jahre;
        save();
    }

    private void save()
    {
        SharedPreferences.Editor ttrKonstanteSettingsEditor = ttrKonstanteSettings.edit();
        ttrKonstanteSettingsEditor.putBoolean(UEBER_EIN_JAHR_OHNE_SPIEL ,ueberEinJahrOhneSpiel);
        ttrKonstanteSettingsEditor.putBoolean(WENIGER_ALS_15_SPIELE, wenigerAls15Spiele);
        ttrKonstanteSettingsEditor.putBoolean(UNTER_16_JAHRE, unter16Jahre);
        ttrKonstanteSettingsEditor.putBoolean(UNTER_21_JAHRE, unter21Jahre);
        ttrKonstanteSettingsEditor.apply();
    }

    /**
     * berechnet die Höhe der Änderungskonstante
     * @return die Änderungskonstante
     */
    public int getTTRKonstante ()
    {
        int ttrKonstante = TTR_KONSTANTE;

        if (ueberEinJahrOhneSpiel)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG;
        }
        if (wenigerAls15Spiele)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG;
        }
        if (unter16Jahre)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG;
        }
        if (unter21Jahre)
        {
            ttrKonstante = ttrKonstante + TTRKONSTANTE_ERHOEHUNG;
        }
        return ttrKonstante;
    }

}
