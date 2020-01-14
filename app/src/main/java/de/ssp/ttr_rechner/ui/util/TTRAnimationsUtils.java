package de.ssp.ttr_rechner.ui.util;

import android.widget.Button;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class TTRAnimationsUtils
{
    public static void standardAnimationButtonPress(Button button)
    {
        YoYo.with(Techniques.Pulse)
                .onStart(animator -> button.setAlpha(0.5f))
                .duration(250)
                .playOn(button);
    }

}
