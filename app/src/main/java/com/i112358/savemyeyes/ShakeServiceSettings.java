package com.i112358.savemyeyes;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by i112358 on 10/21/15.
 */
public class ShakeServiceSettings {

    private static int m_triggerStrength = 0;
    private static int m_averageStrength = 0;
    private static int[] m_startTime = {0,0};
    private static int[] m_endTime = {0,0};

    public ShakeServiceSettings()
    {
        super();
    }

    public static void loadSavedSettings( SharedPreferences preferences )
    {
        String loadPoints = preferences.getString("shakePreferences", "");
        if ( !loadPoints.isEmpty() ) {
            try {
                JSONObject json =  new JSONObject(loadPoints);
//                int length = json.length();
//
//                for ( int i = 0; i < length; i++ ) {
//                    JSONObject jPoint = new JSONObject(json.getString("point_"+i));
//                    BrightnessPoint point = new BrightnessPoint(jPoint.getInt("hour"), jPoint.getInt("minute"), jPoint.getInt("brightness"));
//                    s_brightnessPoints.add(point);
//                }
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
    }

    public static void saveSettings( SharedPreferences preferences )
    {
        Log.i("info", "save shake ToPreferences");

        if ( preferences.contains("shakePreferences") ) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("shakePreferences");
            editor.apply();
            Log.i("info", "clear saved shake preferences");
        }

//        JSONObject saveJSON = new JSONObject();
//        for ( int i = 0; i < s_brightnessPoints.size(); i++ ) {
//            BrightnessPoint point = s_brightnessPoints.get(i);
//            try {
//                saveJSON.put("point_"+i, point.getJSONObject());
//            } catch ( JSONException e ) {
//                e.printStackTrace();
//            }
//        }
//
//        Log.i("info", "POINTS Saved" + saveJSON.toString());

//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("brightnessPoints", saveJSON.toString());
//        editor.apply();
    }
}
