package de.ssp.ttr_rechner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.jmelzer.myttr.Player;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ssp.ttr_rechner.model.ChooseablePlayer;
import de.ssp.ttr_rechner.ui.foundedplayer.FoundedPlayersListAdapter;

public class FoundedPlayerActivity extends AppCompatActivity
{
    public static String PUT_EXTRA_PLAYER_LIST = "PLAYER_LIST";
    protected ArrayList<ChooseablePlayer> players;
    protected @BindView(R.id.listPlayer) ListView listViewPlayer;
    protected @BindView(R.id.toolbar) Toolbar tbToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players_founded_activity);
        ButterKnife.bind(this);

        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        players = ChooseablePlayer.convertFromPlayers((ArrayList<Player>) getIntent().getSerializableExtra(PUT_EXTRA_PLAYER_LIST));
        FoundedPlayersListAdapter listAdapter = new FoundedPlayersListAdapter(this, getPlayers());
        listViewPlayer.setAdapter(listAdapter);
        listViewPlayer.setItemsCanFocus(true);
        listViewPlayer.setOnItemClickListener((parent, view, position, id) -> {

            CheckBox chkAuswahl = view.findViewById(R.id.chkAuswahl);
            chkAuswahl.setChecked(!chkAuswahl.isChecked());
        });
    }

    protected ChooseablePlayer[] getPlayers()
    {
        return players.toArray(new ChooseablePlayer[players.size()]);
    }

    @OnClick(R.id.fab)
    public void onPressedDone(View view)
    {
        ArrayList<Player> playerArrayList = new ArrayList<>();
        for (ChooseablePlayer player: players)
        {
            if(player.isChecked)
            {
                playerArrayList.add(player.player);
            }
        }
        Intent intent = new Intent();
        intent.putExtra(TTRechnerActivity.PUT_EXTRA_RESULT_PLAYERS, playerArrayList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
