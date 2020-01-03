package de.ssp.ttr_rechner.ui.searchplayer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

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
            hideFloatingActionButton(fab);
        }
        else {
            showFloatingActionButton(fab);
        }
    }

    private void hideFloatingActionButton(FloatingActionButton fab)
    {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
        FloatingActionButton.Behavior behavior= (FloatingActionButton.Behavior)params.getBehavior();
        if(behavior!=null){
            behavior.setAutoHideEnabled(false);
        }
        fab.hide();
    }

    private void showFloatingActionButton(FloatingActionButton fab){
        fab.show();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
        FloatingActionButton.Behavior behavior = (FloatingActionButton.Behavior)params.getBehavior();
        if(behavior!=null){
            behavior.setAutoHideEnabled(true);
        }
    }
}
