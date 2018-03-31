package de.ssp.ttr_rechner.rechner;

import java.util.ArrayList;
import java.util.List;

import de.ssp.ttr_rechner.model.Match;

public class TTRRechnerUtil
{
    /**
     16 als Grundwert
     +4 für 15 Einzel, wenn es in den letzten 365 Tagen keine bewertete Veranstaltung des Spielers
     gegeben hat
     +4, wenn die Anzahl bewerteter Einzel des Spielers < 30 ist
     +4, wenn das Alter des Spielers < 21 Jahre ist
     +4, wenn das Alter des Spielers < 16 Jahre ist
     */
    private int aenderungsKonstante;
    private int eigenerTTRWert;

    /**
     * @param eigenerTTRWert - Der eigene TTR-Wert
     * @param aenderungsKonstante - siehe oben
     */
    public TTRRechnerUtil(int eigenerTTRWert, int aenderungsKonstante)
    {
        this.aenderungsKonstante = aenderungsKonstante;
        this.eigenerTTRWert = eigenerTTRWert;
    }

    /**
     * Berechnet die Änderung der TTR-Punkte die durch
     * alle Spiele während eines "Spieltages" passiert
     * @param matches - Alle Spiele an einem Spieltag
     * @return die Änderung des TTR-Wertes
     */
    public long berechneAenderung(List<Match> matches)
    {
        double gesamtWahrscheinlichkeit = 0;
        int anzahlSiege = 0;
        for (Match match: matches)
        {
            double einzelwahrscheinlichkeit = berechneGewinnwahrscheinlichkeit(match.getGegnerischerTTRWert());
            gesamtWahrscheinlichkeit = gesamtWahrscheinlichkeit + einzelwahrscheinlichkeit;
            anzahlSiege = anzahlSiege + (match.isGewonnen() ? + 1 : 0);
        }
        double aenderung = (anzahlSiege - gesamtWahrscheinlichkeit) * this.aenderungsKonstante;
        return Math.round(aenderung);
    }

    /**
     * // 1 / (1 + 10 ^ (TTR_B - TTR_A / 150))
     * @param gegnerischerTTRWert
     * @return Gewinnwahrscheinlichkeit
     */
    double berechneGewinnwahrscheinlichkeit(int gegnerischerTTRWert)
    {

        int ttrA = this.eigenerTTRWert;
        int ttrB = gegnerischerTTRWert;
        double differenzTtrBTtrA = ttrB - ttrA;
        double exponent = differenzTtrBTtrA/150f;
        double hoch = Math.pow(10,exponent);
        double divident = 1 + hoch;
        return (1 / divident);
    }
}
