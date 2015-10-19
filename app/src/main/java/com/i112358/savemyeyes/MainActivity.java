package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {

    public static MainActivity Get() { return activity; }
    private static MainActivity activity;

    private TextView m_brightnessPointsText = null;
    private TextView m_shakeEndOn = null;
    private TextView m_shakeStartFrom = null;
    private Switch m_shakeSwitcher = null;
    private Switch m_changeBrightnessSwitcher = null;
    private SharedPreferences m_preferences = null;
    private Alarm m_alarm = null;
    private int[] m_timeValue = {20,0};
    private int[] m_previousTimeValue = new int[2];

    public boolean isChangeBrightnessEnable() { return m_isChangeBrightnessEnable; }
    private boolean m_isChangeBrightnessEnable = false;

    public BrightnessPoint getCurrentBrightnessPoint() { return m_currentBrightnessPoint; }
    public void setCurrentBrightnessPoint() { this.m_currentBrightnessPoint = BrightnessPointManager.getClosestTimePoint(m_preferences); }

    private BrightnessPoint m_currentBrightnessPoint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("info", "MainActivity onCreate");
        setContentView(R.layout.activity_main);

        activity = this;

        m_preferences = getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE);
        BrightnessPointManager.loadSavedPoints(m_preferences);

        m_timeValue[0] = m_preferences.getInt("shakeStartFromHour", m_timeValue[0]);
        m_timeValue[1] = m_preferences.getInt("shakeStartFromMin", m_timeValue[1]);

        final boolean startShake = m_preferences.getBoolean("shakeServiceStatus", false);
        m_shakeSwitcher = (Switch)findViewById(R.id.shakeServiceSwitcher);
        m_shakeSwitcher.setChecked(startShake);
        m_shakeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = m_preferences.edit();
                editor.putBoolean("shakeServiceStatus", isChecked);
                editor.apply();
//                changeShakeServiceState(isChecked);
                String[] txt = {"Ой все! Сломал телефон нахуй.", "Я тебе, блеать, говорил не трогай это!"};
                int pos = (Math.random() > 0.5) ? 0 : 1;
                Toast.makeText(activity, txt[pos], Toast.LENGTH_LONG).show();
            }
        });

        m_isChangeBrightnessEnable = m_preferences.getBoolean("changeBrightnessStatus", false);
        m_changeBrightnessSwitcher = (Switch)findViewById(R.id.changeBrightnesSwitcher);
        m_changeBrightnessSwitcher.setChecked(m_isChangeBrightnessEnable);
        m_changeBrightnessSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_isChangeBrightnessEnable = isChecked;
                SharedPreferences.Editor editor = m_preferences.edit();
                editor.putBoolean("changeBrightnessStatus", m_isChangeBrightnessEnable);
                editor.apply();
                changeBrightnessState(isChecked);
            }
        });

        m_shakeStartFrom = (TextView) findViewById(R.id.shakeStartTime);
        m_shakeStartFrom.setText(getString(R.string.shake_service_start_from) + Utilites.convertTime(m_timeValue[0], m_timeValue[1]));

        m_brightnessPointsText = (TextView)findViewById(R.id.setBrightnessPointsText);

        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeInMillis(System.currentTimeMillis());
        updateTime.set(Calendar.HOUR_OF_DAY, 20);
        updateTime.set(Calendar.MINUTE, 00);

        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.main_layout);
        TextView txt1 = new TextView(MainActivity.this);
        txt1.setText("niggers like butter");
        txt1.setRotation(1.8f);
        linearLayout.addView(txt1);
    }

    @Override
    public void onDestroy()
    {
        Log.w("info", "MainActivity onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume( ) {
        super.onResume();
        Log.w("info", "MainActivity onResume");

        int pointsCount = BrightnessPointManager.getPointsCount();
        if ( pointsCount > 0 ) {
            m_brightnessPointsText.setText(pointsCount + " " + getString(R.string.set_brightness_points_text));
        } else {
            m_brightnessPointsText.setText("Brightness points doesn't exists");
        }
    }

    @Override
    public void onPause()
    {
        Log.w("info", "MainActivity onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.w("info", "MainActivity onStop");
        super.onStop();
    }

    ////////////////////////////////

    private boolean isShakeServiceRunning(Class<ShakeService> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void changeBrightnessState( final boolean isStart )
    {
        Log.i("info", "Start Shake Service");
        Intent alarmService = new Intent(this, AlarmService.class);
        alarmService.putExtra("startNextAlarm", isStart);
        startService(alarmService);
    }

    private void changeShakeServiceState( final boolean isStart )
    {
        if ( isStart ) {
            if ( !isShakeServiceRunning(ShakeService.class) ) {
                Log.i("info", "Start Shake Service");
                startService(new Intent(this, ShakeService.class));
            }
        } else {
            Log.i("info", "Stop Shake Service");
            stopService(new Intent(this, ShakeService.class));
        }
    }

    public void onFromClick( View view )
    {
        /*
        BrightnessPointManager.addPoint(new BrightnessPoint(13,30,80));
        BrightnessPointManager.addPoint(new BrightnessPoint(13,31,100));
        BrightnessPointManager.addPoint(new BrightnessPoint(13,32,10));
        BrightnessPointManager.saveToPreferences(m_preferences);
        BrightnessPoint point = BrightnessPointManager.getClosestTimePoint(m_preferences);
        if ( point != null ) {
            Alarm alarm = new Alarm();
            alarm.setAlarm(activity, point);
        }
        */

        /*
        m_previousTimeValue = m_timeValue.clone();
        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
                MainActivity.Get().saveNewTime(hour,minute);
            }
        }, m_timeValue[0], m_timeValue[1], true);
        tpd.setCancelable(true);
        tpd.setOnCancelListener(new TimePickerDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.i("info", "onCancel called");
                saveNewTime(m_previousTimeValue[0], m_previousTimeValue[1]);
            }
        });

        tpd.show();
        */
    }

    public void onViewPointsClick( View view )
    {
        Log.i("info", "On Click set points");
        Intent intent = new Intent(this, SetPointsActivity.class);
        startActivity(intent);
    }

    private void saveNewTime( final int hour, final int minute )
    {
        SharedPreferences.Editor editor = m_preferences.edit();
        m_timeValue[0] = hour;
        m_timeValue[1] = minute;
        editor.putInt("shakeStartFromHour", hour);
        editor.putInt("shakeStartFromMin", minute);
        editor.apply();

        m_shakeStartFrom.setText(getString(R.string.shake_service_start_from) + Utilites.convertTime(m_timeValue[0], m_timeValue[1]));
        new BrightnessPoint(hour, minute, 50);
    }
}
