package com.i112358.savemyeyes;

import android.content.ContentResolver;
import android.provider.Settings;

/**
 * Created by i112358 on 10/7/15.
 */
public class Utilites {

    public static String convertTime( int hour, int minute )
    {
        StringBuilder time = new StringBuilder();
        time.append(" ");
        if ( hour < 10 ) {
            time.append("0");
        }
        time.append(String.valueOf(hour));

        time.append(":");
        if ( minute < 10 ) {
            time.append("0");
        }
        time.append(String.valueOf(minute));
        return time.toString();
    }

    public static int getCurrentBrightness( final ContentResolver cr )
    {
        int currentBrightness = -1;
        try {
            currentBrightness = android.provider.Settings.System.getInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch ( Settings.SettingNotFoundException e ) {
            e.printStackTrace();
        }
        return currentBrightness;
    }

    public static void setBrightness( final ContentResolver cr, int brightness )
    {
        if ( brightness > 255 )
            brightness = 255;
        if ( brightness < 0 )
            brightness = 0;

        try {
            android.provider.Settings.System.putInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int evaluateChangeDelay( final int brightnessFrom, final int brightnessTo )
    {
        int seconds = 10;
        int diff = Math.abs(brightnessFrom - brightnessTo);
        if ( diff == 0 )
            diff = 1;
        return seconds * 1000 / diff;
    }

    public static int evaluateChangeDirection( final int brightnessFrom, final int brightnessTo )
    {
        if ( brightnessTo - brightnessFrom > 0 )
            return 1;
        else
            return -1;
    }
}
