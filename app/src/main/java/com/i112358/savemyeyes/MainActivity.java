package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static MainActivity Get() { return activity; }
    private static MainActivity activity;

    private Switch m_shakeSwitcher = null;
    private SharedPreferences m_preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("info", "MainActivity onCreate");
        setContentView(R.layout.activity_main);
        activity = this;

        m_preferences = getPreferences(Context.MODE_PRIVATE);

        final boolean startShake = m_preferences.getBoolean("shakeServiceStatus", false);
        changeShakeServiceState(startShake);
        m_shakeSwitcher = (Switch)findViewById(R.id.shakeServiceSwitcher);
        m_shakeSwitcher.setChecked(startShake);
        m_shakeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                Log.i("info", "status is " + startShake);
                SharedPreferences.Editor editor = m_preferences.edit();
                editor.putBoolean("shakeServiceStatus", isChecked);
                editor.commit();
                changeShakeServiceState(isChecked);
//                if ( isChecked ) {
//                    Log.i("info", "swither ON");
//                }else{
//                    Log.i("info", "swither OFF");
//                }
                Log.i("info", "status is " + m_preferences.getBoolean("shakeServiceStatus", false));
            }
        });

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
        super.onResume();
        Log.w("info", "MainActivity onResume");

        /*
        if ( !isShakeServiceRunning(ShakeService.class) ) {
            this.startService(new Intent(this, ShakeService.class));
        } else {
            Log.i("info", "ShakeService already running");
        }
        */
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
}
