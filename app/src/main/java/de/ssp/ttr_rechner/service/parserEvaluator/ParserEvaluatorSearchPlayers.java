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

import de.ssp.ttr_rechner.model.PlayerChooseable;
import de.ssp.ttr_rechner.model.SearchPlayerResults;

public class ParserEvaluatorSearchPlayers implements ParserEvaluator<List<SearchPlayer>, List<SearchPlayerResults>>
{
    private List<SearchPlayer> searchPlayerList;
    private List<SearchPlayerResults> searchPlayerResultsList;
    private String errorMessage;

    public ParserEvaluatorSearchPlayers(List<SearchPlayer> searchPlayerList)
    {
        searchPlayerResultsList = new ArrayList<>();
        this.searchPlayerList = searchPlayerList;
    }

    @Override
    public List<SearchPlayerResults> evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        for (SearchPlayer searchPlayer:searchPlayerList) {
            SearchPlayerResults searchPlayerResults = evaluateParser(searchPlayer);
            if(searchPlayerResults != null) {

                searchPlayerResultsList.add(searchPlayerResults);
            }
        }
        return searchPlayerResultsList;
    }

    @Override
    public List<SearchPlayer> getPostElement() {
        return searchPlayerList;
    }

    private SearchPlayerResults evaluateParser(SearchPlayer searchPlayer) throws ValidationException, NetworkException, LoginExpiredException
    {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        List<Player> listPlayer = null;
        String error = null;

        try {
            listPlayer = myTischtennisParser.findPlayer(searchPlayer);
        }
        catch(TooManyPlayersFound e)
        {
            error = ParserEvaluatorSearchPlayer.ZU_VIELE_SPIELER_GEFUNDEN;
        }

        if((listPlayer == null || listPlayer.isEmpty()) &&  (error == null || error.isEmpty()))
        {
            error = ParserEvaluatorSearchPlayer.KEINE_SPIELER_GEFUNDEN;
        }

        List<PlayerChooseable> playerChooseableErrorableList = PlayerChooseable.convertFromPlayers((ArrayList)listPlayer);
        SearchPlayerResults searchPlayerResults = new SearchPlayerResults(searchPlayer, playerChooseableErrorableList, error);
        return searchPlayerResults;
    }
}
