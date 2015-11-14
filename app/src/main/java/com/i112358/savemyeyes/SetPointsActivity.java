package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.i112358.savemyeyes.Graphics.Line;
import java.util.Calendar;

public class SetPointsActivity extends Activity {

    public static SetPointsActivity Get() { return activity; }
    private static SetPointsActivity activity = null;
    private int[] m_newValues = {0, 0, 0};
    LinearLayout m_layout = null;
    private int m_previousBrightnessValue = 255;
    private boolean m_enableBrightnessPreview = false;
    private int m_deletePointTag = 0;

    private ImageView m_imageView;
    Handler m_handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.points_layout);
        m_layout = (LinearLayout)findViewById(R.id.pointsLayout);
        MainActivity.Get().setNextBrightnessPoint();
        updateSetPointScreen();

        m_imageView = (ImageView)findViewById(R.id.imageView);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.w("info", "SetPointsActivity onResume");
        boolean isGranted = Utilites.isPermissionGranted(getApplicationContext());
        if ( !isGranted ) {
            this.finish();
        }
    }

    public void onEditPointClick( View view )
    {
        showSetDialog(BrightnessPointManager.getPoint((int) view.getTag()));
    }

    private void deletePoint()
    {
        BrightnessPointManager.removePoint(m_deletePointTag);
        BrightnessPointManager.saveToPreferences(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
        updateSetPointScreen();
    }

    public void onDeletePointClick( View view )
    {
        m_deletePointTag = -1;
        m_deletePointTag = (int)view.getTag();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Confirm delete point");
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("info", "Delete confirmed " + m_deletePointTag);
                deletePoint();
            }
        });
        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("info", "Delete cancelled");
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    public void onAddPointClick( View view )
    {
        showSetDialog(null);
    }

    private void showSetDialog( final BrightnessPoint point )
    {
        m_previousBrightnessValue = Utilites.getCurrentBrightness(getContentResolver());

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.setup_point);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                m_newValues[0] = m_newValues[1] = m_newValues[2] = 0;
                Log.i("info", "Dismiss is " + Utilites.convertTime(m_newValues[0], m_newValues[1]) + " Brightness is " + m_newValues[2]);
                updateSetPointScreen();
            }
        });

        Button doneButton = (Button)(dialog.findViewById(R.id.buttonSetPointDone));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int closestPointId = BrightnessPointManager.checkIfPointCloseToNeighbor(m_newValues[0] * 60 + m_newValues[1], point);
                if ( closestPointId != -1 ) {
                    Log.i("info", "point to close to point " + (closestPointId + 1));
                    Toast.makeText(getApplicationContext(), "Point is too close to other point", Toast.LENGTH_LONG).show();
                    return;
                }

                if ( point != null ) {
                    point.updatePoint(m_newValues[0], m_newValues[1], m_newValues[2]);
                } else {
                    BrightnessPointManager.addPoint(new BrightnessPoint(m_newValues[0], m_newValues[1], m_newValues[2]));
                }

                BrightnessPointManager.saveToPreferences(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
                dialog.dismiss();
            }
        });

        SeekBar seekBar = (SeekBar)(dialog.findViewById(R.id.seekBar));
        seekBar.setMax(255);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("info", "switcher onProgressChanged " + seekBar.getProgress());
                if (m_enableBrightnessPreview) {
                    Utilites.setBrightness(getContentResolver(), seekBar.getProgress());
                }
                m_newValues[2] = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("info", "switcher onStartTrackingTouch " + seekBar.getProgress());
                m_enableBrightnessPreview = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("info", "switcher onStopTrackingTouch " + seekBar.getProgress());
                m_enableBrightnessPreview = false;
                Utilites.setBrightness(getContentResolver(), m_previousBrightnessValue);
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
            Calendar calendar = Calendar.getInstance();
            m_newValues[0] = calendar.get(Calendar.HOUR_OF_DAY);
            m_newValues[1] = calendar.get(Calendar.MINUTE);
        }

        dialog.show();
    }

    public void updateSetPointScreen()
    {
        for ( int i = 0; i < m_layout.getChildCount(); i++ ) {
            View point = m_layout.getChildAt(i);
            if ( point != null ) {
                if ( ("point").equals(point.getTag()) ) {
                    i--;
                    m_layout.removeView(point);
                }
            }
        }

        BrightnessPoint nextPoint = BrightnessPointManager.getClosestTimePoint(getSharedPreferences(getString(R.string.PREFERENCES), Context.MODE_PRIVATE));
        BrightnessPointManager.sortPoints();

        int pointsCount = BrightnessPointManager.getPointsCount();
        for ( int i = 0; i < pointsCount; i++ ) {
            BrightnessPoint brPoint = BrightnessPointManager.getPoint(i);
            if ( brPoint != null ) {
                View point = getLayoutInflater().inflate(R.layout.point_element,null);
                Button button_delete = (Button)point.findViewById(R.id.button_delete_point);
                button_delete.setTag(i);
                Button button_edit = (Button)point.findViewById(R.id.button_edit_point);
                button_edit.setTag(i);
                TextView text = (TextView)point.findViewById(R.id.brightnessPointText);
                text.setText(Utilites.convertTime(brPoint.getHour(), brPoint.getMinute()));

                point.setTag("point");

                m_layout.addView(point);

                if ( brPoint.equals(nextPoint) ) {
                    point.findViewById(R.id.pointBackgroundElement).setBackgroundColor(0xFFBFAFA1);
                }
            }
        }

        if ( MainActivity.Get().isChangeBrightnessEnable() && pointsCount > 0 ) {
            if ( !nextPoint.equals(MainActivity.Get().getNextBrightnessPoint()) ) {
                MainActivity.Get().setNextBrightnessPoint();
                MainActivity.Get().changeBrightnessState(true);
            }
        } else {
            MainActivity.Get().changeBrightnessState(false);
        }
        m_handler.postDelayed(createGraphic, 100);
    }

    private Runnable createGraphic = new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap = Bitmap.createBitmap(m_imageView.getWidth(), m_imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            m_imageView.setImageBitmap(bitmap);

            BrightnessPointManager.sortPoints();
            if ( BrightnessPointManager.getPointsCount() > 1 ) {
                int pointNumber = 0;
                BrightnessPoint previous = new BrightnessPoint(0,0, BrightnessPointManager.getPoint(BrightnessPointManager.getPointsCount() - 1).getBrightness());
                while ( pointNumber < BrightnessPointManager.getPointsCount() ) {
                    BrightnessPoint current = BrightnessPointManager.getPoint(pointNumber);
                    Line line = new Line(previous, BrightnessPointManager.getPoint(pointNumber));
                    line.draw(canvas);
                    previous = current;
                    pointNumber++;
                }
                BrightnessPoint current = new BrightnessPoint(24,0, previous.getBrightness());
                Line line = new Line(previous, current);
                line.draw(canvas);
            }

            Calendar calendar = Calendar.getInstance();
            float currentTime = calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE);
            Line time = new Line(currentTime);
            time.setLineWidth(1);
            time.setColor(0xFFDD1122);
            time.draw(canvas);
        }
    };
}