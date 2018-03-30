package de.ssp.ttr_rechner.model;

public class Match
{
    private int gegnerischerTTRWert;
    private boolean gewonnen;

    public Match(int gegnerischerTTRWert, boolean gewonnen)
    {
        this.gegnerischerTTRWert = gegnerischerTTRWert;
        this.gewonnen = gewonnen;
    }

    public boolean isGewonnen() {
        return gewonnen;
    }

    public int getGegnerischerTTRWert() {
        return gegnerischerTTRWert;
    }
}
