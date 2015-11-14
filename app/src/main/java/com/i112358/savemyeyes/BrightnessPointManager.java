package com.i112358.savemyeyes;

import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class BrightnessPointManager {
    public static ArrayList<BrightnessPoint> s_brightnessPoints = new ArrayList<>();

    public static BrightnessPoint getClosestTimePoint( SharedPreferences preferences )
    {
        if ( !loadSavedPoints(preferences) ) {
            Log.i("info", "BrightnessPointManager no saved points Line is preferences");
            return null;
        }

        if ( s_brightnessPoints.isEmpty() ) {
            Log.i("info", "BrightnessPointManager List of points is empty");
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        int currentTime[] = {calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)};

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
            Log.i("info", "BrightnessPointManager Lower point used ");
            pointToUse = lowerPoint;
            pointToUse.setNextDay();
        }
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
        sortPoints();
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
        SharedPreferences.Editor editor = preferences.edit();
        if ( preferences.contains("brightnessPoints") ) {
            editor.remove("brightnessPoints");
            editor.apply();
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

        Log.i("info", "BrightnessPointManager POINTS Saved" + saveJSON.toString());
        editor.putString("brightnessPoints", saveJSON.toString());
        editor.apply();
    }

    public static boolean loadSavedPoints( SharedPreferences preferences )
    {
        String loadPoints = preferences.getString("brightnessPoints", "");
        if ( ("").equals(loadPoints) ) {
            Log.i("info", "BrightnessPointManager no points exists");
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

    public static int checkIfPointCloseToNeighbor( final int time, BrightnessPoint editPoint )
    {
        int limit = 10;

        for ( int i = 0; i < s_brightnessPoints.size(); i++ )
        {
            BrightnessPoint point = s_brightnessPoints.get(i);
            if ( editPoint != null ) {
                if ( editPoint.equals(point) ) {
                    continue;
                }
            }

            int result = Math.abs(time - point.getAbsoluteValue());
            if ( result <= limit || result >= 1430 ) {
                return i;
            }
        }
        return -1;
    }
}