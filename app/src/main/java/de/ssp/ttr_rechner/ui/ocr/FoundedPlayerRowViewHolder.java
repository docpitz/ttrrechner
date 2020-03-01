package de.ssp.ttr_rechner.ui.ocr;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.ssp.ttr_rechner.R;

public final class FoundedPlayerRowViewHolder extends RecyclerView.ViewHolder {

    View rootView;
    TextView txtName;
    TextView txtPunkte;
    TextView txtVerein;
    CheckBox chkAuswahl;
    ImageView imgPlayersAvatar = null;
    boolean isPlayersImageShow;
    Context context;
    LinearLayout pnlRowFoundedPlayer;

    FoundedPlayerRowViewHolder(@NonNull View view, Context context) {
        super(view);

        this.isPlayersImageShow = isPlayersImageShow;
        this.context = context;
        rootView = view;
        txtName = view.findViewById(R.id.txtName);
        txtVerein = view.findViewById(R.id.txtError);
        txtPunkte = view.findViewById(R.id.txtTTRPunkte);
        chkAuswahl = view.findViewById(R.id.chkAuswahl);
        imgPlayersAvatar = view.findViewById(R.id.imgPlayer);
        pnlRowFoundedPlayer = view.findViewById(R.id.pnlRowFoundedPlayer);

    }

    public void setCheckBoxIsChoosed(boolean isChoosed)
    {
        chkAuswahl.setChecked(isChoosed);
    }


}
