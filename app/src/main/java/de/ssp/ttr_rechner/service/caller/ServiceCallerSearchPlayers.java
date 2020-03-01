package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

import de.ssp.ttr_rechner.model.SearchPlayerResults;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorSearchPlayers;

public class ServiceCallerSearchPlayers extends MyTischtennisEnsureLoginCaller<List<SearchPlayer>, List<SearchPlayerResults>>
{
    private List<SearchPlayer> searchPlayerList;
    public ServiceCallerSearchPlayers(Context context, ServiceFinish<List<SearchPlayer>, List<SearchPlayerResults>> serviceFinish, List<SearchPlayer> searchPlayerList, boolean isViewLocked)
    {
        super(context, isViewLocked ? "Suche Spieler" : null, serviceFinish);
        this.searchPlayerList = searchPlayerList;
    }

    @Override
    protected ParserEvaluator<List<SearchPlayer>, List<SearchPlayerResults>> getParserEvaluator() {
        return new ParserEvaluatorSearchPlayers(searchPlayerList);
    }
}
