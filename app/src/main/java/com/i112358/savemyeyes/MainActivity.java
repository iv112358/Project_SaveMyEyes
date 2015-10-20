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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static MainActivity Get() { return activity; }
    private static MainActivity activity;
    private SharedPreferences m_preferences = null;
    private TextView m_brightnessPointsText = null;
    private Switch m_shakeSwitcher = null;
    private Switch m_changeBrightnessSwitcher = null;

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
        ShakeServiceSettings.loadSavedSettings(m_preferences);

        final boolean startShake = m_preferences.getBoolean("shakeServiceStatus", false);
        m_shakeSwitcher = (Switch)findViewById(R.id.shakeServiceSwitcher);
        m_shakeSwitcher.setChecked(startShake);
        m_shakeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = m_preferences.edit();
                editor.putBoolean("shakeServiceStatus", isChecked);
                editor.apply();
                changeShakeServiceState(isChecked);
            }
        });
        changeShakeServiceState(startShake);

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

        if ( m_brightnessPointsText == null )
            m_brightnessPointsText = (TextView)findViewById(R.id.setBrightnessPointsText);

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
        View shakeSettingsView = findViewById(R.id.shakeSettingsLayout);
        View space = findViewById(R.id.space_shake_settings);
        if ( isStart ) {
            shakeSettingsView.setVisibility(View.VISIBLE);
            space.setVisibility(View.VISIBLE);
            if ( !isShakeServiceRunning(ShakeService.class) ) {
                Log.i("info", "Start Shake Service");
                startService(new Intent(this, ShakeService.class));
            }
        } else {
            shakeSettingsView.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
            Log.i("info", "Stop Shake Service");
            stopService(new Intent(this, ShakeService.class));
        }
    }

    public void onViewPointsClick( View view )
    {
        Intent intent = new Intent(this, SetPointsActivity.class);
        startActivity(intent);
    }

    public void onShakeSettingsClick( View view )
    {
        Log.i("info", "On onShakeSettingsClick");
        Intent intent = new Intent(this, ShakeServiceSettingsActivity.class);
        startActivity(intent);
    }
}
