package de.ssp.ttr_rechner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.ssp.ttr_rechner.ui.searchplayer.FloatingButtonAction;
import de.ssp.ttr_rechner.ui.searchplayer.FloatingButtonHideShowListner;
import de.ssp.ttr_rechner.ui.searchplayer.SearchWithCriteriaFragment;
import de.ssp.ttr_rechner.ui.searchplayer.SearchWithNextGamesFragment;
import de.ssp.ttr_rechner.ui.searchplayer.SearchWithPlayerFragment;
import de.ssp.ttr_rechner.ui.searchplayer.SectionsPagerAdapter;

public class SearchPlayerActivity extends AppCompatActivity implements SearchWithPlayerFragment.OnFragmentInteractionListener, SearchWithCriteriaFragment.OnFragmentInteractionListener, SearchWithNextGamesFragment.OnFragmentInteractionListener
{
    protected @BindView(R.id.toolbar) Toolbar tbToolbar;
    protected @BindView(R.id.view_pager) ViewPager viewPager;
    protected @BindView(R.id.tabs) TabLayout tabs;
    protected @BindView(R.id.fab) FloatingActionButton fab;
    protected SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_search_activity);

        ButterKnife.bind(this);

        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        tabs.setOnTabSelectedListener(new FloatingButtonHideShowListner(viewPager, fab));
    }

    @OnClick(R.id.fab)
    public void onPressedSearch(View view)
    {
        FloatingButtonAction floatingButtonAction = (FloatingButtonAction) sectionsPagerAdapter.getRegisteredFragment(tabs.getSelectedTabPosition());
        floatingButtonAction.onPressedFloatingButton(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TTRechnerActivity.REQUEST_CODE_SEARCH && resultCode == RESULT_OK)
        {
            setResult(RESULT_OK, data);
            finish();
        }
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

    @Override
    public void onFragmentInteraction(Uri uri)
    {
    }
}