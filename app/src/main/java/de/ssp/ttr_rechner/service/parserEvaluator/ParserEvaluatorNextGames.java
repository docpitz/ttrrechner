package de.ssp.ttr_rechner.service.parserEvaluator;

import android.content.Intent;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.activities.EnterClubNameActivity;
import com.jmelzer.myttr.logic.AppointmentParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.logic.ValidationException;

import java.util.List;

public class ParserEvaluatorNextGames implements ParserEvaluator<List<TeamAppointment>>
{
    @Override
    public List<TeamAppointment> evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
        String name = myTischtennisParser.getNameOfOwnClub();
        List<TeamAppointment> teamAppointmentList = null;
        if (name != null) {
            AppointmentParser appointmentParser = new AppointmentParser();
            teamAppointmentList = appointmentParser.read(name);
        } else {
            String errorMessage = "Konnte den Namen deines Vereins nicht ermitteln. Wahrscheinlich ein Fehler bei mytischtennis.de." +
                    "Du kannst ihn aber in den Einstellungen selbst eingeben.";
            throw new ValidationException(errorMessage);

        }
        return teamAppointmentList;
    }
}
