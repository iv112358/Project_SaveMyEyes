package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ChangeBrightnessService extends Service {

    private final Handler m_handler = new Handler();
    private int m_direction = 0;
    private int m_delay = 0;
    private int m_brightnessCurrent = 0;
    private int m_brightnessToSet = 0;

    public ChangeBrightnessService() {
        Log.i("info", "ChangeBrightnessService Constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE);

        if ( Utilites.isPermissionGranted(getApplicationContext()) ) {
            m_brightnessCurrent = Utilites.getCurrentBrightness(getContentResolver());
            m_brightnessToSet = intent.getIntExtra("brightness", 100);
            int seconds = preferences.getInt("changeBrightnessPeriod", 10);
            m_delay = Utilites.evaluateChangeDelay(seconds, m_brightnessCurrent, m_brightnessToSet);
            m_direction = Utilites.evaluateChangeDirection(m_brightnessCurrent, m_brightnessToSet);

            Log.i("info", "delay is " + m_delay);
            m_handler.removeCallbacks(changeBrightness);
            m_handler.post(changeBrightness);
            return START_STICKY;
        } else {
            Toast.makeText(getApplicationContext(), "Please grant Permission and relaunch application", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("changeBrightnessStatus", false);
            editor.apply();
            stopSelf();
            return START_NOT_STICKY;
        }
    }

    private Runnable changeBrightness = new Runnable() {
        @Override
        public void run() {
            Log.i("info", "ChangeBrightnessService" + m_brightnessCurrent + "/" + m_brightnessToSet);
            if ( m_brightnessCurrent == m_brightnessToSet ) {
                m_handler.removeCallbacks(changeBrightness);
                gotoNextPoint();

                return;
            }
            m_brightnessCurrent += m_direction;
            Utilites.setBrightness(getContentResolver(), m_brightnessCurrent);
            m_handler.postDelayed(this, m_delay);
        }
    };

    private void gotoNextPoint()
    {
        if ( MainActivity.Get() != null ) {
            MainActivity.Get().setNextBrightnessPoint();
        }
        if ( SetPointsActivity.Get() != null ) {
            SetPointsActivity.Get().updateSetPointScreen();
        }
        Log.i("info", "ChangeBrightnessService finish currentBrightness is " + Utilites.getCurrentBrightness(getContentResolver()));
        Intent alarmService = new Intent(this, AlarmService.class);
        alarmService.putExtra("startScheduledChangeBrightness", true);
        startService(alarmService);
        stopSelf();
    }
}
