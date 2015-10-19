package com.i112358.savemyeyes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by gleb on 06.10.15.
 */
public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
//        Log.i("info", "Alarm onRecieve");
//        Toast.makeText(context, "onReceive Alarm", Toast.LENGTH_LONG).show();
        Intent intent1 = new Intent(context, ChangeBrightnessService.class);
        intent1.putExtra("brightness", intent.getIntExtra("brightness", 255));
        context.startService(intent1);
    }

    public void setAlarm( final Context context, final BrightnessPoint point )
    {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.setAction(context.getPackageName());
        intent.putExtra("brightness", point.getBrightness());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, point.getHour());
        calendar.set(Calendar.MINUTE, point.getMinute());
        calendar.set(Calendar.SECOND, 0);
        if ( point.isNextDay() ) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        Toast.makeText(context, point.getBrightness() + " will set on " + calendar.getTime().toString(), Toast.LENGTH_LONG).show();
    }

    public void cancelAlarm( final Context context ) // change to Activity
    {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.setAction(context.getPackageName());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);
        Toast.makeText(context, "Cancel next change brightness", Toast.LENGTH_LONG).show();
    }
}