package cav.vopros.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;


import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        Func.startStopServiceAlartm(context,false,0);
        showNotification(context);
    }


    private void showNotification(Context context){
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int period = Integer.parseInt(mPreferences.getString(ConstantManager.PREF_TIME_DELAY,"12"));
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
                    .setTicker("Важное сообщение!")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(mPreferences.getString("message_txt", ""))
                    .setSound(Uri.parse(rington));
                    //.setContentText(mPreferences.getString("message_txt", "")); // Текст уведомления;
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
            intentOk.putExtra(ConstantManager.ACTION_PERIOD,period);
            PendingIntent piOk = PendingIntent.getService(context,1,intentOk,PendingIntent.FLAG_CANCEL_CURRENT);

            //Intent delIntent = new Intent(context,TaskSaveService.class);
            //delIntent.setAction(ConstantManager.ACTION_DEL);
            //delIntent.putExtra(ConstantManager.ACTION_PERIOD,period);
            //PendingIntent delPi = PendingIntent.getService(context,2,delIntent,PendingIntent.FLAG_CANCEL_CURRENT);

            int index = mPreferences.getInt(ConstantManager.IMAGE_INDEX,0);

            List img;

            if (mPreferences.getBoolean("all_image_sd",false) && mPreferences.getString("path_to_img","").length()!=0){
                img = Func.listFilesWithSubFolders(new File(mPreferences.getString("path_to_img","")));
            }else {
                img = Func.getAllImage(context);
            }

            Log.d(TAG,"SELECT PATH : "+mPreferences.getString("path_to_img",""));
           // Rollbar.reportMessage(TAG+" SELECT PATH : "+mPreferences.getString("path_to_img",""), "debug");
            Log.d(TAG,"COUNT :"+img.size());

            if (index>=img.size()){
                index = 0;
            }

            builder.setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setTicker("Важное сообщение!")
                    .setWhen(System.currentTimeMillis())
                    //.setContentTitle("Важное сообщение!")
                    .setContentTitle(mPreferences.getString("message_txt", ""))
                    .setSound(Uri.parse(rington))
                    .setContentText(mPreferences.getString("message_txt", ""))
                    .addAction(R.drawable.ic_close_black_24dp,"",pi)
                    .addAction(R.drawable.ic_check_black_24dp,"",piOk)
                    .setOngoing(true)
                    .setAutoCancel(true);
            try {
                if (img.size() != 0) {
                    builder.setStyle(new Notification.BigPictureStyle()
                                    //.bigPicture(BitmapFactory.decodeFile("/storage/sdcard0/Img/0a76308d91fb8bb0.jpg"))
                                    .bigPicture(Func.getPicSize(String.valueOf(img.get(index))))
                            //.bigPicture(BitmapFactory.decodeFile(String.valueOf(img.get(index))))
                           /* .setSummaryText(mPreferences.getString("message_txt", ""))*/);
                }
            }catch (Exception e){
               // Rollbar.reportException(e, "critical", "Set Image");
            }

            index += 1;
            Func.saveIndexImage(mPreferences,index);


            // картинка
            //notification = new Notification.BigPictureStyle(builder)
            //        .bigPicture(BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20170304_183610.jpg"))
            //        .setSummaryText(mPreferences.getString("message_txt", ""))
            //        .build();
            notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

        }

        //Func.saveStartAlarmTask(mPreferences,true);

        notificationManager.notify(ConstantManager.NOTIFY_ID, notification);
        saveNextTime(period);
    }

    // сохраняем время следующего выполнения ?
    private void saveNextTime(int period){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE,period);
        String hm = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(ConstantManager.NEXT_TIME,hm);
        editor.apply();

    }

}
