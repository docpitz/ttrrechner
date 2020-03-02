package de.ssp.ttr_rechner.ocr.playerrecognition;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public interface PlayerFinder
{
    public ArrayList<SearchPlayer> search(@NonNull FirebaseVisionText results);
}
