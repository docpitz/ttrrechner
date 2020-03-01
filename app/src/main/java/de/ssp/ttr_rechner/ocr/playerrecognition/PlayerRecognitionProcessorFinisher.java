package de.ssp.ttr_rechner.ocr.playerrecognition;

import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;

public interface PlayerRecognitionProcessorFinisher
{
    public void ocrFinished(ArrayList<SearchPlayer> searchPlayers);
}
