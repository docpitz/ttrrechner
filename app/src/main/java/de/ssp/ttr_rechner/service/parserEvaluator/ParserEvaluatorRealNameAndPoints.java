package de.ssp.ttr_rechner.service.parserEvaluator;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginException;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.ValidationException;

import de.ssp.ttr_rechner.service.parserEvaluator.ParserEvaluator;

public class ParserEvaluatorRealNameAndPoints implements ParserEvaluator<User>
{
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
            new LoginManager().logout();
            throw new ValidationException(e.getMessage());
        }
        return user;
    }
}
