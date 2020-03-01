package de.ssp.ttr_rechner.ui.util;

import android.content.Context;
import android.widget.ImageView;

import java.util.Map;

import de.ssp.ttr_rechner.model.PlayerChooseable;
import de.ssp.ttr_rechner.service.caller.ServiceFinish;

public class ServiceFinishPlayerAvatar implements ServiceFinish<String, String>
{
    protected ImageView imageView;
    protected PlayerChooseable player;
    protected Map<PlayerChooseable, String> imgAdresses;
    protected Context context;

    public ServiceFinishPlayerAvatar(ImageView imageView, PlayerChooseable player, Map<PlayerChooseable, String> imgAdresses, Context context)
    {
        this.context = context;
        this.imageView = imageView;
        this.player = player;
        this.imgAdresses = imgAdresses;
    }

    @Override
    public void serviceFinished(String requestValue, boolean success, String url, String errorMessage)
    {
        PlayersImageLoader playersImageLoader = new PlayersImageLoader(context, player, imageView);
        imgAdresses.put(player, url);
        playersImageLoader.loadImage2View(url);
    }
}