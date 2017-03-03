package cav.vopros;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cav.vopros.managers.DbConnector;
import cav.vopros.utils.AspectRatioImageView;
import cav.vopros.utils.ConstantManager;
import cav.vopros.utils.OpenFileDialog;

/**
 * Created by Kotov Alexandr on 28.02.17.
 *
 */
public class TaskOutActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "TASK ACTIVITY";
    private TextView mMessage;
    private ImageView mImageView;

    private ImageButton mCancelBtn;
    private ImageButton mOkBtn;

    private DbConnector db;

    private int outImageIndex = 0;

    private SharedPreferences mPreferences;

    private int mScreenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_out);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mMessage = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.out_img_v);

        mCancelBtn = (ImageButton) findViewById(R.id.out_cancel_btn);
        mOkBtn = (ImageButton) findViewById(R.id.out_ok_btn);

        mOkBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        db = new DbConnector(this);

        outImageIndex = mPreferences.getInt(ConstantManager.IMAGE_INDEX,0);

        mMessage.setText(mPreferences.getString("message_txt",""));

        //setImage();
        Display display = getWindowManager().getDefaultDisplay();
        mScreenWidth = display.getWidth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"RESUME");
        setImage();
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
            /*
            case R.id.open_dialog_btn:
                OpenFileDialog fileDialog = new OpenFileDialog(this);
                fileDialog.show();
                break;
                */
        }
        saveIndexImage();
        finish();
    }

    private void saveIndexImage(){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(ConstantManager.IMAGE_INDEX,outImageIndex);
        editor.apply();
    }

    // устанавливает картинку
    private void setImage(){
        List img = getAllImage();
        if (outImageIndex>img.size()){
            outImageIndex = 0;
        }
        setPic((String) img.get(outImageIndex));
        //mImageView.setImageURI(null);
        //mImageView.setImageURI(Uri.fromFile(new File((String) img.get(outImageIndex))));
        outImageIndex += 1;
    }



    // список файлов из указаного каталога
    public ArrayList<File> listFilesWithSubFolders(File dir) {
        ArrayList<File> files = new ArrayList<File>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                files.addAll(listFilesWithSubFolders(file));
            else
                files.add(file);
        }
        return files;
    }

    // масштабирование загружаемой картинки
    private void setPic(String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = mScreenWidth-100;//mImageView.getWidth();
        int targetH = 300;// mImageView.getHeight();

        // Get the dimensions of the bitmap
        // Читаем с inJustDecodeBounds=true для определения размеров
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    // получение списка фсех картинок

    /**
     * Cursor used to access the results from querying for images on the SD card.
     */
    private Cursor cursor;
    /*
     * Column index for the Thumbnails Image IDs.
     */
    private int columnIndex;

    private List getAllImage(){
        List<String> res= new ArrayList<>();
        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {MediaStore.Images.Media.DATA};

// Create the cursor pointing to the SDCard
        cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);
        // Get the column index of the Thumbnails Image ID
        //columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int ic = cursor.getCount();
        String imagePath = null;
        for (int i=0;i<ic;i++){
            cursor.moveToPosition(i);
            // Get the current value for the requested column
            //int imageID = cursor.getInt(columnIndex);
            //Uri fx = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageID);
            imagePath = cursor.getString(columnIndex);
            //Uri fx = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cursor.getString(columnIndex));
            //Log.d(TAG, String.valueOf(fx));
            res.add(imagePath);
            Log.d(TAG,imagePath);
        }
        // Uri.parse("file:///sdcard/playcat.3gp");  // файл в корне карточки
        //mImageView.setImageURI(Uri.fromFile(new File(imagePath)));
        return res;
    }


}
