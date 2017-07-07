package com.winjit.vwdemo;

import android.content.res.Resources;

@SuppressWarnings({"UnnecessaryLocalVariable", "unused"})
public class Utils {
    public static float getPixelFromDp(float dp) {
        float pixel = dp * Resources.getSystem().getDisplayMetrics().density;
        return pixel;
    }
    
    public static float getDPFromPixel(float pixel) {
        float dpValue = pixel / Resources.getSystem().getDisplayMetrics().density;
        return dpValue;
    }
}
