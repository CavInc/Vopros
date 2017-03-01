package cav.vopros;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cav.vopros.managers.DbConnector;

/**
 * Created by Kotov Alexandr on 28.02.17.
 *
 */
public class TaskOutActivity extends Activity implements View.OnClickListener{

    private TextView mMessage;
    private ImageView mImageView;

    private ImageButton mCancelBtn;
    private ImageButton mOkBtn;

    private DbConnector db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_out);

        mMessage = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.out_img_v);

        mCancelBtn = (ImageButton) findViewById(R.id.out_cancel_btn);
        mOkBtn = (ImageButton) findViewById(R.id.out_ok_btn);

        mOkBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        db = new DbConnector(this);

    }

    @Override
    public void onClick(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dt = sdf.format(new Date());
        switch (view.getId()){
            case R.id.out_cancel_btn:
                db.updateRec(dt,true,false);
                break;
            case R.id.out_ok_btn:
                db.updateRec(dt,false,true);
                break;
        }
    }
}
