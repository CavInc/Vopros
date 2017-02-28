package cav.vopros.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kotov Alexandr on 28.02.17.
 */
public class DbConnector {
    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "";

    private SQLiteDatabase db;
    private DBHelper mDBHelper;

    public DbConnector(Context context) {
        mDBHelper = new DBHelper(context,DB_NAME,null,DB_VERSION);
    }

    public void open(){
        db = mDBHelper.getWritableDatabase();

    }

    public void close(){
        if (db!=null){
            db.close();
        }
    }



    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            updateData(sqLiteDatabase,0,DB_VERSION);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            updateData(sqLiteDatabase,oldVersion,newVersion);

        }

        private void updateData(SQLiteDatabase db,int oldVersion, int newVersion){
            // создаем базу
            if (oldVersion<1){
                db.execSQL("");
            }

        }
    }

}
