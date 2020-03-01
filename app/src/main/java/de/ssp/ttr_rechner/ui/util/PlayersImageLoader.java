package de.ssp.ttr_rechner.ui.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.model.PlayerChooseable;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PlayersImageLoader implements Callback
{
    protected PlayerChooseable player;
    protected ImageView imageView;
    protected Context context;

    public PlayersImageLoader(Context context, PlayerChooseable player, ImageView imageView)
    {
        this.context = context;
        this.player = player;
        this.imageView = imageView;
    }

    public void loadImage2View(String url)
    {
        Picasso.with(context.getApplicationContext())
                .load(url)
                .transform(new CropCircleTransformation())
                .placeholder(getNewCircularProgressDrawable(context))
                .error(R.drawable.error_image_player)
                .into(imageView, this);
    }

    public static CircularProgressDrawable getNewCircularProgressDrawable(Context context)
    {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }

    @Override
    public void onSuccess() {
        player.playersAvatar = imageView.getDrawable();
    }

    @Override
    public void onError() {

    }
}
