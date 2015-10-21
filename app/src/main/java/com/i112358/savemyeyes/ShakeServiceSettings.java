package com.i112358.savemyeyes;

import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class ShakeServiceSettings {
    private static int m_triggerStrength = 51;
    public static int getTriggerStrength() { return m_triggerStrength; }
    public static void setTriggerStrength(int m_triggerStrength) { ShakeServiceSettings.m_triggerStrength = m_triggerStrength; }

    private static int m_averageStrength = 4;
    public static int getAverageStrength() { return m_averageStrength; }
    public static void setAverageStrength(int m_averageStrength) { ShakeServiceSettings.m_averageStrength = m_averageStrength; }

    private static int[] m_startTime = {2,16};
    public static int[] getStartTime() { return m_startTime; }
    public static void setStartTime( int[] m_startTime ) { ShakeServiceSettings.m_startTime = m_startTime; }

    private static int[] m_endTime = {1,54};
    public static void setEndTime(int[] m_endTime) { ShakeServiceSettings.m_endTime = m_endTime; }
    public static int[] getEndTime() { return m_endTime; }

    public ShakeServiceSettings()
    {
        super();
    }

    public static void loadSavedSettings( SharedPreferences preferences )
    {
        Log.i("info", "trigger: " + m_triggerStrength + " average: " + m_averageStrength + " start: " + Utilites.convertTime(m_startTime[0],m_startTime[1]) + " end: " + Utilites.convertTime(m_endTime[0],m_endTime[1]));
        String loadPoints = preferences.getString("shakePreferences", "");
        if ( !loadPoints.isEmpty() ) {
            Log.i("info", "Saved shake preferences is " + loadPoints);
            try {
                JSONObject json =  new JSONObject(loadPoints);
                m_triggerStrength = json.getInt("triggerStrength");
                m_averageStrength = json.getInt("averageStrength");
                m_startTime[0] = json.getJSONObject("startTime").getInt("hour");
                m_startTime[1] = json.getJSONObject("startTime").getInt("minute");
                m_endTime[0] = json.getJSONObject("endTime").getInt("hour");
                m_endTime[1] = json.getJSONObject("endTime").getInt("minute");
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        } else {
            Log.i("info", "NO Saved shake preferences");
        }
        Log.i("info", "trigger: " + m_triggerStrength + " average: " + m_averageStrength + " start: " + Utilites.convertTime(m_startTime[0],m_startTime[1]) + " end: " + Utilites.convertTime(m_endTime[0],m_endTime[1]));
    }

    public static void saveSettings( SharedPreferences preferences )
    {
        SharedPreferences.Editor editor = preferences.edit();
        if ( preferences.contains("shakePreferences") ) {
            editor.remove("shakePreferences");
            editor.apply();
        }

        JSONObject saveJSON = new JSONObject();
        try {
            saveJSON.put("triggerStrength", m_triggerStrength);
            saveJSON.put("averageStrength", m_averageStrength);

            JSONObject startTime = new JSONObject();
            startTime.put("hour", m_startTime[0]);
            startTime.put("minute", m_startTime[1]);
            saveJSON.put("startTime", startTime);

            JSONObject endTime = new JSONObject();
            endTime.put("hour", m_endTime[0]);
            endTime.put("minute", m_endTime[1]);
            saveJSON.put("endTime", endTime);
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

        Log.i("info", "shake settings Saved" + saveJSON.toString());

        editor.putString("shakePreferences", saveJSON.toString());
        editor.apply();
    }
}
