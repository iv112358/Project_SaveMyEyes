package com.i112358.savemyeyes;

import org.json.JSONException;
import org.json.JSONObject;

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

    public int getAbsoluteValue()
    {
        return getHour() * 60 + getMinute();
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

    public int getBrightness() { return getValue("brightness"); }
    public float getBrightnessRelative() { return (100.0f *getBrightness() / 255.0f); }

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
