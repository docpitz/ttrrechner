package de.ssp.ttr_rechner.model;

import android.graphics.drawable.Drawable;

import com.jmelzer.myttr.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerChooseable implements Serializable
{
    public static ArrayList<PlayerChooseable> convertFromPlayers(ArrayList<Player> playerArrayList)
    {
        ArrayList<PlayerChooseable> returnList = new ArrayList<>();
        if(playerArrayList != null && ! playerArrayList.isEmpty())
        {
            for (Player player: playerArrayList)
            {
                returnList.add(new PlayerChooseable(player));
            }
        }
        return returnList;
    }

    public static PlayerChooseable[] getPlayers(ArrayList<PlayerChooseable> players)
    {
        return players.toArray(new PlayerChooseable[players.size()]);
    }

    public Player player;
    public boolean isChecked;
    public Drawable playersAvatar;

    public PlayerChooseable(Player player)
    {
        this.player = player;
    }

}
