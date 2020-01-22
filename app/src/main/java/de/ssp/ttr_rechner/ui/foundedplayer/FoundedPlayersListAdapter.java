package de.ssp.ttr_rechner.ui.foundedplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Player;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.model.ChooseablePlayer;
import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.service.caller.ServiceCallerFindPlayerAvatarAdress;
import de.ssp.ttr_rechner.service.caller.ServiceFinish;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FoundedPlayersListAdapter extends ArrayAdapter<ChooseablePlayer>
{
    class ServiceFinishPlayerAvatar implements ServiceFinish<String>
    {
        ImageView imageView;
        Player player;
        ServiceFinishPlayerAvatar(ImageView imageView, Player player)
        {
            this.imageView = imageView;
            this.player = player;
        }

        @Override
        public void serviceFinished(boolean success, String url, String errorMessage)
        {
            imgAdresses.put(player, url);
            FoundedPlayersListAdapter.this.loadImagetoView(url, imageView);
        }
    }

    protected final Context context;
    protected final ChooseablePlayer[] players;
    protected boolean isSingleChoose;
    protected boolean isPlayersImageShow = new MyTischtennisCredentials(getContext()).isPlayersImageShow();
    protected ImageView errorImageView;

    protected @BindView(R.id.chkAuswahl) CheckBox chkAuswahl;
    protected @BindView(R.id.txtName) TextView txtName;
    protected @BindView(R.id.txtVerein) TextView txtVerein;
    protected @BindView(R.id.txtTTRPunkte) TextView txtTTRPunkte;
    protected @Nullable @BindView(R.id.imgPlayer) ImageView imgPlayer;
    protected @BindView(R.id.pnlRowFoundedPlayer) LinearLayout pnlRowFoundedPlayer;
    protected Map<Player, String> imgAdresses = new HashMap<>();

    public FoundedPlayersListAdapter(Context context, ChooseablePlayer[] values, boolean isSingleChoose) {
        super(context, -1, values);
        this.context = context;
        this.players = values;
        this.isSingleChoose = isSingleChoose;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //getErrorImage();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int layoutId = isPlayersImageShow ? R.layout.players_founded_with_image_row : R.layout.players_founded_row;
        View rowView = inflater.inflate(layoutId, parent, false);
        ButterKnife.bind(this, rowView);

        ChooseablePlayer player = players[position];
        txtVerein.setText(player.player.getClub());
        txtName.setText(player.player.getFirstname()+ " " + player.player.getLastname());
        txtTTRPunkte.setText(String.valueOf(player.player.getTtrPoints()));
        chkAuswahl.setChecked(player.isChecked);
        if(isSingleChoose)
        {
            chkAuswahl.setVisibility(TextView.GONE);
        }
        chkAuswahl.setOnCheckedChangeListener((buttonView, isChecked) -> player.isChecked = isChecked);
        showPlayersImageIfUserRequested(player.player, (ListView) parent);

        return rowView;
    }

    protected void showPlayersImageIfUserRequested(Player player, ListView listView)
    {
        if(isPlayersImageShow)
        {
            initializePlayersPic(player, listView);
        }
    }

    protected void initializePlayersPic(Player player, ListView listView)
    {
        imgPlayer.setImageDrawable(getNewCircularProgressDrawable());
        listView.setRecyclerListener(view -> {
            if(view.getTag() != null) {
                ((ServiceCallerFindPlayerAvatarAdress) view.getTag()).cancelService();
            }
        });
        String adresse = imgAdresses.get(player);
        if(adresse != null)
        {
            loadImagetoView(adresse, imgPlayer);
        }
        else
        {
            ServiceFinishPlayerAvatar serviceFinishPlayerAvatar = new ServiceFinishPlayerAvatar(imgPlayer, player);
            ServiceCallerFindPlayerAvatarAdress serviceCaller = new ServiceCallerFindPlayerAvatarAdress(getContext(), serviceFinishPlayerAvatar, String.valueOf(player.getPersonId()));
            serviceCaller.callService();
            pnlRowFoundedPlayer.setTag(serviceCaller);
        }
    }

    protected void loadImagetoView(String url, ImageView imageView)
    {
        Picasso.with(getContext().getApplicationContext())
                .load(url)
                .transform(new CropCircleTransformation())
                .placeholder(getNewCircularProgressDrawable())
                .error(R.drawable.error_image_player)
                .into(imageView);
    }

    private CircularProgressDrawable getNewCircularProgressDrawable()
    {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getContext());
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }
}
