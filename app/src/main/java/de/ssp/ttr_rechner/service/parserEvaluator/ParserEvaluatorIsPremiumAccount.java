package de.ssp.ttr_rechner.service.parserEvaluator;

import android.util.Log;

import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.ValidationException;

public class ParserEvaluatorIsPremiumAccount implements ParserEvaluator<Boolean>
{
    @Override
    public Boolean evaluateParser() throws NoDataException, NetworkException, LoginExpiredException, ValidationException, NoClickTTException, NiceGuysException
    {
        int points = -1;
        try
        {
            points = new MyTischtennisParser().getPoints();
        }
        catch (PlayerNotWellRegistered e)
        {
            Log.e(this.getClass().toString(), e.getMessage());
        }

        return points != -3;
    }
}
