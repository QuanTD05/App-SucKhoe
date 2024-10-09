package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import DbHelper.MyHelper;


public class NguoiDungDAO {
    private MyHelper myHelper;

    public NguoiDungDAO(Context context){
        myHelper = new MyHelper(context);
    }
    //login
    public boolean CheckLogin(String username, String password){
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tb_dangNhap WHERE tendangnhap = ? AND matkhau = ?", new String[]{username,password});
        if (cursor.getCount() >0 ){
            return true;
        }
        return false;
    }
    // register == dăng kí
    public boolean Register(String username,String password, String hoten){
        SQLiteDatabase sqLiteDatabase = myHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tendangnhap", username);
        contentValues.put("matkhau",password);
        contentValues.put("hoten",hoten);

        long check = sqLiteDatabase.insert("tb_dangNhap",null,contentValues);
        if (check != -1){
            return true;
        }
        return false;
    }
    // forgot

    public String ForgotPassWord(String email){
        SQLiteDatabase sqLiteDatabase = myHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT matkhau FROM tb_dangNhap WHERE tendangnhap = ? ", new String[]{email});
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            return cursor.getString(0);
        }else {
            return "";
        }
        // 1 4 5 7 2
    }
}


