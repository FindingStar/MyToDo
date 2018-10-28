package com.example.acer.todo.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.acer.todo.R;

import java.io.File;

public class RemindReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//
        Notification notification= new NotificationCompat.Builder(context,"default")
                .setVibrate(new long[]{0,1000,1000,1000})
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setContentText("hhh")
                .setSmallIcon(R.drawable.icon_red)
                .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Blues.ogg")))
                .setVibrate(new long[]{0,1000,1000,1000})
                .build();
        notificationManager.notify(1,notification);
    }
}
