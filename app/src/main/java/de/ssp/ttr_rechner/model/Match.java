package de.ssp.ttr_rechner.model;

public class Match
{
    private int gegnerischerTTRWert;
    private boolean gewonnen;
    private String name;
    private String verein;

    public Match(int gegnerischerTTRWert, boolean gewonnen, String name, String verein)
    {
        this.gegnerischerTTRWert = gegnerischerTTRWert;
        this.gewonnen = gewonnen;
        this.name = name;
        this.verein = verein;
    }

    public boolean isGewonnen() {
        return gewonnen;
    }

    public int getGegnerischerTTRWert() {
        return gegnerischerTTRWert;
    }

    public String getNameAndVerein() {
        String name = "Daniela Pitz";
        String verein = "TSV Hofolding";
        return name + " (" + verein + ")";
    }
}
