package com.i112358.savemyeyes;

import android.graphics.Point;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by i112358 on 10/5/15.
 */
public class BrightnessPoint {

    JSONObject point = new JSONObject();
    private boolean m_nextDay = false;

    public BrightnessPoint( final int hour, final int minute, final int brightness )
    {
        super();
        updatePoint(hour, minute, brightness);
    }

    public void updatePoint( final int hour, final int minute, final int brightness )
    {
        try {
            point.put("hour", hour);
            point.put("minute", minute);
            point.put("brightness", brightness);
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    public boolean isNextDay()
    {
        return m_nextDay;
    }

    public void setNextDay()
    {
        m_nextDay = true;
    }

    public JSONObject getJSONObject() { return point; }

    public int getHour()
    {
        return getValue("hour");
    }

    public int getMinute()
    {
        return getValue("minute");
    }

    public int getBrightness()
    {
        return getValue("brightness");
    }

    private int getValue( final String key )
    {
        int value = -1;
        try {
            value = point.getInt(key);
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public boolean equals( Object other )
    {
        Log.i("info", "compare point with point");
        if ( other != null ) {
            BrightnessPoint otherPoint = (BrightnessPoint)other;
            if ( otherPoint.getHour() == this.getHour() ) {
                if ( otherPoint.getMinute() == this.getMinute() ) {
                    if ( otherPoint.getBrightness() == this.getBrightness() ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
