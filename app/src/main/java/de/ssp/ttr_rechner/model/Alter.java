package de.ssp.ttr_rechner.model;

public enum Alter
{
    UNTER_16(0),
    UNTER_21(1),
    UEBER_21(2);

    int alter;
    Alter(int i)
    {
        alter = i;
    }
}
