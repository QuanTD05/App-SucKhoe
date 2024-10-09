package DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import DTO.longBietOnDTO;
import DbHelper.MyHelper;


public class LongBietOnDAO {
    MyHelper myHelper;
    SQLiteDatabase db;
    public LongBietOnDAO(Context context){
        myHelper = new MyHelper(context);
        db = myHelper.getWritableDatabase();// khởi tạo đối tượng db
    }

    public ArrayList<longBietOnDTO> getList() {
        ArrayList<longBietOnDTO> listCat = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT date, dieubieton FROM tb_longBietOn", null);
        if (c != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // định dạng ngày trong cơ sở dữ liệu

            try {
                if (c.moveToFirst()) {
                    do {
                        String ngayString = c.getString(0); // cột ngày
                        String dieubieton = c.getString(1); // cột dieubieton

                        // Chuyển đổi chuỗi ngày tháng thành đối tượng Date
                        Date ngay = null;
                        try {
                            ngay = dateFormat.parse(ngayString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Tạo đối tượng DTO
                        longBietOnDTO objCat = new longBietOnDTO();
                        if (ngay != null) {
                            objCat.setNgay(ngay);
                        }
                        objCat.setDieuBietOn(dieubieton);

                        // Thêm objCat vào list
                        listCat.add(objCat);
                    } while (c.moveToNext());
                }
            } finally {
                c.close(); // Đóng Cursor để giải phóng tài nguyên
            }
        }

        return listCat;
    }


    // Tên bảng và cột trong cơ sở dữ liệu
//    private static final String TABLE_LONG_BIET_ON = "tb_longBietOn";
//    private static final String COLUMN_GRATITUDE = "dieubieton";
//    private static final String COLUMN_ID_DANGNHAP = "id_dangNhap";
//
//    public LongBietOnDAO(Context context) {
//        myHelper = new MyHelper(context);
//    }
//
//    // Thêm lời cảm ơn mới vào cơ sở dữ liệu
//    public void addGratitude(String dieubieton, int idDangNhap) {
//        SQLiteDatabase db = myHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_GRATITUDE, dieubieton);
//        values.put(COLUMN_ID_DANGNHAP, idDangNhap);
//        db.insert(TABLE_LONG_BIET_ON, null, values);
//        db.close();
//    }
//
//    // Lấy danh sách tất cả lời cảm ơn từ cơ sở dữ liệu
//    public List<String> getAllGratitudes() {
//        List<String> gratitudeList = new ArrayList<>();
//        String selectQuery = "SELECT * FROM " + TABLE_LONG_BIET_ON;
//        SQLiteDatabase db = myHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                String gratitude = cursor.getString(cursor.getColumnIndex(COLUMN_GRATITUDE));
//                gratitudeList.add(gratitude);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//        return gratitudeList;
//    }
}
