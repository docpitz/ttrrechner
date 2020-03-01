package de.ssp.ttr_rechner.ui.ocr;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.model.PlayerChooseable;
import de.ssp.ttr_rechner.service.caller.ServiceCallerFindPlayerAvatarAdress;
import de.ssp.ttr_rechner.ui.util.PlayersImageLoader;
import de.ssp.ttr_rechner.ui.util.ServiceFinishPlayerAvatar;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class SearchResultsSection extends Section {

    public static final String LOADING = "Loading";

    protected final String title;
    protected final List<PlayerChooseable> list;
    protected final ClickListener clickListener;
    protected String errorMessage;
    protected int foundedPlayersCount;
    protected Context context;
    protected boolean isPlayerImageShow;
    protected RecyclerView recyclerView;
    protected Map<PlayerChooseable, String> imgAdresses = new HashMap<>();

    public SearchResultsSection(@NonNull final String title,
                                @NonNull final List<PlayerChooseable> list,
                                String errorMessage,
                                @NonNull final ClickListener clickListener,
                                boolean isPlayerImageShow,
                                Context context,
                                RecyclerView recyclerView) {

        super(SectionParameters.builder()
                .itemResourceId(isPlayerImageShow ? R.layout.players_founded_with_image_row : R.layout.players_founded_row)
                .headerResourceId(R.layout.ocr_founded_header)
                .failedResourceId(R.layout.ocr_not_founded_row)
                .loadingResourceId(R.layout.ocr_founded_loading_indicator)
                .build());

        this.recyclerView = recyclerView;
        this.context = context;
        this.title = title;
        this.list = list;
        this.errorMessage = errorMessage;
        this.clickListener = clickListener;
        this.isPlayerImageShow = isPlayerImageShow;
        if(list != null)
        {
            foundedPlayersCount = list.size();
        }

        if(foundedPlayersCount == 0 && LOADING.equals(errorMessage))
        {
            setState(State.LOADING);

        }
        else if(foundedPlayersCount == 0)
        {
            setState(State.FAILED);
        }
        else
        {
            setState(State.LOADED);
        }
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getFailedViewHolder(View view) {
        return new ErrorRowViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getLoadingViewHolder(View view) {
        return new LoadingViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new FoundedPlayerRowViewHolder(view, context);
    }

    @Override
    public void onBindFailedViewHolder(RecyclerView.ViewHolder holder) {
        final ErrorRowViewHolder itemHolder = (ErrorRowViewHolder) holder;
        itemHolder.txtError.setText(errorMessage);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final FoundedPlayerRowViewHolder itemHolder = (FoundedPlayerRowViewHolder) holder;
        PlayerChooseable foundedPlayer = list.get(position);
        itemHolder.txtName.setText(foundedPlayer.player.getFirstname() + " " + foundedPlayer.player.getLastname());
        itemHolder.txtVerein.setText(foundedPlayer.player.getClub());
        itemHolder.txtPunkte.setText(String.valueOf(foundedPlayer.player.getTtrPoints()));
        itemHolder.chkAuswahl.setChecked(foundedPlayer.isChecked);
        itemHolder.chkAuswahl.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(foundedPlayer, itemHolder));
        itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(foundedPlayer, itemHolder)
        );

        if(isPlayerImageShow)
        {
            itemHolder.imgPlayersAvatar.setImageDrawable(PlayersImageLoader.getNewCircularProgressDrawable(context));
            showPlayersImageIfUserRequested(foundedPlayer, recyclerView, itemHolder.imgPlayersAvatar, itemHolder.pnlRowFoundedPlayer);
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new FoundedPlayerHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final FoundedPlayerHeaderViewHolder headerHolder = (FoundedPlayerHeaderViewHolder) holder;

        headerHolder.tvTitle.setText(title);
    }

    public interface ClickListener
    {
        void onItemRootViewClicked(@NonNull PlayerChooseable playerChooseable, FoundedPlayerRowViewHolder viewHolder);
    }

    protected void showPlayersImageIfUserRequested(PlayerChooseable player, RecyclerView recyclerView, ImageView imgPlayersAvatar, LinearLayout pnlRowFoundedPlayer)
    {
        if(isPlayerImageShow)
        {
            initializePlayersPic(player, recyclerView, imgPlayersAvatar, pnlRowFoundedPlayer);
        }
    }

    protected void initializePlayersPic(PlayerChooseable player, RecyclerView recyclerView, ImageView imgPlayersAvatar, LinearLayout pnlRowFoundedPlayer)
    {
        imgPlayersAvatar.setImageDrawable(PlayersImageLoader.getNewCircularProgressDrawable(context));
        if(recyclerView != null) {
            recyclerView.setRecyclerListener(view -> {
                if (pnlRowFoundedPlayer.getTag() != null) {
                    ((ServiceCallerFindPlayerAvatarAdress) pnlRowFoundedPlayer.getTag()).cancelService();
                }
            });
        }
        String adresse = imgAdresses.get(player);
        if(adresse != null)
        {
            PlayersImageLoader playersImageLoader = new PlayersImageLoader(context, player, imgPlayersAvatar);
            playersImageLoader.loadImage2View(adresse);
        }
        else
        {
            ServiceFinishPlayerAvatar serviceFinishPlayerAvatar = new ServiceFinishPlayerAvatar(imgPlayersAvatar, player, imgAdresses, context);
            ServiceCallerFindPlayerAvatarAdress serviceCaller = new ServiceCallerFindPlayerAvatarAdress(context, serviceFinishPlayerAvatar, String.valueOf(player.player.getPersonId()));
            serviceCaller.callService();
            pnlRowFoundedPlayer.setTag(serviceCaller);
        }
    }
}
