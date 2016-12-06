package com.example.gaopj.class1test1;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gpj on 2016/11/16.
 */

public class test extends Service {
    int counter = 0;
    static final int UPDATE_INTERVAL = 1000000;
    private Timer timer = new Timer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doSomethingRepeatedly();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.i("init", "doSomethingRepeatedly");
            }
        }, 0, UPDATE_INTERVAL);
    }
}
