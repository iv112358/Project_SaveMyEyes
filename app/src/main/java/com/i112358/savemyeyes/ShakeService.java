package com.i112358.savemyeyes;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class ShakeService extends Service implements SensorEventListener {

    private SensorManager m_sensorManager;
    private float m_lastAcceleration = SensorManager.GRAVITY_EARTH;
    private float m_currentAcceleration = SensorManager.GRAVITY_EARTH;

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
        m_sensorManager.registerListener(this, m_sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

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
        m_lastAcceleration = m_currentAcceleration;

        m_currentAcceleration = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = m_currentAcceleration - m_lastAcceleration;
        m_currentAcceleration = m_currentAcceleration * 0.9f + m_lastAcceleration * 0.1f;
        Log.i("info", " current shake is " + m_currentAcceleration);
    }

    protected void onResume()
    {
        m_sensorManager.registerListener(this,m_sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause()
    {
        m_sensorManager.unregisterListener(this);
    }
}
