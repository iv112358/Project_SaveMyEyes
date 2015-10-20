package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class ShakeServiceSettingsActivity extends Activity {

    private int[] m_newValues = {0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("info", "ShakeServiceSettingsActivity onCreate");

        setContentView(R.layout.shake_settings);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.w("info", "ShakeServiceSettingsActivity onResume");
    }

    public void onSSSASetTime( View view )
    {
        switch ( view.getId() ) {
            case R.id.button_shake_start_from:
                Log.i("info", "select start");
                break;
            case R.id.button_shake_end_on:
                Log.i("info", "select end");
                break;
            default:
                return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.setup_point);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        Button doneButton = (Button)(dialog.findViewById(R.id.buttonSetPointDone));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (point != null) {
//                    point.updatePoint(m_newValues[0], m_newValues[1], m_newValues[2]);
//                } else {
//                    BrightnessPointManager.addPoint(new BrightnessPoint(m_newValues[0], m_newValues[1], m_newValues[2]));
//                }
//                BrightnessPointManager.saveToPreferences(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
//                Log.i("info", "Time is " + Utilites.convertTime(m_newValues[0], m_newValues[1]) + " Brightness is " + m_newValues[2]);
                dialog.dismiss();
            }
        });

        TimePicker timePicker = (TimePicker)(dialog.findViewById(R.id.timePicker));
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                m_newValues[0] = hourOfDay;
                m_newValues[1] = minute;
            }
        });

//        if ( point != null ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                timePicker.setHour(point.getHour());
//                timePicker.setMinute(point.getMinute());
//            } else {
//                timePicker.setCurrentHour(point.getHour());
//                timePicker.setCurrentMinute(point.getMinute());
//            }
//        } else {
//            Calendar calendar = Calendar.getInstance();
//            m_newValues[0] = calendar.get(Calendar.HOUR_OF_DAY);
//            m_newValues[1] = calendar.get(Calendar.MINUTE);
//        }

        (dialog.findViewById(R.id.textSetPointBrightness)).setVisibility(View.GONE);
        (dialog.findViewById(R.id.seekBar)).setVisibility(View.GONE);

        dialog.show();
    }

    private void updateScreen()
    {

    }

}
