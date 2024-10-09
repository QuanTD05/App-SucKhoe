package fpoly.md19304.app_moblie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Lớp DatabaseHelper giúp tạo và quản lý cơ sở dữ liệu SQLite.
 */
public class StepDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StepCounter.db"; // Tên cơ sở dữ liệu
    private static final int DATABASE_VERSION = 1;               // Phiên bản cơ sở dữ liệu

    // Câu lệnh tạo bảng StepLogs
    private static final String CREATE_TABLE_STEPLOGS =
            "CREATE TABLE StepLogs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "steps INTEGER, " +
                    "date TEXT, " +
                    "stepGoal INTEGER)";


    public StepDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STEPLOGS); // Tạo bảng StepLogs
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS StepLogs"); // Xóa bảng StepLogs cũ
        onCreate(db);                                // Tạo lại bảng mới
    }
}
