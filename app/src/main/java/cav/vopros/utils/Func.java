package cav.vopros.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cav.vopros.services.AlarmTaskReciver;

/**
 * Created by Kotov Alexandr on 04.03.17.
 *
 */
public class Func {

    public static List getAllImage(Context context){
        /**
         * Cursor used to access the results from querying for images on the SD card.
         */
        Cursor cursor;
        List<String> res= new ArrayList<>();
        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {MediaStore.Images.Media.DATA};

// Create the cursor pointing to the SDCard
        cursor = android.provider.MediaStore.Images.Media.query(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection);

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int ic = cursor.getCount();
        String imagePath = null;
        for (int i=0;i<ic;i++){
            cursor.moveToPosition(i);
            imagePath = cursor.getString(columnIndex);
            res.add(imagePath);
           // Log.d("FUNC",imagePath);
        }
        return res;
    }

    public static void startStopServiceAlartm(Context context,boolean modeService,int period){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, AlarmTaskReciver.class);
        PendingIntent pi= PendingIntent.getBroadcast(context,0, intent,0);
        if (modeService){
// типа скоката минут  для правильного старта добавть вместо System.currentTimeMillis() System.currentTimeMillis()+period
            am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+period,1000*60*period,pi);
            Log.d("FUNC","START ALARM FUNC");
        }else {
            am.cancel(pi);
            Log.d("FUNC","STOP ALARM FUNC");
        }
    }

    public static void saveIndexImage(SharedPreferences pref,int index){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(ConstantManager.IMAGE_INDEX,index);
        editor.apply();
    }

    @SuppressWarnings({"deprecation"})
    public static Bitmap getPicSize(String mCurrentPhotoPath){
        int targetW = 400;
        int targetH = 300;

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

        return bitmap;

    }

}
