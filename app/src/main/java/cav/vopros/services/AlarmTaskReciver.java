package cav.vopros.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cav.vopros.R;
import cav.vopros.TaskOutActivity;
import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.Func;

public class AlarmTaskReciver extends BroadcastReceiver {
    private static final String TAG = "ALARM RECIVER";
    private SharedPreferences mPreferences;

    public AlarmTaskReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       // throw new UnsupportedOperationException("Not yet implemented");
        Log.d(TAG,"START ALARM RESIVER");
        //showNotification(context);
       // Func.startStopServiceAlartm(context,false,0);
        showNotif2(context);
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

        Notification notification = null;

        Notification.Builder builder = new Notification.Builder(context);

        if (Build.VERSION.SDK_INT<16) {
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setTicker("Гляньте чего у меня есть !!!!")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("Важное сообщение!")
                    .setSound(Uri.parse(rington))
                    .setContentText(mPreferences.getString("message_txt", "")); // Текст уведомления;

        } else {
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setTicker("Гляньте чего у меня есть !!!!")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("Важное сообщение!")
                    .setSound(Uri.parse(rington))
                    .setContentText(mPreferences.getString("message_txt", ""));
                    //.addAction(R.drawable.ic_close_black_24dp,"No",contentIntent)
                    //.addAction(R.drawable.ic_check_black_24dp,"Yes",contentIntent); // Текст уведомления

            // убираем уведомление, когда его выбрали
            //builder.flags |= Notification.FLAG_AUTO_CANCEL;
            //List img = Func.getAllImage(context);

            // Подготовим большую картинку
            notification = new Notification.BigPictureStyle(builder)
                    .bigPicture(BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20170304_183610.jpg"))
                    .setSummaryText(mPreferences.getString("message_txt", ""))
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


        }


/*

        if (Build.VERSION.SDK_INT < 16) {
            notification = builder.getNotification(); // до API 16
        } else {
            notification = builder.build();
        }
        */
        notificationManager.notify(ConstantManager.NOTIFY_ID, notification);

    }

    private void showNotif2(Context context){
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int period = Integer.parseInt(mPreferences.getString("time_delay","1"));
        String rington = mPreferences.getString("toast_ringtone","");

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = null;

        Notification.Builder builder = new Notification.Builder(context);


        if (Build.VERSION.SDK_INT < 16){
            Intent intent = new Intent(context,TaskOutActivity.class);

            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setTicker("Гляньте чего у меня есть !!!!")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("Важное сообщение!")
                    .setSound(Uri.parse(rington))
                    .setContentText(mPreferences.getString("message_txt", "")); // Текст уведомления;
            notification = builder.getNotification(); // до API 16

        } else {
            Intent intent = new Intent(context,TaskSaveService.class);
            intent.setAction(ConstantManager.ACTION_CANCEL);
            intent.putExtra("mode",false);
            intent.putExtra(ConstantManager.ACTION_PERIOD,period);
            PendingIntent pi = PendingIntent.getService(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

            Intent intentOk = new Intent(context,TaskSaveService.class);
            intentOk.setAction(ConstantManager.ACTION_OK);
            intentOk.putExtra("mode",true);
            intent.putExtra(ConstantManager.ACTION_PERIOD,period);
            PendingIntent piOk = PendingIntent.getService(context,1,intentOk,PendingIntent.FLAG_CANCEL_CURRENT);

            int index = mPreferences.getInt(ConstantManager.IMAGE_INDEX,0);
            List img = Func.getAllImage(context);
            if (index>=img.size()){
                index = 0;
            }


            builder.setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setTicker("Гляньте чего у меня есть !!!!")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Важное сообщение!")
                    .setSound(Uri.parse(rington))
                    .setContentText(mPreferences.getString("message_txt", ""))
                    .addAction(R.drawable.ic_close_black_24dp,"",pi)
                    .addAction(R.drawable.ic_check_black_24dp,"",piOk)
                    .setStyle(new Notification.BigPictureStyle()
                            //.bigPicture(BitmapFactory.decodeFile("/storage/sdcard0/Img/0a76308d91fb8bb0.jpg"))
                            .bigPicture(Func.getPicSize(String.valueOf(img.get(index))))
                            //.bigPicture(BitmapFactory.decodeFile(String.valueOf(img.get(index))))
                            .setSummaryText(mPreferences.getString("message_txt", "")))
                    .setAutoCancel(true);

            index += 1;
            Func.saveIndexImage(mPreferences,index);


            // картинка
            //notification = new Notification.BigPictureStyle(builder)
            //        .bigPicture(BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20170304_183610.jpg"))
            //        .setSummaryText(mPreferences.getString("message_txt", ""))
            //        .build();
            //notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

        }

        notificationManager.notify(ConstantManager.NOTIFY_ID, notification);
    }
}
