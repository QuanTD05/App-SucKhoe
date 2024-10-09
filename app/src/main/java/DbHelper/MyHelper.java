package DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "steps";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STEP_COUNT = "step_count";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public MyHelper(Context context){
        super(context,"QLSA.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_dangNhap = "CREATE TABLE tb_dangNhap (id INTEGER PRIMARY KEY AUTOINCREMENT,tendangnhap TEXT  NOT NULl, matkhau TEXT, hoten TEXT NOT NULL);";
        db.execSQL(sql_dangNhap);
        db.execSQL("INSERT INTO tb_dangNhap (tendangnhap, matkhau, hoten) VALUES ('trandinhquan66@gmail.com', '123', 'Trần Đình Quân')");
        db.execSQL("INSERT INTO tb_dangNhap (tendangnhap, matkhau, hoten) VALUES ('quandinhtran70@gmail.com', '123', 'Nguyễn Văn Quân')");
       String sql_longBietOn ="CREATE TABLE tb_longBietOn (id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT, dieubieton TEXT  NOT NULL, id_dangNhap REFERENCES tb_dangNhap (id) );";
       String sql_Sleep = "CREATE TABLE tb_sleep(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT, sleep_time TEXT, wake_up_time TEXT, id_dangNhap REFERENCES tb_dangNhap (id)  );";
       db.execSQL(sql_longBietOn);
       db.execSQL(sql_Sleep);
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STEP_COUNT + " INTEGER, " +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, COLUMN_ID REFERENCES tb_dangNhap (id) )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        if (i1>i){
            db.execSQL("DROP TABLE  IF EXISTS tb_dangNhap");
            db.execSQL("DROP TABLE  IF EXISTS tb_longBietOn");
            db.execSQL("DROP TABLE  IF EXISTS tb_sleep");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
    public void insertStepCount(int stepCount) {
        // thêm dữ liệu vào bảng


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STEP_COUNT, stepCount);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public void resetTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.execSQL("delete from sqlite_sequence where name='" + TABLE_NAME + "' ");
    }


    public List<String> getAllSteps() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery("SELECT * FROM " + TABLE_NAME, null);


        Log.d("zzzzz", "getAllSteps: " + c.getCount());
        if(c!= null && c.getCount()>0){
            c.moveToFirst();
            do {
                int id = c.getInt(0);
                int step = c.getInt(1);


                String time = c.getString(2);




                list.add( id + "|" + step + "|" + time);


            }while (c.moveToNext());


        }
        return list;
    }
}




