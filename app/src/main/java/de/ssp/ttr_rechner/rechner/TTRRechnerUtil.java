package de.ssp.ttr_rechner.rechner;

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
    private List<Match> matches;

    /**
     * @param eigenerTTRWert - Der eigene TTR-Wert
     * @param aenderungsKonstante - siehe oben
     */
    public TTRRechnerUtil(int eigenerTTRWert, int aenderungsKonstante, List<Match> matches)
    {
        this.aenderungsKonstante = aenderungsKonstante;
        this.eigenerTTRWert = eigenerTTRWert;
        this.matches = matches;
    }

    public long berechneNeueTTRPunkte()
    {
        return eigenerTTRWert + berechneTTRAenderung();
    }

    /**
     * Berechnet die Änderung der TTR-Punkte die durch
     * alle Spiele während eines "Spieltages" passiert sind
     * @return die Änderung des TTR-Wertes
     */
    public long berechneTTRAenderung()
    {
        double gesamtWahrscheinlichkeit = 0;
        int anzahlSiege = 0;
        for (Match match: matches)
        {
            if(match.getGegnerischerTTRWert() > 0)
            {
                double einzelwahrscheinlichkeit = berechneGewinnwahrscheinlichkeit(match.getGegnerischerTTRWert());
                gesamtWahrscheinlichkeit = gesamtWahrscheinlichkeit + einzelwahrscheinlichkeit;
                anzahlSiege = anzahlSiege + (match.isGewonnen() ? +1 : 0);
            }
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
