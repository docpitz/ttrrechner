package de.ssp.ttr_rechner.service.parserEvaluator;

import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

public class ParserEvaluatorSearchPlayer implements ParserEvaluator<List<Player>>
{
    private SearchPlayer searchPlayer;
    private String errorMessage;

    public ParserEvaluatorSearchPlayer(SearchPlayer searchPlayer)
    {
        this.searchPlayer = searchPlayer;
    }

    @Override
    public List<Player> evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> listPlayer = null;
        try {
            listPlayer = myTischtennisParser.findPlayer(searchPlayer);
        }
        catch(TooManyPlayersFound e)
        {
            throw new ValidationException("Es wurden zu viele Spieler gefunden.");
        }
        return listPlayer;
    }
}
