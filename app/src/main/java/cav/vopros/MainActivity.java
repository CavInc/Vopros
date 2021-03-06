package cav.vopros;



import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cav.vopros.managers.DbConnector;
import cav.vopros.services.AlarmTaskReciver;
import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.Func;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MAIN";
    private static final int PERMISSION_REQUEST_CODE = 122;
    private Button mServiceBtn;
    private TextView mCountRecord;
    private TextView mNextTimer;

    private ListView mListView;

    private SharedPreferences mPreferences;


    private Boolean mStatusService = false;

    private SimpleCursorAdapter scAdapter;

    private DbConnector db;

    private BroadcastReceiver broadcastReceiver;
    public final static String BROADCAST_ACTION = "cav.vopros.broadcast_refresh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DbConnector(this);
        db.open();

        // формируем столбцы сопоставления
        String[] from = new String[] {"data_rec","count_no","count_yes" };
        int[] to = new int[] { R.id.dateRec, R.id.countRec,R.id.countYesRec};

        mServiceBtn = (Button) findViewById(R.id.start_btn);
        mServiceBtn.setOnClickListener(this);

        mCountRecord = (TextView) findViewById(R.id.count_record);
        mNextTimer = (TextView) findViewById(R.id.next_timer);

        scAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null, from, to, 0);

        mListView = (ListView) findViewById(R.id.list);


        mListView.setAdapter(scAdapter);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mPreferences!=null) {
            mStatusService = mPreferences.getBoolean(ConstantManager.START_SERVICE_FLAG,false);
            mNextTimer.setText(getString(R.string.next_timer_str)+mPreferences.getString(ConstantManager.NEXT_TIME,"None"));

        }else {
            mNextTimer.setText(getString(R.string.next_timer_str)+" None");
        }

        setupBar();
        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"UPD IN SERVICE");
                boolean res = intent.getBooleanExtra(ConstantManager.UPDATE_SERVICE_DATA,false);
                if (res) {
                    updateUI();
                }
            }
        };

        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(broadcastReceiver, intFilt);
    }

    private void setupBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStatusService) {
            mServiceBtn.setText(getString(R.string.btn_end_message));
        }else {
            mServiceBtn.setText(getString(R.string.btn_start_message));
        }
        updateUI();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
        //Log.d(TAG, String.valueOf(Func.isAlarm(this)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db!=null) {
            db.close();
        }
        unregisterReceiver(broadcastReceiver);
    }

    private void updateUI(){
        ArrayList<Integer> all_count = db.getCountStatistic();
        String s = getString(R.string.total_txt)+" \"V\" – "+Integer.toString(all_count.get(1))+"    "+
                getString(R.string.total_txt)+" \"X\" – "+all_count.get(0).toString();

        mCountRecord.setText(s);
        getSupportLoaderManager().getLoader(0).forceLoad();
        mNextTimer.setText(getString(R.string.next_timer_str)+mPreferences.getString(ConstantManager.NEXT_TIME,"None"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // если не получили права
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                // TODO переделать на фрагмент
                //Intent intent = new Intent(this,SettingActivty.class);

                startActivity(new Intent(this,SettingActivty.class));
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_btn:
                startStopServiceAlartm();
                break;
        }
    }


    private void startStopServiceAlartm(){
       // AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       // Intent intent=new Intent(this, AlarmTaskReciver.class);
       // PendingIntent pi= PendingIntent.getBroadcast(this,0, intent,0);

       // am.cancel(pi);

        if (mStatusService) {
            // запущено
            mStatusService = false;
            mServiceBtn.setText(getString(R.string.btn_start_message));
            //am.cancel(pi);
            Func.startStopServiceAlartm(this,false,0);

        }else {
            // остановлено
            mStatusService = true;
            mServiceBtn.setText(getString(R.string.btn_end_message));
            int period = Integer.parseInt(mPreferences.getString(ConstantManager.PREF_TIME_DELAY,"12"));
            // типа скоката минут  для правильного старта добавть вместо System.currentTimeMillis() System.currentTimeMillis()+period
            //am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000*60*period,pi);
            Func.startStopServiceAlartm(this,true,period);
        }
        saveStatusFlag();
    }

    private void saveStatusFlag(){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(ConstantManager.START_SERVICE_FLAG,mStatusService);
        editor.apply();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new MyCursorLoader(this,db);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    static class MyCursorLoader extends CursorLoader {
        DbConnector db;

        public MyCursorLoader(Context context,DbConnector db) {
            super(context);
            this.db = db;

        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllRecord();
            return cursor;
        }
    }


}
