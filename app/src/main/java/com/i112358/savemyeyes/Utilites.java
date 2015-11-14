package com.i112358.savemyeyes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utilites {

    private static int s_density = -1;
    public static void setDensity( final Context context )
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        s_density = metrics.densityDpi;
        Log.i("info", "density is " + s_density);
    }

    public static String convertTime( int[] time )
    {
        return convertTime(time[0], time[1]);
    }

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

    public static int evaluateChangeDelay( final int seconds, final int brightnessFrom, final int brightnessTo )
    {
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

    public static float convertDpToPixel( final float dp )
    {
        return dp * (s_density / 160f);
    }

    public static float convertPixelsToDp( final float px )
    {
        return px / (s_density / 160f);
    }

    public static boolean isPermissionGranted( Context context )
    {
        Log.i("info", "askPermissions");
        boolean isGranted = true;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if ( !Settings.System.canWrite(context) ) {
                Log.i("info", "Write Settings NOT GRANTED");
                isGranted = false;
            }
        }

        return isGranted;
    }
}
