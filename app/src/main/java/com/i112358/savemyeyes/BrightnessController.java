package com.i112358.savemyeyes;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by i112358 on 10/5/15.
 */
public class BrightnessController {


    private static int m_currentAbsouluteValue;
    private static int m_currentValue;
    private static int m_shouldBeValue;
    private static int m_shoudBeAbsoluteValue;
    private static int m_fadeInFadeOutTime = 60000; // in Miliseconds
    private static int m_delay = 0;

    public static int getCurrentBrightness( final ContentResolver cr )
    {
        try {
            m_currentAbsouluteValue = android.provider.Settings.System.getInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch ( Settings.SettingNotFoundException e ) {
            e.printStackTrace();
        }
        m_currentValue = m_currentAbsouluteValue * 100 / 255;
        Log.i("info", "Brightness in % is " + m_currentValue);
        Log.i("info", "Brightness is " + m_currentAbsouluteValue);

        return m_currentAbsouluteValue;
    }

    public static int setBrightness( final ContentResolver cr, final int brightness )
    {
        m_currentAbsouluteValue = brightness;
        m_currentValue = m_currentAbsouluteValue * 100 / 255;
        try {
            android.provider.Settings.System.putInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS, m_currentAbsouluteValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_currentAbsouluteValue;
    }

    public static int changeToValueInTime( final int brightness, final int seconds )
    {
        m_shouldBeValue = brightness;
        m_shoudBeAbsoluteValue = m_shouldBeValue * 255 / 100;

        int diff = Math.abs(m_shoudBeAbsoluteValue - m_currentAbsouluteValue);
        Log.i("info", "values to change is " + diff);
        m_delay = seconds * 1000 / diff;
        Log.i("info", "change every Miliseconds " + m_delay);
        return m_delay;
    }
}
