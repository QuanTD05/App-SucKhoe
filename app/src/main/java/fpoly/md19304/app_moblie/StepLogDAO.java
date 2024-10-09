package fpoly.md19304.app_moblie;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp StepLogDAO chịu trách nhiệm thao tác dữ liệu bản ghi bước chân trong cơ sở dữ liệu.
 */
public class StepLogDAO {
    private StepDBHelper dbHelper;  // Trợ giúp cơ sở dữ liệu
    private SQLiteDatabase db;        // Cơ sở dữ liệu

    /**
     * Constructor của StepLogDAO.
     *
     * @param context Ngữ cảnh của ứng dụng
     */
    public StepLogDAO(Context context) {
        dbHelper = new StepDBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Thêm bản ghi bước chân mới vào cơ sở dữ liệu.
     *
     * @param stepLog Bản ghi bước chân
     */
    public void addStepLog(StepLog stepLog) {
        ContentValues values = new ContentValues();
        values.put("steps", stepLog.getSteps());
        values.put("date", stepLog.getDate());
        values.put("stepGoal", stepLog.getStepGoal());
        db.insert("StepLogs", null, values);
    }




    /**
     * Xóa bản ghi bước chân khỏi cơ sở dữ liệu.
     *
     * @param id ID của bản ghi
     */
    public void deleteStepLog(int id) {
        db.delete("StepLogs", "id=?", new String[]{String.valueOf(id)});
    }

    /**
     * Lấy bản ghi bước chân theo ngày từ cơ sở dữ liệu.
     *
     * @param date Ngày cần lấy
     * @return Bản ghi bước chân
     */
    public StepLog getStepLogByDate(String date) {
        // Truy vấn cơ sở dữ liệu để lấy bản ghi với ngày cụ thể
        Cursor cursor = db.query("StepLogs", null, "date=?", new String[]{date}, null, null, null);

        // Kiểm tra nếu cursor không null và di chuyển đến bản ghi đầu tiên
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy dữ liệu từ các cột của bản ghi
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") int steps = cursor.getInt(cursor.getColumnIndex("steps"));
            @SuppressLint("Range") int stepGoal = cursor.getInt(cursor.getColumnIndex("stepGoal"));

            // Đóng cursor để giải phóng tài nguyên
            cursor.close();

            // Tạo và trả về đối tượng StepLog với dữ liệu vừa lấy
            return new StepLog(id, steps, date, stepGoal);
        }

        // Trả về null nếu không có bản ghi nào được tìm thấy
        return null;
    }



    public List<StepLog> getAllStepLogs() {
        List<StepLog> stepLogs = new ArrayList<>();
        Cursor cursor = db.query("StepLogs", null, null, null, null, null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") int steps = cursor.getInt(cursor.getColumnIndex("steps"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") int stepGoal = cursor.getInt(cursor.getColumnIndex("stepGoal"));
                stepLogs.add(new StepLog(id, steps, date, stepGoal));
            }
            cursor.close();
        }
        return stepLogs;
    }

    /**
     * Đóng kết nối cơ sở dữ liệu.
     */
    public void close() {
        db.close();
        dbHelper.close();
    }
}
