package de.ssp.ttr_rechner.ui.foundedplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.model.ChooseablePlayer;

public class FoundedPlayersListAdapter extends ArrayAdapter<ChooseablePlayer> {
    private final Context context;
    private final ChooseablePlayer[] players;

    protected @BindView(R.id.chkAuswahl) CheckBox chkAuswahl;
    protected @BindView(R.id.txtName) TextView txtName;
    protected @BindView(R.id.txtVerein) TextView txtVerein;
    protected @BindView(R.id.txtTTRPunkte) TextView txtTTRPunkte;

    public FoundedPlayersListAdapter(Context context, ChooseablePlayer[] values) {
        super(context, -1, values);
        this.context = context;
        this.players = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.players_founded_row, parent, false);
        ButterKnife.bind(this, rowView);

        ChooseablePlayer player = players[position];
        txtVerein.setText(player.player.getClub());
        txtName.setText(player.player.getFirstname()+ " " + player.player.getLastname());
        txtTTRPunkte.setText(String.valueOf(player.player.getTtrPoints()));
        chkAuswahl.setChecked(player.isChecked);
        chkAuswahl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                player.isChecked = isChecked;
            }
        });
        return rowView;
    }

    @OnClick(R.id.chkAuswahl)
    public void clickAuswahl()
    {

    }
}
