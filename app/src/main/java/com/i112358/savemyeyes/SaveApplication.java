package com.i112358.savemyeyes;

import android.app.Application;

import com.i112358.savemyeyes.ErrorLogs.RoboErrorReporter;

public class SaveApplication extends Application {

    @Override
    public void onCreate()
    {
        RoboErrorReporter.bindReporter(this);
        super.onCreate();
    }
}