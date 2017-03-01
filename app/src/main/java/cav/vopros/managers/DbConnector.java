package cav.vopros.managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kotov Alexandr on 28.02.17.
 */
public class DbConnector {
    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "vopros.db3";


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

    public void insertRec(Boolean no,Boolean yes){
        open();

        close();
    }

    public void updateRec(String data_rec,Boolean no,Boolean yes){
        open();
        String sql = null;
        if (no){
            sql="update statistic set count_no=count_no+1 where date_rec='"+data_rec+"'; select changes();";
        }
        if (yes) {
            sql="update statistic set count_yes=count_yes+1 where date_rec='"+data_rec+"'; select changes();";
        }

        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        if (cursor.getInt(0)==0) {
            // нет нифига нужно добавить
            if (no) {
                db.execSQL("insert into statistic (data_rec,count_no,count_yes) values (current_date,1,0)");
            }
            if (yes){
                db.execSQL("insert into statistic (data_rec,count_no,count_yes) values (current_date,0,1)");
            }
        }
        close();
    }

    public Cursor getAllRecord(){
        return db.query("statistic",new String[] {"data_rec","count_no","count_yes"},null,null,null,null,"data_rec DESC");
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
                db.execSQL("create table statistic (data_rec text not null primary key,"+
                        "count_no integer default 0,count_yes integer default 0)");
            }

        }
    }

}
