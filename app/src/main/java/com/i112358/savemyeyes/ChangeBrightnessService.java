package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Intent;
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
        m_brightnessCurrent = Utilites.getCurrentBrightness(getContentResolver());
        m_brightnessToSet = intent.getIntExtra("brightness", 100);
        m_delay = Utilites.evaluateChangeDelay(m_brightnessCurrent, m_brightnessToSet);
        m_direction = Utilites.evaluateChangeDirection(m_brightnessCurrent, m_brightnessToSet);

        Log.i("info", "Value current is " + m_brightnessCurrent);
        Log.i("info", "Value to set is " + m_brightnessToSet);
        Log.i("info", "Value change delay is " + m_delay);
        Log.i("info", "Value change value is " + m_direction);
        Toast.makeText(this, "Set brightness to " + m_brightnessToSet, Toast.LENGTH_LONG).show();

        m_handler.removeCallbacks(changeBrightness);
        m_handler.post(changeBrightness);
        return START_STICKY;
    }

    private Runnable changeBrightness = new Runnable() {
        @Override
        public void run() {
//            Log.i("info", m_brightnessCurrent + "/" + m_brightnessToSet);
            if ( m_brightnessCurrent == m_brightnessToSet ) {
                m_handler.removeCallbacks(changeBrightness);
                gotoNextPoint();
                Log.i("info", "currentBrightness is " + Utilites.getCurrentBrightness(getContentResolver()));
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
            MainActivity.Get().setCurrentBrightnessPoint();
        }
        if ( SetPointsActivity.Get() != null ) {
            SetPointsActivity.Get().updateSetPointScreen();
        }

        Intent alarmService = new Intent(this, AlarmService.class);
        alarmService.putExtra("startNextAlarm", true);
        startService(alarmService);
        stopSelf();
    }
}
