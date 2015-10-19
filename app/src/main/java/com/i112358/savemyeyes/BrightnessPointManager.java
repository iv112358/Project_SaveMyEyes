package com.i112358.savemyeyes;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by i112358 on 10/7/15.
 */
public class BrightnessPointManager {
    public static ArrayList<BrightnessPoint> s_brightnessPoints = new ArrayList<BrightnessPoint>();

    public static BrightnessPoint getClosestTimePoint( SharedPreferences preferences )
    {
        if ( !loadSavedPoints(preferences) ) {
            Log.i("info", "no saved points");
            return null;
        }

        if ( s_brightnessPoints.isEmpty() ) {
            Log.i("info", "List of points is empty");
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        int currentTime[] = {calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)};
//        int currentTime[] = {14,18};
        Log.i("info", "Getpoints counter is " + s_brightnessPoints.size());
        Log.i("info", "Current point time is " + currentTime[0] + ":" + currentTime[1]);

        BrightnessPoint pointToUse = null;
        BrightnessPoint lowerPoint = null;
        for ( int i = 0; i < s_brightnessPoints.size(); i++ ) {
            BrightnessPoint point = s_brightnessPoints.get(i);
            if ( lowerPoint != null ) {
                if ( point.getHour() < lowerPoint.getHour() ) {
                    lowerPoint = point;
                } else if (point.getHour() == lowerPoint.getHour()) {
                    if ( point.getMinute() < lowerPoint.getMinute() ) {
                        lowerPoint = point;
                    }
                }
            } else {
                lowerPoint = point;
            }

            if ( point.getHour() > currentTime[0] ) {
                if ( pointToUse == null )
                    pointToUse = point;
                if ( point.getHour() < pointToUse.getHour() ) {
                    pointToUse = point;
                } else if ( point.getHour() == pointToUse.getHour() ) {
                    if ( point.getMinute() < pointToUse.getMinute() ) {
                        pointToUse = point;
                    }
                }
            } else if ( point.getHour() == currentTime[0] && point.getMinute() > currentTime[1] ) {
//                Log.i("info", "point minuete is " + point.getMinute() + " current minute is " + currentTime[1] );
                if ( pointToUse == null || point.getHour() != pointToUse.getHour() )
                    pointToUse = point;
                else if ( point.getMinute() < pointToUse.getMinute() ) {
                    pointToUse = point;
                }
            }
//            Log.i("info", "---------");
        }

        if ( pointToUse == null && lowerPoint != null ) {
            Log.i("info", "Lower point used ");
            pointToUse = lowerPoint;
            pointToUse.setNextDay();
        }

        Log.i("info", "Time will be " + pointToUse.getHour() + ":" + pointToUse.getMinute());

        return pointToUse;
    }

    public static BrightnessPoint getPoint( final int id )
    {
        if ( id < s_brightnessPoints.size() )
            return s_brightnessPoints.get(id);
        else
            return null;
    }

    public static void removePoint( final int index )
    {
        s_brightnessPoints.remove(index);
    }

    public static void addPoint( final BrightnessPoint point )
    {
        s_brightnessPoints.add(point);
    }

    public static int getPointsCount()
    {
        return s_brightnessPoints.size();
    }

    public static void saveToPreferences( SharedPreferences preferences )
    {
        Log.i("info", "saveToPreferences counter is " + s_brightnessPoints.size());

        if ( preferences.contains("brightnessPoints") ) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("brightnessPoints");
            editor.apply();
            Log.i("info", "clear saved points");
        }

        JSONObject saveJSON = new JSONObject();
        for ( int i = 0; i < s_brightnessPoints.size(); i++ ) {
            BrightnessPoint point = s_brightnessPoints.get(i);
            try {
                saveJSON.put("point_"+i, point.getJSONObject());
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }

        Log.i("info", "POINTS Saved" + saveJSON.toString());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("brightnessPoints", saveJSON.toString());
        editor.apply();
    }

    public static boolean loadSavedPoints( SharedPreferences preferences )
    {
        String loadPoints = preferences.getString("brightnessPoints", "");
        if ( ("").equals(loadPoints) ) {
            Log.i("info", "no points exists");
            return false;
        } else {
            s_brightnessPoints.clear();
            try {
                JSONObject json =  new JSONObject(loadPoints);
                int length = json.length();

                for ( int i = 0; i < length; i++ ) {
                    JSONObject jPoint = new JSONObject(json.getString("point_"+i));
                    BrightnessPoint point = new BrightnessPoint(jPoint.getInt("hour"), jPoint.getInt("minute"), jPoint.getInt("brightness"));
                    s_brightnessPoints.add(point);
                }
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void sortPoints()
    {
        Collections.sort(s_brightnessPoints, new Comparator<BrightnessPoint>() {
            @Override
            public int compare( BrightnessPoint p1, BrightnessPoint p2 ) {
                int totalP1 = p1.getHour()*60 + p1.getMinute();
                int totalP2 = p2.getHour()*60 + p2.getMinute();
                return Integer.valueOf(totalP1).compareTo(totalP2);
            }
        });
    }
}