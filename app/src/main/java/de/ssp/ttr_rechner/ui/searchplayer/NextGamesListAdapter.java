package de.ssp.ttr_rechner.ui.searchplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.ssp.service.mytischtennis.model.NextGame;
import de.ssp.ttr_rechner.R;

public class NextGamesListAdapter extends ArrayAdapter<NextGame> {
    private final Context context;
    private final NextGame[] nextGames;
    protected @BindView(R.id.txtDate) TextView txtDate;
    protected @BindView(R.id.txtGegner) TextView txtGegner;

    public NextGamesListAdapter(Context context, NextGame[] values) {
        super(context, -1, values);
        this.context = context;
        this.nextGames = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.player_search_row_next_games, parent, false);
        ButterKnife.bind(this, rowView);

        NextGame nextGame = nextGames[position];
        txtDate.setText(nextGame.getTeamAppointment().getDate());
        txtGegner.setText(nextGame.getGegner());
        return rowView;
    }
}
