package cav.vopros.managers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Kotov Alexandr on 28.02.17.
 */
public class DbConnector {
    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "vopros.db3";
    private static final String TAG = "DB CONNECTOR";


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


    public void updateRec(String data_rec,Boolean no,Boolean yes){
        open();
        String sql = null;
        if (no){
            sql="update statistic set count_no=count_no+1 where data_rec='"+data_rec+"';";
        }
        if (yes) {
            sql="update statistic set count_yes=count_yes+1 where data_rec='"+data_rec+"';";
        }
        db.execSQL(sql);
        if (no){
            sql="insert or ignore into statistic (data_rec,count_no,count_yes) values (current_date,1,0);";
        }
        if (yes){
           sql="insert or ignore into statistic (data_rec,count_no,count_yes) values (current_date,0,1);";
        }
        db.execSQL(sql);

        close();
    }

    public Cursor getAllRecord(){
        return db.query("statistic",new String[] {"_id","data_rec","count_no","count_yes"},null,null,null,null,"data_rec DESC");
    }

    public ArrayList<Integer> getCountStatistic(){
        ArrayList<Integer> rec= new ArrayList<>();
       // open();
        Cursor cursor = db.rawQuery("select sum(count_no) as cno,sum(count_yes) as cyes from statistic;",null);
        cursor.moveToFirst();
        rec.add(cursor.getInt(cursor.getColumnIndex("cno")));
        rec.add(cursor.getInt(cursor.getColumnIndex("cyes")));
       // close();
        return rec;
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
                db.execSQL("create table statistic (_id integer not null primary key autoincrement,"+
                        " data_rec text not null unique ,"+
                        "count_no integer default 0,count_yes integer default 0)");
            }

        }
    }

}
