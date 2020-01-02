package de.ssp.ttr_rechner.service.caller;

import android.content.Context;

import com.jmelzer.myttr.TeamAppointment;

import java.util.List;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;
import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluatorNextGames;

public class ServiceCallerNextGames extends MyTischtennisEnsureLoginCaller<List<TeamAppointment>>
{
    public ServiceCallerNextGames(Context context, ServiceReady<List<TeamAppointment>> serviceReady)
    {
        super(context, "Lade Begegnungen", serviceReady);
    }

    @Override
    protected ParserEvaluator<List<TeamAppointment>> getParserEvaluator() {
        return new ParserEvaluatorNextGames();
    }
}
