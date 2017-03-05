package cav.vopros.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import cav.vopros.managers.DbConnector;
import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.Func;

public class TaskSaveService extends Service {
    private static final String TAG = "TASKSEVISE";
    private DbConnector db;

    public TaskSaveService() {
        db = new DbConnector(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"START SETVICE");
        closeNotification();
        Log.d(TAG,"action :"+intent.getAction());
        Log.d(TAG,"content :"+intent.getBooleanExtra("mode",false));
        new WorkDB(intent.getBooleanExtra("mode",false)).execute();
        return START_NOT_STICKY;
    }

    private void closeNotification(){
        NotificationManager notificationManager = (NotificationManager) getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ConstantManager.NOTIFY_ID);
    }

    private class WorkDB extends AsyncTask<Void,Void,Void>{
        private boolean mode;

        private WorkDB(boolean mode){
            this.mode = mode;

        }

        @Override
        protected Void doInBackground(Void... params) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dt = sdf.format(new Date());
            //TODO переделать на 1 строку без условия
            if (mode) {
                db.updateRec(dt, false, true);
            }else {
                db.updateRec(dt, true, false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG,"STOP SERVICE");
            stopSelf();
        }
    }
}
