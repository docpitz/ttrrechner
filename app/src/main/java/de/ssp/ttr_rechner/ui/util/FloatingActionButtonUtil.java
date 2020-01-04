package de.ssp.ttr_rechner.ui.util;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class FloatingActionButtonUtil
{
    public static void hideFloatingActionButton(FloatingActionButton fab)
    {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
        FloatingActionButton.Behavior behavior= (FloatingActionButton.Behavior)params.getBehavior();
        if(behavior!=null){
            behavior.setAutoHideEnabled(false);
        }
        fab.hide();
    }

    public static void showFloatingActionButton(FloatingActionButton fab)
    {
        fab.show();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
        FloatingActionButton.Behavior behavior = (FloatingActionButton.Behavior)params.getBehavior();
        if(behavior!=null){
            behavior.setAutoHideEnabled(true);
        }
    }
}
