package cav.vopros;


import android.content.Intent;
import android.content.SharedPreferences;
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

import cav.vopros.services.TaskService;
import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.RecordModel;
import cav.vopros.utils.StatisticAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MAIN";
    private Button mServiceBtn;
    private TextView mCountRecord;

    private ListView mListView;

    private SharedPreferences mPreferences;


    private Boolean mStatusService = false;

    private List testDate = new ArrayList();

    private SimpleCursorAdapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testDate.add(new RecordModel("28.02.2017","V – 38 X – 42"));
        testDate.add(new RecordModel("25.02.2017","V – 238 X – 142"));


        // формируем столбцы сопоставления
        String[] from = new String[] {"date_rec","count_no" };
        int[] to = new int[] { R.id.dateRec, R.id.count_record};

        mServiceBtn = (Button) findViewById(R.id.start_btn);
        mServiceBtn.setOnClickListener(this);

        mCountRecord = (TextView) findViewById(R.id.count_record);

        mListView = (ListView) findViewById(R.id.list);

        ArrayAdapter adapter = new StatisticAdapter(this,R.layout.list_item,testDate);
        mListView.setAdapter(adapter);

        if (savedInstanceState!=null) {
            mStatusService = savedInstanceState.getBoolean(ConstantManager.START_SERVICE_FLAG);
        }

    }

    private void setupBar(){


    }

    @Override
    protected void onResume() {
        super.onResume();

        mCountRecord.setText("Всего \"V\" – 144  Всего \"X\" – 105");// TEST
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
                startStopService();
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

    }


}
