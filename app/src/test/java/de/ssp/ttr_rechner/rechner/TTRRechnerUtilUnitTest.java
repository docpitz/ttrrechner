package de.ssp.ttr_rechner.rechner;

import org.junit.Test;

import java.util.ArrayList;

import de.ssp.ttr_rechner.model.Match;

import static org.junit.Assert.*;

public class TTRRechnerUtilUnitTest
{
    @Test
    public void testBerechne1650vs1650()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1650, 16);
        double aenderung = ttrRechnerUtil.berechneGewinnwahrscheinlichkeit(1650);

        assertEquals(0.5, aenderung, 0.01);
    }

    @Test
    public void testBerechne1650vs1648()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1650, 16);
        double aenderung = ttrRechnerUtil.berechneGewinnwahrscheinlichkeit(1648);

        assertEquals(0.51, aenderung, 0.01);
    }

    @Test
    public void testBerechne1650vs1800()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1650, 16);
        double aenderung = ttrRechnerUtil.berechneGewinnwahrscheinlichkeit(1800);

        assertEquals(0.09, aenderung, 0.01);
    }

    @Test
    public void testBerechne1650vs1500()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1650, 16);
        double aenderung = ttrRechnerUtil.berechneGewinnwahrscheinlichkeit(1500);

        assertEquals(0.91, aenderung, 0.01);
    }

    @Test
    public void testBerechne1650vs1750()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1650, 16);
        double aenderung = ttrRechnerUtil.berechneGewinnwahrscheinlichkeit(1750);

        assertEquals(0.18, aenderung, 0.01);
    }

    @Test
    public void testBerechneAenderungBeispielEinfach()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1615, 16);
        Match wildenauer = new Match(1593, true);
        ArrayList<Match> matchArrayList = new ArrayList<>();
        matchArrayList.add(wildenauer);
        long ttrAenderung = ttrRechnerUtil.berechneTTRAenderung(matchArrayList);

        assertEquals(7, ttrAenderung);
    }

    @Test
    public void testBerechneAenderungBeispielGroßerErfolg()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1576, 16);
        Match wolf = new Match(1625, true);
        Match brunhuber = new Match(1581, true);
        Match egger = new Match(1562, true);
        Match kahler = new Match(1635, true);
        Match schmidt = new Match(1594, true);
        Match weissenbach = new Match(1651, true);
        Match kirchberger = new Match(1688, true);
        Match betz = new Match(1645, false);

        ArrayList<Match> matchArrayList = new ArrayList<>();
        matchArrayList.add(wolf);
        matchArrayList.add(brunhuber);
        matchArrayList.add(egger);
        matchArrayList.add(kahler);
        matchArrayList.add(schmidt);
        matchArrayList.add(weissenbach);
        matchArrayList.add(kirchberger);
        matchArrayList.add(betz);

        long ttrAenderung = ttrRechnerUtil.berechneTTRAenderung(matchArrayList);
        assertEquals(68, ttrAenderung);
    }

    @Test
    public void testBerechneAenderungBeispielKleineNiederlage()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1662, 16);
        Match muthorst = new Match(1512, true);
        Match heim = new Match(1630, false);
        Match waechter = new Match(1512, false);

        ArrayList<Match> matchArrayList = new ArrayList<>();
        matchArrayList.add(muthorst);
        matchArrayList.add(heim);
        matchArrayList.add(waechter);

        long ttrAenderung = ttrRechnerUtil.berechneTTRAenderung(matchArrayList);
        assertEquals(-23, ttrAenderung);
    }

    @Test
    public void testBerechneAenderungBeispielKleineNiederlageMitLeerwert()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1662, 16);
        Match muthorst = new Match(1512, true);
        Match heim = new Match(1630, false);
        Match waechter = new Match(1512, false);
        Match leerWert1 = new Match(-1, true);
        Match leerWert2 = new Match(-1, false);

        ArrayList<Match> matchArrayList = new ArrayList<>();
        matchArrayList.add(muthorst);
        matchArrayList.add(heim);
        matchArrayList.add(waechter);
        matchArrayList.add(leerWert1);
        matchArrayList.add(leerWert2);

        long ttrAenderung = ttrRechnerUtil.berechneTTRAenderung(matchArrayList);
        assertEquals(-23, ttrAenderung);
    }

    @Test
    public void testBerechneAenderungBeispielAndereKonstante()
    {
        TTRRechnerUtil ttrRechnerUtil = new TTRRechnerUtil(1170, 24);
        Match coutureaV = new Match(921, true);
        Match haasL = new Match(837, true);
        Match coutureaM = new Match(789, true);
        Match bodensteiner = new Match(1121, true);
        Match haasS = new Match(857, true);
        Match pilgrim = new Match(926, true);

        ArrayList<Match> matchArrayList = new ArrayList<>();
        matchArrayList.add(coutureaV);
        matchArrayList.add(haasL);
        matchArrayList.add(coutureaM);
        matchArrayList.add(bodensteiner);
        matchArrayList.add(haasS);
        matchArrayList.add(pilgrim);

        long ttrAenderung = ttrRechnerUtil.berechneTTRAenderung(matchArrayList);
        assertEquals(9, ttrAenderung);
    }

}
