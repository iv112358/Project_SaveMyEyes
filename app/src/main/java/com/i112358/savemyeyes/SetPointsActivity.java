package com.i112358.savemyeyes;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

public class SetPointsActivity extends Activity {

    private int[] m_newValues = {0, 0, 0};
    Dialog m_dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.points_layout);

        LinearLayout layout = (LinearLayout)findViewById(R.id.pointsLayout);

        Button addButton = (Button)findViewById(R.id.pl_add_button);

        for ( int i = 0; i < BrightnessPointManager.getPointsCount(); i++ ) {
            View point = getLayoutInflater().inflate(R.layout.point_element,null);

            Button button_delete = (Button)point.findViewById(R.id.button_delete_point);
            button_delete.setTag("delete" + i);

            Button button_edit = (Button)point.findViewById(R.id.button_edit_point);
            button_edit.setTag(i);

            TextView text = (TextView)point.findViewById(R.id.brightnessPointText);
            BrightnessPoint brPoint = BrightnessPointManager.getPoint(i);
            text.setText(brPoint.getBrightness() + "% on " + Utilites.convertTime(brPoint.getHour(), brPoint.getMinute()));

            layout.addView(point);
        }


        Log.i("info", "button id is " + addButton.getId());

//        layout.addView(new Button(this));
    }

    public void onEditPointClick( View view )
    {
        Log.i("info", "onEditPointClick" + view.getTag());

        showSetDialog(BrightnessPointManager.getPoint((int)view.getTag()));
    }

    public void onDeletePointClick( View view )
    {
        Log.i("info", "onDeletePointClick " + view.getTag());
    }

    public void onAddPointClick( View view )
    {
        Log.i("info", "onAddPointClick");
        showSetDialog(null);


/*
        int[] m_timeValue = new int[2];
        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {
//                MainActivity.Get().saveNewTime(hour,minute);
            }
        }, m_timeValue[0], m_timeValue[1], true);
        tpd.setCancelable(true);
        tpd.setOnCancelListener(new TimePickerDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.i("info", "onCancel called");
//                saveNewTime(m_previousTimeValue[0], m_previousTimeValue[1]);
            }
        });
        SeekBar seekBar = new SeekBar(this);
        tpd.addContentView(seekBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tpd.show();
*/
    }

    private void showSetDialog( BrightnessPoint point )
    {
        m_dialog = new Dialog(this);
        m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dialog.setContentView(R.layout.setup_point);
        m_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                m_newValues[0] = m_newValues[1] = m_newValues[2] = 0;
                Log.i("info", "Dismiss is " + Utilites.convertTime(m_newValues[0], m_newValues[1]) + " Brightness is " + m_newValues[2]);
            }
        });

        final TextView brightness = (TextView)(m_dialog.findViewById(R.id.textSetPointBrightness));

        Button doneButton = (Button)(m_dialog.findViewById(R.id.buttonSetPointDone));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrightnessPointManager.addPoint(new BrightnessPoint(m_newValues[0],m_newValues[1],m_newValues[2]));
                BrightnessPointManager.saveToPreferences(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
                Log.i("info", "Time is " + Utilites.convertTime(m_newValues[0], m_newValues[1]) + " Brightness is " + m_newValues[2]);
                m_dialog.dismiss();
            }
        });

        SeekBar seekBar = (SeekBar)(m_dialog.findViewById(R.id.seekBar));
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_newValues[2] = seekBar.getProgress();
                brightness.setText("Brightness " + seekBar.getProgress() + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        TimePicker timePicker = (TimePicker)(m_dialog.findViewById(R.id.timePicker));
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                m_newValues[0] = hourOfDay;
                m_newValues[1] = minute;
            }
        });

        if ( point != null ) {
            seekBar.setProgress(point.getBrightness());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(point.getHour());
                timePicker.setMinute(point.getMinute());
            } else {
                timePicker.setCurrentHour(point.getHour());
                timePicker.setCurrentMinute(point.getMinute());
            }
        } else {
            seekBar.setProgress(seekBar.getMax());
        }

        brightness.setText("Brightness " + seekBar.getProgress() + "%");

        m_dialog.show();
    }

}
