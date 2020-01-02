package de.ssp.ttr_rechner;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.model.SearchPlayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ssp.ttr_rechner.service.caller.ServiceCallerNextGames;
import de.ssp.ttr_rechner.service.caller.ServiceCallerSearchPlayer;
import de.ssp.ttr_rechner.service.caller.ServiceReady;
import de.ssp.ttr_rechner.ui.main.SearchWithPlayerFragment;
import de.ssp.ttr_rechner.ui.main.SectionsPagerAdapter;

public class SearchPlayerActivity extends AppCompatActivity implements SearchWithPlayerFragment.OnFragmentInteractionListener, ServiceReady<List<Player>>
{

    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.view_pager) ViewPager viewPager;
    protected @BindView(R.id.tabs) TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_player);

        ButterKnife.bind(this);

        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.fab)
    public void onPressedSearch(View view)
    {
        SearchPlayer searchPlayer = new SearchPlayer();
        searchPlayer.setLastname("Weich");
        searchPlayer.setFirstname("Larissa");
        ServiceCallerSearchPlayer serviceCallerSearchPlayer = new ServiceCallerSearchPlayer(this, this, searchPlayer);
        serviceCallerSearchPlayer.callService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    @Override
    public void serviceReady(boolean success, List<Player> playerList, String errorMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Suche nach Larissa Weich")
                .setMessage(playerList.toString())
                .setPositiveButton("Ok", null);
        dialogBuilder.create().show();
    }
}