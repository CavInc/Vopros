package cav.vopros.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.URLUtil;

import java.util.Map;

import cav.vopros.R;
import cav.vopros.TaskOutActivity;

public class AlarmTaskReciver extends BroadcastReceiver {
    private static final String TAG = "ALARM RECIVER";
    private static final int NOTIFY_ID = 10001;
    private SharedPreferences mPreferences;

    public AlarmTaskReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       // throw new UnsupportedOperationException("Not yet implemented");
        Log.d(TAG,"START ALARM RESIVER");
        showNotification(context);

    }
    //http://developer.alexanderklimov.ru/android/notification.php
    //http://startandroid.ru/ru/uroki/vse-uroki-spiskom/164-urok-99-service-uvedomlenija-notifications.html

    private void showNotification(Context context){
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String rington = mPreferences.getString("toast_ringtone","");

        Intent notificationIntent = new Intent(context, TaskOutActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setTicker("Гляньте чего у меня есть !!!!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Важное сообщение!")
                .setSound(Uri.parse(rington))
                .setContentText(mPreferences.getString("message_txt","")); // Текст уведомления;




        if (Build.VERSION.SDK_INT < 16) {
            notification = builder.getNotification(); // до API 16
        } else {
            notification = builder.build();
        }
        notificationManager.notify(NOTIFY_ID, notification);

    }
}
