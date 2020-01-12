package de.ssp.ttr_rechner.model;

import com.jmelzer.myttr.Player;

import java.util.ArrayList;

public class ChooseablePlayer
{
    public static ArrayList<ChooseablePlayer> convertFromPlayers(ArrayList<Player> playerArrayList)
    {
        ArrayList<ChooseablePlayer> returnList = new ArrayList<>();
        for (Player player: playerArrayList)
        {
            returnList.add(new ChooseablePlayer(player));
        }
        return returnList;
    }

    public Player player;
    public boolean isChecked;

    public ChooseablePlayer(Player player)
    {
        this.player = player;
    }

}
