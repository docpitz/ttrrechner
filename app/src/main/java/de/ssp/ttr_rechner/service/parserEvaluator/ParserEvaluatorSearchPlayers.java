package de.ssp.ttr_rechner.service.parserEvaluator;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.ArrayList;
import java.util.List;

public class ParserEvaluatorSearchPlayers implements ParserEvaluator<List<SearchPlayer>, List<Player>>
{
    private List<SearchPlayer> searchPlayerList;
    private List<Player> foundedPlayer;
    private String errorMessage;

    public ParserEvaluatorSearchPlayers(List<SearchPlayer> searchPlayerList)
    {
        foundedPlayer = new ArrayList<>();
        this.searchPlayerList = searchPlayerList;
    }

    @Override
    public List<Player> evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        for (SearchPlayer searchPlayer:searchPlayerList) {
            List<Player> listPlayer = evaluateParser(searchPlayer);
            if(listPlayer != null && !listPlayer.isEmpty()) {
                foundedPlayer.add(listPlayer.get(0));
            }
        }
        return foundedPlayer;
    }

    @Override
    public List<SearchPlayer> getPostElement() {
        return searchPlayerList;
    }

    private List<Player> evaluateParser(SearchPlayer searchPlayer) throws ValidationException, NetworkException, LoginExpiredException
    {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> listPlayer = null;
        try {
            listPlayer = myTischtennisParser.findPlayer(searchPlayer);
        }
        catch(TooManyPlayersFound e)
        {
        }
        return listPlayer;
    }
}
