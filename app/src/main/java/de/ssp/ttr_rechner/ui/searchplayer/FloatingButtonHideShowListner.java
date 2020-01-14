package de.ssp.ttr_rechner.ui.searchplayer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import de.ssp.ttr_rechner.ui.util.FloatingActionButtonUtil;

public class FloatingButtonHideShowListner implements TabLayout.BaseOnTabSelectedListener {
    protected FloatingActionButton fab;
    public FloatingButtonHideShowListner(FloatingActionButton fab)
    {
        this.fab = fab;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        if(tab.getPosition()==2)
        {
            FloatingActionButtonUtil.hideFloatingActionButton(fab);
        }
        else {
            FloatingActionButtonUtil.showFloatingActionButton(fab);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


}
