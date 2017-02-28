package cav.vopros.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import cav.vopros.R;
import cav.vopros.TaskOutActivity;

public class TaskService extends Service {
    private static final String TAG = "TASK SERVICE";
    private static final int NOTIFY_ID = 1000;

    public TaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"START SERVICE");
        myTask();
        return super.onStartCommand(intent, flags, startId);
    }

    private void myTask(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showNotification();
                Log.d(TAG,"WORK");
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"SERVICE DESTROY");
    }

    private void showNotification(){
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, TaskOutActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Гляньте чего у меня есть !!!!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Важное сообщение!")
                .setContentText("Здесть пока нифига нет "); // Текст уведомления;

        if (Build.VERSION.SDK_INT < 16) {
            notification = builder.getNotification(); // до API 16
        } else {
            notification = builder.build();
        }
        notificationManager.notify(NOTIFY_ID, notification);

    }
}
