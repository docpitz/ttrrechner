package de.ssp.ttr_rechner.ui.foundedplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.model.ChooseablePlayer;
import de.ssp.ttr_rechner.model.MyTischtennisCredentials;
import de.ssp.ttr_rechner.service.caller.ServiceCallerFindPlayerAvatarAdress;
import de.ssp.ttr_rechner.service.caller.ServiceFinish;

public class FoundedPlayersListAdapter extends ArrayAdapter<ChooseablePlayer>
{
    class ServiceFinishPlayerAvatar implements ServiceFinish<String, String>
    {
        ImageView imageView;
        ChooseablePlayer player;
        ServiceFinishPlayerAvatar(ImageView imageView, ChooseablePlayer player)
        {
            this.imageView = imageView;
            this.player = player;
        }

        @Override
        public void serviceFinished(String requestValue, boolean success, String url, String errorMessage)
        {
            PlayersImageLoader playersImageLoader = new PlayersImageLoader(getContext(), player, imageView);
            imgAdresses.put(player, url);
            playersImageLoader.loadImage2View(url);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            Log.d(this.getClass().toString(), "performFiltering");
            FilterResults results = new FilterResults();
            if(constraint != null)
            {
                String filterString = constraint.toString().toLowerCase();
                String filterStringArray[] = filterString.split(" ");
                if(filterStringArray.length > 1)
                {
                    String filterVorname = filterStringArray[0];
                    String filterNachname = filterStringArray[1];

                    final ChooseablePlayer[] list = originalPlayers;

                    int count = list.length;
                    final ArrayList<ChooseablePlayer> nlist = new ArrayList<ChooseablePlayer>(count);

                    ChooseablePlayer chooseablePlayer;

                    for (int i = 0; i < count; i++)
                    {
                        chooseablePlayer = list[i];
                        String firstname = chooseablePlayer.player.getFirstname().toLowerCase();
                        String lastname = chooseablePlayer.player.getLastname().toLowerCase();


                        if (firstname.contains(filterVorname) && lastname.contains(filterNachname))
                        {
                            nlist.add(chooseablePlayer);
                        }
                    }

                    results.values = ChooseablePlayer.getPlayers(nlist);
                    results.count = nlist.size();

                }

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.d(this.getClass().toString(), "publishResults");

            if(results.count > 0)
            {
                filteredPlayers = (ChooseablePlayer[]) results.values;
                notifyDataSetChanged();
            }
            else {
                filteredPlayers = new ChooseablePlayer[]{};
                notifyDataSetInvalidated();
            }


        }

    }

    protected final Context context;
    protected ChooseablePlayer[] originalPlayers;
    protected ChooseablePlayer[] filteredPlayers;

    protected boolean isSingleChoose;
    protected Filter filter;
    protected boolean isPlayersImageShow = new MyTischtennisCredentials(getContext()).isPlayersImageShow();
    protected FloatingActionButtonView floatingActionButtonView;
    protected ImageView errorImageView;

    protected @BindView(R.id.chkAuswahl) CheckBox chkAuswahl;
    protected @BindView(R.id.txtName) TextView txtName;
    protected @BindView(R.id.txtVerein) TextView txtVerein;
    protected @BindView(R.id.txtTTRPunkte) TextView txtTTRPunkte;
    protected @Nullable @BindView(R.id.imgPlayer) ImageView imgPlayer;
    protected @BindView(R.id.pnlRowFoundedPlayer) LinearLayout pnlRowFoundedPlayer;
    protected Map<ChooseablePlayer, String> imgAdresses = new HashMap<>();

    public FoundedPlayersListAdapter(Context context, ChooseablePlayer[] values, boolean isSingleChoose, FloatingActionButtonView floatingActionButtonView) {
        super(context, -1, values);
        this.context = context;
        this.originalPlayers = values;
        this.filteredPlayers = values;
        this.isSingleChoose = isSingleChoose;
        this.filter = new ItemFilter();
        this.floatingActionButtonView = floatingActionButtonView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int layoutId = isPlayersImageShow ? R.layout.players_founded_with_image_row : R.layout.players_founded_row;
        View rowView = inflater.inflate(layoutId, parent, false);
        ButterKnife.bind(this, rowView);

        ChooseablePlayer player = filteredPlayers[position];
        txtVerein.setText(player.player.getClub());
        txtName.setText(player.player.getFirstname()+ " " + player.player.getLastname());
        txtTTRPunkte.setText(String.valueOf(player.player.getTtrPoints()));
        chkAuswahl.setChecked(player.isChecked);
        if(isSingleChoose)
        {
            chkAuswahl.setVisibility(TextView.GONE);
        }
        chkAuswahl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            player.isChecked = isChecked;
            boolean isAnyPlayerChecked = false;
            for (ChooseablePlayer searchingPlayer:originalPlayers) {
                if(searchingPlayer.isChecked)
                {
                    isAnyPlayerChecked = true;
                }
            }
            if(floatingActionButtonView != null) {
                floatingActionButtonView.showFloatingActionButton(isAnyPlayerChecked);
            }
        });
        showPlayersImageIfUserRequested(player, (ListView) parent);

        return rowView;
    }

    public void updateData(ChooseablePlayer[] players)
    {
        Log.d(this.getClass().toString(), "updateData");
        this.originalPlayers = players;
        this.filteredPlayers = players;
    }

    @Override
    public int getCount() {
        return this.filteredPlayers.length;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    @Nullable
    @Override
    public ChooseablePlayer getItem(int position) {
        return filteredPlayers[position];
    }

    protected void showPlayersImageIfUserRequested(ChooseablePlayer player, ListView listView)
    {
        if(isPlayersImageShow)
        {
            initializePlayersPic(player, listView);
        }
    }

    protected void initializePlayersPic(ChooseablePlayer player, ListView listView)
    {

        imgPlayer.setImageDrawable(PlayersImageLoader.getNewCircularProgressDrawable(getContext()));
        listView.setRecyclerListener(view -> {
            if(view.getTag() != null) {
                ((ServiceCallerFindPlayerAvatarAdress) view.getTag()).cancelService();
            }
        });
        String adresse = imgAdresses.get(player);
        if(adresse != null)
        {
            PlayersImageLoader playersImageLoader = new PlayersImageLoader(getContext(), player, imgPlayer);
            playersImageLoader.loadImage2View(adresse);
        }
        else
        {
            FoundedPlayersListAdapter.ServiceFinishPlayerAvatar serviceFinishPlayerAvatar = new ServiceFinishPlayerAvatar(imgPlayer, player);
            ServiceCallerFindPlayerAvatarAdress serviceCaller = new ServiceCallerFindPlayerAvatarAdress(getContext(), serviceFinishPlayerAvatar, String.valueOf(player.player.getPersonId()));
            serviceCaller.callService();
            pnlRowFoundedPlayer.setTag(serviceCaller);
        }
    }


}
