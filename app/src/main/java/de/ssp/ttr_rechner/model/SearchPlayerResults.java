package de.ssp.ttr_rechner.model;

import com.jmelzer.myttr.model.SearchPlayer;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;

public class SearchPlayerResults implements Serializable {
    SearchPlayer searchPlayer;
    List<PlayerChooseable> searchPlayerResultsList;
    String error;

    public SearchPlayerResults(@NonNull SearchPlayer searchPlayer, List<PlayerChooseable> searchPlayerResultsList, String error) {
        this.searchPlayer = searchPlayer;
        this.searchPlayerResultsList = searchPlayerResultsList;
        this.error = error;
    }

    public SearchPlayer getSearchPlayer() {
        return searchPlayer;
    }

    public List<PlayerChooseable> getSearchPlayerResultsList() {
        return searchPlayerResultsList;
    }

    public String getError() {
        return error;
    }
}
