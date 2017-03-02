package cav.vopros;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cav.vopros.managers.DbConnector;

/**
 * Created by Kotov Alexandr on 28.02.17.
 *
 */
public class TaskOutActivity extends Activity implements View.OnClickListener{

    private static final int REQUEST = 1020;
    private static final String TAG = "TASK ACTIVITY";
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

        //setImage();
        getAllImage();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getFileList();
       // getStandartPictyre();
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
        finish();
    }

    private void setImage(){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, String.valueOf(data.getData()));
            mImageView.setImageURI(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getStandartPictyre(){
         File album = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
         File[] files = album.listFiles();
        for (File f:files){
            if (f.isDirectory()){
                System.out.println("Folder: "+f);
            }else if (f.isFile()){
                System.out.println("File:" +f);
            }
        }
    }

    private void getFileList(){

        File rootFolder = Environment.getExternalStorageDirectory();
        File[] filesArray = rootFolder.listFiles();

        System.out.println("файлов: " + filesArray.length);
        for (File f: filesArray) {
            if (f.isDirectory()) System.out.println("Folder: " + f);
            else if (f.isFile()) System.out.println("File: " + f);
        }
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
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
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

    private void getAllImage(){
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
            Log.d(TAG,imagePath);
        }
        // Uri.parse("file:///sdcard/playcat.3gp");  // файл в корне карточки
        mImageView.setImageURI(Uri.fromFile(new File(imagePath)));

    }

}
