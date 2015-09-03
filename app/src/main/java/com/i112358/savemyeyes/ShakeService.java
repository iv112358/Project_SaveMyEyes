package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;

public class ShakeService extends Service implements SensorEventListener {

    private final int ACCELERATION_ACCURACY = 10;
    private final float ACCELERATION_STRENGTH = 15;
    private final float ACCELERATION_TRIGGER = 12;
    private SensorManager m_sensorManager;
    private float m_lastAcceleration = SensorManager.GRAVITY_EARTH;
    private ArrayList<Float> m_accelerationList = new ArrayList<Float>();
    private long m_lastChangeTime = -1;
    private long m_timeOut = -1;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public ShakeService()
    {

    }

    public void onCreate()
    {
        super.onCreate();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        m_sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_sensorManager.registerListener(this, m_sensorManager .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

        return START_NOT_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float currentAcceleration = (float) Math.sqrt((double) (x*x + y*y + z*z));
        currentAcceleration = currentAcceleration * 0.9f + m_lastAcceleration * 0.1f;
        m_lastAcceleration = currentAcceleration;

        if ( currentAcceleration > ACCELERATION_TRIGGER ) {
            if ( m_lastChangeTime < 0 ) {
                m_lastChangeTime = System.currentTimeMillis();
            }
        }

        if ( m_lastChangeTime > 0 ) {
            m_accelerationList.add(currentAcceleration);
            if ( System.currentTimeMillis() - m_lastChangeTime > 1000 ) {
                float avarege = 0.0f;
                for ( Float elem : m_accelerationList ) {
                    avarege += elem.floatValue();
                }
                avarege /= m_accelerationList.size();
                Log.i("info", " avarege shake is " + avarege);
                if ( avarege > ACCELERATION_STRENGTH ) {
                    Log.i("info", "recorded " + m_accelerationList.size() + " amaunts");
                    MainActivity.Get().ChangeBrightness();
                }
                m_lastChangeTime = -1;
                m_accelerationList.clear();
            }
        }
//        Log.i("info", " current shake is " + currentAcceleration);
    }

    protected void onResume()
    {
        m_sensorManager.registerListener(this,m_sensorManager .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause()
    {
        m_sensorManager.unregisterListener(this);
    }
}
