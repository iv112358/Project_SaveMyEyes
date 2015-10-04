package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

    public static MainActivity Get() { return activity; }
    private static MainActivity activity;

    private TextView m_shakeEndOn = null;
    private TextView m_shakeStartFrom = null;
    private Switch m_shakeSwitcher = null;
    private SharedPreferences m_preferences = null;
    private int[] m_shakeStart = {20,0};
    private int[] m_shakeEnd = {7,0};
    private boolean m_shakeServiceChangeHour = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("info", "MainActivity onCreate");
        setContentView(R.layout.activity_main);
        activity = this;

        m_preferences = getPreferences(Context.MODE_PRIVATE);

        m_shakeStart[0] = m_preferences.getInt("shakeStartFromHour", m_shakeStart[0]);
        m_shakeStart[1] = m_preferences.getInt("shakeStartFromMin", m_shakeStart[1]);

        m_shakeEnd[0] = m_preferences.getInt("shakeEndOnHour",  m_shakeEnd[0]);
        m_shakeEnd[1] = m_preferences.getInt("shakeEndOnMin",  m_shakeEnd[1]);

        final boolean startShake = m_preferences.getBoolean("shakeServiceStatus", false);
        changeShakeServiceState(startShake);
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

        m_shakeStartFrom = (TextView)findViewById(R.id.shakeStartTime);
        m_shakeStartFrom.setText(getString(R.string.shake_service_start_from) + convertTime(m_shakeStart[0], m_shakeStart[1]));

        m_shakeEndOn = (TextView)findViewById(R.id.shakeEndTime);
        m_shakeEndOn.setText(getString(R.string.shake_service_end_on) + convertTime(m_shakeEnd[0], m_shakeEnd[1]));

        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.main_layout);
        TextView txt1 = new TextView(MainActivity.this);
        txt1.setText("niggers like butter");
        txt1.setRotation(1.8f);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isShakeServiceRunning(Class<ShakeService> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        TimePickerDialog tpd;
        if ( view.getId() == R.id.shakeStartTime ) {
            m_shakeServiceChangeHour = true;
            tpd = new TimePickerDialog(this, shakeServiceFromCallback, m_shakeStart[0], m_shakeStart[1], true);
        } else if ( view.getId() == R.id.shakeEndTime ) {
            m_shakeServiceChangeHour = false;
            tpd = new TimePickerDialog(this, shakeServiceFromCallback, m_shakeEnd[0], m_shakeEnd[1], true);
        } else {
            return;
        }
        tpd.show();
    }

    TimePickerDialog.OnTimeSetListener shakeServiceFromCallback = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            SharedPreferences.Editor editor = m_preferences.edit();
            if ( m_shakeServiceChangeHour ) {
                m_shakeStart[0] = hour;
                m_shakeStart[1] = minute;
                editor.putInt("shakeStartFromHour", hour);
                editor.putInt("shakeStartFromMin", minute);
                m_shakeStartFrom.setText(getString(R.string.shake_service_start_from) + convertTime(m_shakeStart[0], m_shakeStart[1]));
            } else {
                m_shakeEnd[0] = hour;
                m_shakeEnd[1] = minute;
                editor.putInt("shakeEndOnHour", m_shakeEnd[0]);
                editor.putInt("shakeEndOnMin", m_shakeEnd[1]);
                m_shakeEndOn.setText(getString(R.string.shake_service_end_on) + convertTime(m_shakeEnd[0], m_shakeEnd[1]));
            }

            editor.apply();
        }
    };

    private String convertTime( int hour, int minute )
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
}
