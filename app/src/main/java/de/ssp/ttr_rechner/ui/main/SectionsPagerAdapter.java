package de.ssp.ttr_rechner.ui.main;

import android.content.Context;

import com.jmelzer.myttr.TeamAppointment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import de.ssp.ttr_rechner.R;
import de.ssp.ttr_rechner.service.caller.ServiceCallerNextGames;
import de.ssp.ttr_rechner.service.caller.ServiceReady;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter implements ServiceReady<List<TeamAppointment>> {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_suche_spieler, R.string.tab_suche_kritierien, R.string.tab_suche_next_matches};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position)
        {
            case 0:
            {
                return SearchWithPlayerFragment.newInstance();
            }
            case 1:
            {
                return PlaceholderFragment.newInstance(position + 1);
            }
            case 2:
            {
                ServiceCallerNextGames serviceCallerNextGames = new ServiceCallerNextGames(mContext, this);
                serviceCallerNextGames.callService();
                return PlaceholderFragment.newInstance(position + 1);
            }
            default:
            {
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }

    @Override
    public void serviceReady(boolean success, List<TeamAppointment> teamAppointmentList, String errorMessage)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setTitle("Laden der Gegner erfolgreich")
                .setMessage(teamAppointmentList.toString())
                .setPositiveButton("Ok", null);
        dialogBuilder.create().show();
    }
}