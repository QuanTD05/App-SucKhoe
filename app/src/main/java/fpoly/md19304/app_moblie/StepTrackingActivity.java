package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StepTrackingActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isCounting = false;
    private int currentSteps = 0;
    private int stepGoal = 10000; // Mục tiêu mặc định

    private TextView txtCurrentSteps, txtGoalStatus;
    private EditText edtStepGoal;
    private RecyclerView recyclerView;
    private StepLogDAO stepLogDAO;


    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_step_tracking);
        txtCurrentSteps = findViewById(R.id.txtCurrentSteps);
        txtGoalStatus = findViewById(R.id.txtGoalStatus);
        edtStepGoal = findViewById(R.id.edtStepGoal);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stepLogDAO = new StepLogDAO(this);
        displayStepLogs();

        // Thiết lập cảm biến
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        // Kiểm tra và yêu cầu quyền truy cập
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE);
        }

        // Thiết lập sự kiện cho các nút
        Button btnStartCounting = findViewById(R.id.btnStartCounting);
        btnStartCounting.setOnClickListener(v -> startCountingSteps());

        Button btnSetGoal = findViewById(R.id.btnSetGoal);
        btnSetGoal.setOnClickListener(v -> setStepGoal());

        Button btnResetCounter = findViewById(R.id.btnResetCounter);
        btnResetCounter.setOnClickListener(v -> resetCounter());

        Button btnThoat = findViewById(R.id.btnThoat);
        btnThoat.setOnClickListener(v -> finish());
        setupBottomNavigation();
    }

    private void startCountingSteps() {
        if (!isCounting) {
            isCounting = true;
            if (stepCounterSensor != null) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
                Toast.makeText(this, "Bắt đầu đếm bước chân", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cảm biến không khả dụng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setStepGoal() {
        try {
            stepGoal = Integer.parseInt(edtStepGoal.getText().toString());
            updateGoalStatus();
            Toast.makeText(this, "Cập nhật mục tiêu bước chân: " + stepGoal, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập một số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGoalStatus() {
        if (currentSteps >= stepGoal) {
            txtGoalStatus.setText("Trạng thái: Đã đạt mục tiêu!");
            txtGoalStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            txtGoalStatus.setText("Trạng thái: Chưa đạt mục tiêu");
            txtGoalStatus.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void resetCounter() {
        currentSteps = 0;
        txtCurrentSteps.setText("Bước chân hiện tại: 0");
        updateGoalStatus();
        Toast.makeText(this, "Đã đặt lại bộ đếm bước chân", Toast.LENGTH_SHORT).show();
    }

    private void saveStepLog() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        StepLog stepLog = stepLogDAO.getStepLogByDate(currentDate);
        if (stepLog == null) {
            stepLog = new StepLog(0, currentSteps, currentDate, stepGoal);
            stepLogDAO.addStepLog(stepLog);
        } else {
            stepLog.setSteps(currentSteps);

        }
        displayStepLogs(); // Cập nhật RecyclerView
    }

    private void displayStepLogs() {
        List<StepLog> stepLogs = stepLogDAO.getAllStepLogs();
        StepLogAdapter adapter = new StepLogAdapter(this, stepLogs);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isCounting) {
            currentSteps = (int) event.values[0];
            txtCurrentSteps.setText("Bước chân hiện tại: " + currentSteps);
            updateGoalStatus();
            saveStepLog(); // lưu lại thông tin bước chân
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền truy cập đã được cấp!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (isCounting) {
            sensorManager.unregisterListener(this, stepCounterSensor);
        }
        stepLogDAO.close(); // Đóng cơ sở dữ liệu
        super.onDestroy();
    }
    private void setupBottomNavigation() {
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(v -> startActivity(new Intent(StepTrackingActivity.this, MainActivity2.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(StepTrackingActivity.this, DisplayActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(StepTrackingActivity.this, activity_friends_list.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(StepTrackingActivity.this, trangcanhan.class)));
    }
}