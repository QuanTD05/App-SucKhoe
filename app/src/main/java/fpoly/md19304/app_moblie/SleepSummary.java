package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Adapter.SleepListAdapter;
import DTO.sleepDTO;

public class SleepSummary extends AppCompatActivity {

    private EditText etSleepTime, etWakeUpTime;
    private TextView tvSleepDuration, txtFullName;
    private Button btnSaveSleep;
    private Calendar sleepTime, wakeUpTime;
    private SleepListAdapter adapter;
    private ListView listView;
    private ArrayList<sleepDTO> sleepList;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private CollectionReference sleepRecordsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_summary);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        db = FirebaseFirestore.getInstance();
        sleepRecordsRef = db.collection("sleep").document(currentUser.getUid()).collection("sleepRecords");

        // Initialize UI components
        etSleepTime = findViewById(R.id.et_sleep_time);
        etWakeUpTime = findViewById(R.id.et_wake_up_time);
        tvSleepDuration = findViewById(R.id.tv_sleep_duration);
        btnSaveSleep = findViewById(R.id.btn_save_sleep);
        listView = findViewById(R.id.list_view);
        txtFullName = findViewById(R.id.name_text);

        // Initialize data and adapter
        sleepList = new ArrayList<>();
        adapter = new SleepListAdapter(this, sleepList);
        listView.setAdapter(adapter);

        sleepTime = Calendar.getInstance();
        wakeUpTime = Calendar.getInstance();

        // Set up event listeners
        etSleepTime.setOnClickListener(v -> showTimePickerDialog(sleepTime, etSleepTime));
        etWakeUpTime.setOnClickListener(v -> showTimePickerDialog(wakeUpTime, etWakeUpTime));
        btnSaveSleep.setOnClickListener(v -> saveSleepRecord());

        // Display existing sleep records
        displaySleepRecords();

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Load user details
        loadUserDetails(currentUser.getUid());

        // Bottom navigation buttons
        setupBottomNavigation();
    }

    private void redirectToLogin() {
        startActivity(new Intent(SleepSummary.this, Login.class));
        finish();
    }

    private void showTimePickerDialog(Calendar time, EditText editText) {
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minutes) -> {
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minutes);
            editText.setText(String.format("%02d:%02d", hourOfDay, minutes));
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void saveSleepRecord() {
        try {
            calculateAndDisplaySleepDuration();

            String date = getCurrentDate();
            String sleepTimeStr = etSleepTime.getText().toString();
            String wakeUpTimeStr = etWakeUpTime.getText().toString();
            String durationStr = tvSleepDuration.getText().toString();

            if (sleepTimeStr.isEmpty() || wakeUpTimeStr.isEmpty()) {
                Toast.makeText(SleepSummary.this, "Vui lòng điền đầy đủ thời gian ngủ và thức dậy.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tính toán thời gian giấc ngủ
            long sleepMillis = sleepTime.getTimeInMillis();
            long wakeUpMillis = wakeUpTime.getTimeInMillis();

            if (wakeUpMillis < sleepMillis) {
                wakeUpMillis += 24 * 60 * 60 * 1000; // Điều chỉnh cho giấc ngủ qua đêm
            }

            long durationMillis = wakeUpMillis - sleepMillis;
            int hours = (int) (durationMillis / (1000 * 60 * 60));

            // Kiểm tra nếu thời gian giấc ngủ quá 8 giờ
            if (hours > 8) {
                showReconfigureTimeDialog();
            } else {
                // Thêm bản ghi vào Firestore và thiết lập báo thức nếu thời gian ngủ hợp lý
                addSleepRecordToFirestore(date, sleepTimeStr, wakeUpTimeStr, durationStr);
                setAlarms();
                displaySleepRecords();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving sleep record", e);
            Toast.makeText(SleepSummary.this, "Đã xảy ra lỗi khi lưu bản ghi giấc ngủ.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showReconfigureTimeDialog() {
        new AlertDialog.Builder(SleepSummary.this)
                .setTitle("Thiết lập lại giờ")
                .setMessage("\uD83D\uDCA4 Giấc Ngủ Không Cố Định Khuyến nghị ngủ 8 tiếng mỗi ngày không phải là chuẩn mực cho tất cả mọi người, vì nhu cầu giấc ngủ có thể khác nhau tùy theo mỗi cá nhân\n" +
                        "⏰ Lịch Trình Ngủ Quan Trọng Nghiên cứu cho thấy việc duy trì lịch trình ngủ đều đặn quan trọng hơn việc ngủ đủ 8 tiếng\n" +
                        "\uD83D\uDCC9 Tác Động Khi Ngủ Thiếu Nếu ngủ ít hơn 7 tiếng, khả năng hoạt động của cơ thể và tư duy có thể bị giảm sút")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Xóa các trường giờ để người dùng nhập lại
                    etSleepTime.setText("");
                    etWakeUpTime.setText("");
                    tvSleepDuration.setText("");

                    // Đưa người dùng đến đầu vào để nhập lại giờ
                    etSleepTime.requestFocus();
                })
                .create()
                .show();
    }

    private void addSleepRecordToFirestore(String date, String sleepTimeStr, String wakeUpTimeStr, String durationStr) {
        try {
            if (sleepRecordsRef == null) {
                Log.e(TAG, "Firestore reference is null");
                return;
            }

            sleepDTO sleepRecord = new sleepDTO(date, sleepTimeStr, wakeUpTimeStr, durationStr);

            sleepRecordsRef.add(sleepRecord)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(SleepSummary.this, "Đã lưu bản ghi giấc ngủ", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(SleepSummary.this, "Lỗi khi lưu bản ghi giấc ngủ", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error adding record to Firestore", e);
            Toast.makeText(SleepSummary.this, "Đã xảy ra lỗi khi thêm bản ghi vào Firestore.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAlarm(Context context, Calendar time, String message) {
        try {
            if (time == null) {
                Log.e(TAG, "Alarm time is null");
                return;
            }

            Intent intent = new Intent(context, SleepReminderReceiver.class);
            intent.putExtra("message", message);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
                }
            } else {
                Log.e(TAG, "AlarmManager is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting alarm", e);
        }
    }

    private void setAlarms() {
        Calendar sleepReminder = (Calendar) sleepTime.clone();
        Calendar wakeUpReminder = (Calendar) wakeUpTime.clone();

        // Set alarm to notify at sleep time
        setAlarm(this, sleepReminder, "Đã đến giờ đi ngủ!");

        // Set alarm to notify at wake up time
        setAlarm(this, wakeUpReminder, "Đã đến giờ thức dậy!");
    }


    private void displaySleepRecords() {
        sleepList.clear();
        sleepRecordsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                sleepDTO record = document.toObject(sleepDTO.class);
                                sleepList.add(record);
                                Log.d(TAG, "Added record: " + record);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void loadUserDetails(String userId) {
        DocumentReference docRef = db.collection("taikhoan").document(userId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        txtFullName.setText(name);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
    }

    private void setupBottomNavigation() {
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(v -> startActivity(new Intent(SleepSummary.this, MainActivity2.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(SleepSummary.this, MainActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(SleepSummary.this, MainActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(SleepSummary.this, trangcanhan.class)));
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void calculateAndDisplaySleepDuration() {
        long sleepMillis = sleepTime.getTimeInMillis();
        long wakeUpMillis = wakeUpTime.getTimeInMillis();

        if (wakeUpMillis < sleepMillis) {
            wakeUpMillis += 24 * 60 * 60 * 1000; // Adjust for overnight sleep
        }

        long durationMillis = wakeUpMillis - sleepMillis;
        int hours = (int) (durationMillis / (1000 * 60 * 60));
        int minutes = (int) ((durationMillis % (1000 * 60 * 60)) / (1000 * 60));
        tvSleepDuration.setText(String.format("%d giờ %d phút", hours, minutes));
    }
}
