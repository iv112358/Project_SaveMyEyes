package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends Activity {

    public static MainActivity Get() { return activity; }
    private static MainActivity activity;
    private int m_brightness = 255;
    private Timer m_timer = null;
    private TimerTask m_timerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("info", "MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        if ( !isShakeServiceRunning(ShakeService.class) ) {
            this.startService(new Intent(this, ShakeService.class));
        } else {
            Log.i("info", "ShakeService already running");
        }

        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.main_layout);
        TextView txt1 = new TextView(MainActivity.this);
        txt1.setText("niggers like butter");
        txt1.setRotation(1.8f);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.addView(txt1);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
    }

    @Override
    public void onDestroy()
    {
        Log.w("info", "MainActivity onDestroy");
        super.onDestroy();
//        this.stopService(m_shakeService);
    }

    @Override
    public void onResume( ) {
        Log.w("info", "MainActivity onResume");
        super.onResume();
        if ( !isShakeServiceRunning(ShakeService.class) ) {
            this.startService(new Intent(this, ShakeService.class));
        } else {
            Log.i("info", "ShakeService already running");
        }

        m_brightness = 0;
    }

    @Override
    public void onPause()
    {
        Log.w("info", "MainActivity onPause");
        super.onPause();
//        this.stopService(new Intent(this, ShakeService.class));
    }

    @Override
    public void onStop()
    {
        Log.w("info", "MainActivity onStop");
        super.onStop();
//        this.stopService(new Intent(this, ShakeService.class));
    }

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

    public void ChangeBrightness() {
        Log.i("info", "MainActivity ChangeBrightness");

        try {
            m_brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
            final boolean directionUp = (m_brightness <= 10);

            m_timer = new Timer();
            m_timerTask = new TimerTask() {
                @Override
                public void run() {
                    android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, m_brightness);
                    if ( directionUp ) {
                        m_brightness++;
                        if (m_brightness >= 255) {
                            Log.i("info", "stop timer");
                            m_timer.cancel();
                            m_timerTask = null;
                            m_timer = null;
                        }
                    } else {
                        m_brightness--;
                        if (m_brightness <= 0) {
                            Log.i("info", "stop timer");
                            m_timer.cancel();
                            m_timerTask = null;
                            m_timer = null;
                        }
                    }
                }
            };
            m_timer.scheduleAtFixedRate(m_timerTask, 50, (long) (1000 / 60));
//            m_timer.schedule(m_timerTask, 50, (long) (1000 / 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfShakeBrightnessChanging()
    {
        if ( m_timer != null || m_timerTask != null ) {
            Log.i("info", "timer or timerTask exists");
            return true;
        }
        return false;
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
}
