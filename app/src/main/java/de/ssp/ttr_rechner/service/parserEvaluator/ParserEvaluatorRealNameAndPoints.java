package de.ssp.ttr_rechner.service.parserEvaluator;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginException;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.ValidationException;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;

public class ParserEvaluatorRealNameAndPoints implements ParserEvaluator<User>
{
    String errorMessage;
    @Override
    public User evaluateParser() throws NetworkException, LoginExpiredException, ValidationException
    {
        User user = null;
        try
        {
            user = new MyTischtennisParser().getPointsAndRealName();
        }
        catch (PlayerNotWellRegistered | LoginException e)
        {
            // Sollte an der Stelle nicht auftreten da bereits eingeloggt
            errorMessage = e.getMessage();
        }
        return user;
    }

    @Override
    public String getErrorMessageFromEvaluator() {
        return errorMessage;
    }

    @Override
    public String getProgressDialogMessage() {
        return "Hole Spielerdaten";
    }
}
