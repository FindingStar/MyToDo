package com.example.acer.todo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.acer.todo.receiver.RemindReceiver;

public class AlarmService extends Service {

    private static final String TAG = "AlarmService";

    private  TimePickBinder mTimePickBinder=new TimePickBinder();

    public class TimePickBinder extends Binder{

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmManager alarmManager= (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        Log.d(TAG, "onStartCommand: ");
        int hour=intent.getIntExtra("hour",0);
        int minute=intent.getIntExtra("minute",0);

        Calendar calendar=Calendar.getInstance();
        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute=calendar.get(Calendar.MINUTE);

        int time=((hour-currentHour)*60+(minute-currentMinute))*60*1000;

        Log.d(TAG, "onStartCommand: 目前时间 "+currentHour+";"+currentMinute+"获得的时间： "+hour+":"+minute);
        long startTime= SystemClock.elapsedRealtime()+time;

        Intent i=new Intent(this, RemindReceiver.class);

        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,i,0);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,startTime,pendingIntent);

        return super.onStartCommand(intent,flags,startId);
    }
}
