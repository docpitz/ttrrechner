package de.ssp.ttr_rechner.model;

import com.jmelzer.myttr.model.SearchPlayer;

public class SearchPlayerUtil
{
    SearchPlayer searchPlayer;
    public SearchPlayerUtil(SearchPlayer searchPlayer)
    {
        this.searchPlayer = searchPlayer;
    }

    public String getFullName()
    {
        return searchPlayer.getFirstname() + " " + searchPlayer.getLastname();
    }

    public String getFullNameWithClub()
    {
        return getFullName() + getClubText();
    }

    private String getClubText()
    {
        if(searchPlayer.getClub() != null) {
            return  ", " + searchPlayer.getClub().getName();
        }
        return "";
    }
}
