package cav.vopros;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cav.vopros.managers.DbConnector;
import cav.vopros.services.AlarmTaskReciver;
import cav.vopros.services.TaskService;
import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.RecordModel;
import cav.vopros.utils.StatisticAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MAIN";
    private Button mServiceBtn;
    private TextView mCountRecord;

    private ListView mListView;

    private SharedPreferences mPreferences;


    private Boolean mStatusService = false;

    private List testDate = new ArrayList();

    private SimpleCursorAdapter scAdapter;

    private DbConnector db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testDate.add(new RecordModel("28.02.2017","V – 38 X – 42"));
        //testDate.add(new RecordModel("25.02.2017","V – 238 X – 142"));

        db = new DbConnector(this);
        db.open();

        // формируем столбцы сопоставления
        String[] from = new String[] {"data_rec","count_no","count_yes" };
        int[] to = new int[] { R.id.dateRec, R.id.countRec,R.id.countYesRec};

        mServiceBtn = (Button) findViewById(R.id.start_btn);
        mServiceBtn.setOnClickListener(this);

        mCountRecord = (TextView) findViewById(R.id.count_record);

        scAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null, from, to, 0);

        mListView = (ListView) findViewById(R.id.list);

        //ArrayAdapter adapter = new StatisticAdapter(this,R.layout.list_item,testDate);
        //mListView.setAdapter(adapter);
        mListView.setAdapter(scAdapter);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mPreferences!=null) {
            Log.d(TAG,"Читаем настройки 2");
            mStatusService = mPreferences.getBoolean(ConstantManager.START_SERVICE_FLAG,false);
            Log.d(TAG,mPreferences.getString("message_txt",""));
        }

        if (savedInstanceState!=null) {
            Log.d(TAG,"Читаем настройки");
            mStatusService = savedInstanceState.getBoolean(ConstantManager.START_SERVICE_FLAG);
            Log.d(TAG,savedInstanceState.getString("message_txt"));

        }

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);


    }

    private void setupBar(){


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStatusService) {
            mServiceBtn.setText(getString(R.string.btn_end_message));
        }else {
            mServiceBtn.setText(getString(R.string.btn_start_message));
        }

        ArrayList<Integer> all_count = db.getCountStatistic();
        String s = getString(R.string.total_txt)+" \"V\" – "+Integer.toString(all_count.get(1))+"    "+
                getString(R.string.total_txt)+" \"X\" – "+all_count.get(0).toString();

        //mCountRecord.setText("Всего \"V\" – 144  Всего \"X\" – 105");// TEST
        mCountRecord.setText(s);
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db!=null) {
            db.close();
        }
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
                startActivity(new Intent(this,SettingActivty.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_btn:
                //startStopService();
                startStopServiceAlartm();
                break;
        }
    }

    private void startStopService() {
        if (mStatusService) {
            // запущено
            mStatusService = false;
            mServiceBtn.setText(getString(R.string.btn_start_message));
            Log.d(TAG,"STOP");
            stopService(new Intent(this,TaskService.class));
        }else {
            // остановлено
            mStatusService = true;
            mServiceBtn.setText(getString(R.string.btn_end_message));
            Log.d(TAG,"START");
            startService(new Intent(this, TaskService.class));
        }
        saveStatusFlag();
    }

    private void startStopServiceAlartm(){
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this, AlarmTaskReciver.class);
        PendingIntent pi= PendingIntent.getBroadcast(this,0, intent,0);
       // am.cancel(pi);

        if (mStatusService) {
            // запущено
            mStatusService = false;
            mServiceBtn.setText(getString(R.string.btn_start_message));
            Log.d(TAG,"STOP");
            am.cancel(pi);

        }else {
            // остановлено
            mStatusService = true;
            mServiceBtn.setText(getString(R.string.btn_end_message));
            Log.d(TAG,"START");
            int period = Integer.parseInt(mPreferences.getString("time_delay","1"));
            // типа скоката минут
            am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000*60*period,pi);

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
            Log.d(TAG,"LOAD CURSURO");
            Cursor cursor = db.getAllRecord();
            return cursor;
        }
    }


}
