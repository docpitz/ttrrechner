package de.ssp.ttr_rechner.rechner;

import java.util.ArrayList;

import de.ssp.ttr_rechner.model.Match;

public class TTRRechnerUtil
{

    private int aenderungsKonstante;
    private int eigenerTTRWert;

    public TTRRechnerUtil(int eigenerTTWert, int aenderungsKonstante)
    {
        this.aenderungsKonstante = aenderungsKonstante;
        this.eigenerTTRWert = eigenerTTWert;
    }

    public long berechne(ArrayList<Match> matches)
    {
        double gesamtwahrscheinlichkeit = 0;
        int anzahlSiege = 0;
        for (Match match: matches)
        {
            double einzelwahrscheinlichkeit = berechneGewinnwahrscheinlichkeit(match.getGegnerischerTTRWert());
            gesamtwahrscheinlichkeit = gesamtwahrscheinlichkeit + einzelwahrscheinlichkeit;
            anzahlSiege = anzahlSiege + (match.isGewonnen() ? + 1 : 0);
        }
        double aenderung = (anzahlSiege - gesamtwahrscheinlichkeit) * this.aenderungsKonstante;
        return Math.round(aenderung);
    }

    public long berechneGewonnen(int gegnerischerTTRWert)
    {
        double gewinnwahrscheinlichkeit = berechneGewinnwahrscheinlichkeit(gegnerischerTTRWert);
        double aenderungTTRWert = gewinnwahrscheinlichkeit * this.aenderungsKonstante;
        return Math.round(aenderungTTRWert);
    }

    double berechneGewinnwahrscheinlichkeit(int gegnerischerTTRWert)
    {
        int ttrA = this.eigenerTTRWert;
        int ttrB = gegnerischerTTRWert;
        double differenzTtrBTtrA = ttrB - ttrA;
        double exponent = differenzTtrBTtrA/150f;
        double pow = Math.pow(10,exponent);
        double divident = 1 + pow;
        return (1 / divident);
    }
}
