package de.ssp.ttr_rechner.model;

import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.Verein;

import java.io.IOException;

public class MyTischtennis {

    public int getMyPoints()
    {
        LoginManager loginManager = new LoginManager();
        try
        {
            User user = loginManager.login("Florian Pitz", "ping9913");
            return user.getPoints();
        }
        catch (IOException e)
        {

        }
        catch (PlayerNotWellRegistered e)
        {

        }
        catch (LoginException e)
        {

        }
        catch (NetworkException e)
        {

        }
        catch (ValidationException e)
        {

        }

        return 0;
    }
}
