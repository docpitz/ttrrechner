package de.ssp.ttr_rechner.ui.searchplayer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import de.ssp.ttr_rechner.ui.util.FloatingActionButtonUtil;

public class FloatingButtonHideShowListner extends TabLayout.ViewPagerOnTabSelectedListener
{
    protected FloatingActionButton fab;
    public FloatingButtonHideShowListner(ViewPager viewPager, FloatingActionButton fab)
    {
        super(viewPager);
        this.fab = fab;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        super.onTabSelected(tab);
        if(tab.getPosition()==2)
        {
            FloatingActionButtonUtil.hideFloatingActionButton(fab);
        }
        else {
            FloatingActionButtonUtil.showFloatingActionButton(fab);
        }
    }


}
