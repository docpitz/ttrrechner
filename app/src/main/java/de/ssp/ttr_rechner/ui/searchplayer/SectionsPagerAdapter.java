package de.ssp.ttr_rechner.ui.searchplayer;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import de.ssp.ttr_rechner.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter
{

    @StringRes
    private static final int[] TAB_TITLES_PREMIUM = new int[]{R.string.tab_suche_name, R.string.tab_suche_kritierien, R.string.tab_suche_next_matches};
    private static final int[] TAB_TITLES = new int[]{R.string.tab_suche_kritierien};
    private final Context mContext;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    protected boolean isSingleChooseActive;
    protected boolean isPremiumAccount;

    public SectionsPagerAdapter(Context context, FragmentManager fm, boolean isSingleChooseActive, boolean isPremiumAccount)
    {
        super(fm);
        mContext = context;
        this.isSingleChooseActive = isSingleChooseActive;
        this.isPremiumAccount = isPremiumAccount;
    }

    @Override
    public Fragment getItem(int position)
    {
        if(isPremiumAccount)
        {
            return getItemForPremiumAccount(position);
        }
        return SearchWithCriteriaFragment.newInstance(isSingleChooseActive);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        int stringId = isPremiumAccount ? TAB_TITLES_PREMIUM[position] : TAB_TITLES[position];
        return mContext.getResources().getString(stringId);
    }

    @Override
    public int getCount()
    {
        return isPremiumAccount ? 3 : 1;
    }

    protected Fragment getItemForPremiumAccount(int position)
    {
        switch (position)
        {
            case 0:
            {
                return SearchWithNameFragment.newInstance(isSingleChooseActive);
            }
            case 1:
            {
                return SearchWithCriteriaFragment.newInstance(isSingleChooseActive);
            }
            case 2:
            {
                return SearchWithNextGamesFragment.newInstance(isSingleChooseActive);
            }
        }
        return null;
    }
}