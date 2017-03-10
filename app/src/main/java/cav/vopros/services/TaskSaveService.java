package cav.vopros.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import cav.vopros.managers.DbConnector;
import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.Func;

public class TaskSaveService extends Service {
    private static final String TAG = "TASKSEVISE";
    private DbConnector db;

    private int period = 0;

    private boolean mServiceFlag = false;


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
       // Log.d(TAG,"START SETVICE");
        closeNotification();
       // Log.d(TAG,"action :"+intent.getAction());
       // Log.d(TAG,"content :"+intent.getBooleanExtra("mode",false));
        // установлен ли флаг запуска сервиса кнопкой
        mServiceFlag = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .getBoolean(ConstantManager.START_SERVICE_FLAG,false);

        period = intent.getIntExtra(ConstantManager.ACTION_PERIOD, 12);
        if (intent.getAction()!=ConstantManager.ACTION_DEL) {
           new WorkDB(intent.getBooleanExtra("mode", false)).execute();
        } else {
            Func.startStopServiceAlartm(getBaseContext(),true,period);
            stopSelf();
        }
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
            db.updateRec(dt,!mode,mode);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           // Log.d(TAG,"STOP SERVICE");
           // Log.d(TAG, "PERIOD :"+String.valueOf(period));
            if (mServiceFlag) Func.startStopServiceAlartm(getBaseContext(),true,period);
            stopSelf();
        }
    }
}
