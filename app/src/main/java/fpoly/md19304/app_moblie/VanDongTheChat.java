//package fpoly.md19304.app_moblie;
//
//import static android.content.ContentValues.TAG;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.text.InputType;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import Adapter.StepLogAdapter;
//import DTO.vanDongDTO;
//
//public class VanDongTheChat extends AppCompatActivity implements SensorEventListener {
//    private SensorManager sensorManager;
//    private Sensor stepCounterSensor;
//    private int initialStepCount = 0;
//    private boolean isInitialStepCountSet = false;
//    private int stepGoal = 0;
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//    private CollectionReference gratitudeRef;
//    private FirebaseUser currentUser;
//    private TextView userNameView, stepCountView, goalTextView, distanceView, caloriesView;
//    private ListView lvLog;
//    private StepLogAdapter adapter;
//    private List<vanDongDTO> stepLogs;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_van_dong_the_chat);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Initialize Firebase
//        FirebaseApp.initializeApp(this);
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//
//        if (currentUser == null) {
//            startActivity(new Intent(this, Login.class));
//            finish();
//            return;
//        }
//
//        // Initialize Views
//
//        stepCountView = findViewById(R.id.step_count_view);
//        goalTextView = findViewById(R.id.goal_text_view);
//        distanceView = findViewById(R.id.distance_view);
//        caloriesView = findViewById(R.id.calories_view);
//        lvLog = findViewById(R.id.lv_log);
//        Button btnDisplayLog = findViewById(R.id.btn_display_log);
//        Button btnStart = findViewById(R.id.btn_start);
//        Button btnPause = findViewById(R.id.btn_pause);
//
//        stepLogs = new ArrayList<>();
//        adapter = new StepLogAdapter(this, stepLogs);
//        lvLog.setAdapter(adapter);
//
//        // Initialize SensorManager and Sensor
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//        if (stepCounterSensor == null) {
//            Toast.makeText(this, "Không có cảm biến đếm bước!", Toast.LENGTH_SHORT).show();
//        }
//
//        btnDisplayLog.setOnClickListener(v -> loadStepLogs());
//        btnStart.setOnClickListener(v -> startTracking());
//        btnPause.setOnClickListener(v -> pauseTracking());
//
//        // Load data
//        gratitudeRef = db.collection("Steeps").document(currentUser.getUid()).collection("stepLogs");
//        loadDailyStepCount();
//
//
//        // Show goal dialog if no goal set
//        if (getSavedStepGoal() == 0) {
//            showGoalDialog();
//        } else {
//            goalTextView.setText("Mục tiêu: " + getSavedStepGoal() + " steps");
//        }
//
//        // Bottom navigation buttons
//        ImageButton homeButton = findViewById(R.id.home_button);
//        ImageButton calendarButton = findViewById(R.id.calendar_button);
//        ImageButton notificationButton = findViewById(R.id.notification_button);
//        ImageButton profileButton = findViewById(R.id.profile_button);
//
//        homeButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, MainActivity2.class)));
//        calendarButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, PostActivity.class)));
//        notificationButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, activity_friends_list.class)));
//        profileButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, trangcanhan.class)));
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
//            if (!isInitialStepCountSet) {
//                initialStepCount = (int) event.values[0];
//                isInitialStepCountSet = true;
//            }
//            int currentStepCount = (int) event.values[0] - initialStepCount;
//            updateStepCount(currentStepCount);
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (stepCounterSensor != null) {
//            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (stepCounterSensor != null) {
//            sensorManager.unregisterListener(this);
//        }
//    }
//
//    private void updateStepCount(int stepCount) {
//        stepCountView.setText("Số bước chân hiện tại: " + stepCount);
//
//        // Calculate distance and calories
//        double distance = stepCount * 0.000762; // Average step length is 0.762 meters
//        double calories = stepCount * 0.04; // Average calories burned per step
//
//        distanceView.setText(String.format("Khoảng cách: %.2f km", distance / 1000));
//        caloriesView.setText(String.format("Calo: %.2f kcal", calories));
//
//        if (currentUser != null) {
//            String timestamp = String.valueOf(System.currentTimeMillis());
//
//            Map<String, Object> stepData = new HashMap<>();
//            stepData.put("timestamp", timestamp);
//            stepData.put("steps", stepCount);
//
//            gratitudeRef.document(timestamp)
//                    .set(stepData)
//                    .addOnSuccessListener(aVoid -> {
//                        // Successfully added step count to Firestore
//                    })
//                    .addOnFailureListener(e -> {
//                        // Failed to add step count to Firestore
//                        Toast.makeText(VanDongTheChat.this, "Không lưu được số bước", Toast.LENGTH_SHORT).show();
//                    });
//        }
//    }
//
//    private void loadStepLogs() {
//        if (currentUser != null) {
//            gratitudeRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    stepLogs.clear();
//                    for (DocumentSnapshot document : task.getResult()) {
//                        String timestamp = document.getString("timestamp");
//                        int steps = document.getLong("steps").intValue();
//                        vanDongDTO log = new vanDongDTO(timestamp, steps);
//                        stepLogs.add(log);
//                    }
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(VanDongTheChat.this, "Không thể tải nhật ký bước", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    private void loadDailyStepCount() {
//        if (currentUser != null) {
//            gratitudeRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    int totalSteps = 0;
//                    long currentTime = System.currentTimeMillis();
//                    long oneDayMillis = 24 * 60 * 60 * 1000;
//
//                    for (DocumentSnapshot document : task.getResult()) {
//                        long timestamp = Long.parseLong(document.getString("timestamp"));
//                        if (currentTime - timestamp <= oneDayMillis) {
//                            totalSteps += document.getLong("steps").intValue();
//                        }
//                    }
//
//                    stepCountView.setText("Số Bước chân hôm nay: " + totalSteps);
//                    // Calculate distance and calories
//                    double distance = totalSteps * 0.000762;
//                    double calories = totalSteps * 0.04;
//
//                    distanceView.setText(String.format("Distance: %.2f km", distance / 1000));
//                    caloriesView.setText(String.format("Calories: %.2f kcal", calories));
//                } else {
//                    Toast.makeText(VanDongTheChat.this, "Failed to load daily step count", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//
//
//    private void showGoalDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Đặt mục tiêu bước");
//
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
//        builder.setView(input);
//
//        builder.setPositiveButton("Set", (dialog, which) -> {
//            String goal = input.getText().toString();
//            if (!goal.isEmpty()) {
//                stepGoal = Integer.parseInt(goal);
//                saveStepGoal(stepGoal);
//                goalTextView.setText("Mục tiêu: " + stepGoal + " steps");
//            }
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//
//        builder.show();
//    }
//
//    private void startTracking() {
//        // Your start tracking logic here
//        Toast.makeText(this, "Đã bắt đầu theo dõi", Toast.LENGTH_SHORT).show();
//    }
//
//    private void pauseTracking() {
//        // Your pause tracking logic here
//        Toast.makeText(this, "Đã tạm dừng theo dõi", Toast.LENGTH_SHORT).show();
//    }
//
//    private void saveStepGoal(int goal) {
//        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("step_goal", goal);
//        editor.apply();
//    }
//
//    private int getSavedStepGoal() {
//        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
//        return preferences.getInt("step_goal", 0);
//    }
//}
//package fpoly.md19304.app_moblie;
//
//import static android.content.ContentValues.TAG;
//
//import android.app.AlertDialog;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Notification;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.text.InputType;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import Adapter.StepLogAdapter;
//import DTO.vanDongDTO;
//
//public class VanDongTheChat extends AppCompatActivity implements SensorEventListener {
//    private SensorManager sensorManager;
//    private Sensor stepCounterSensor;
//    private int initialStepCount = 0;
//    private boolean isInitialStepCountSet = false;
//    private int stepGoal = 0;
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//    private CollectionReference gratitudeRef;
//    private FirebaseUser currentUser;
//    private TextView userNameView, stepCountView, goalTextView, distanceView, caloriesView;
//    private ListView lvLog;
//    private StepLogAdapter adapter;
//    private List<vanDongDTO> stepLogs;
//
//    private static final String CHANNEL_ID = "goal_notification_channel";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_van_dong_the_chat);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Initialize Firebase
//        FirebaseApp.initializeApp(this);
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//
//        if (currentUser == null) {
//            startActivity(new Intent(this, Login.class));
//            finish();
//            return;
//        }
//
//        // Initialize Views
//        stepCountView = findViewById(R.id.step_count_view);
//        goalTextView = findViewById(R.id.goal_text_view);
//        distanceView = findViewById(R.id.distance_view);
//        caloriesView = findViewById(R.id.calories_view);
//        lvLog = findViewById(R.id.lv_log);
//        Button btnDisplayLog = findViewById(R.id.btn_display_log);
//        Button btnStart = findViewById(R.id.btn_start);
//        Button btnPause = findViewById(R.id.btn_pause);
//
//        stepLogs = new ArrayList<>();
//        adapter = new StepLogAdapter(this, stepLogs);
//        lvLog.setAdapter(adapter);
//
//        // Initialize SensorManager and Sensor
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//        if (stepCounterSensor == null) {
//            Toast.makeText(this, "Không có cảm biến đếm bước!", Toast.LENGTH_SHORT).show();
//        }
//
//        btnDisplayLog.setOnClickListener(v -> loadStepLogs());
//        btnStart.setOnClickListener(v -> startTracking());
//        btnPause.setOnClickListener(v -> pauseTracking());
//
//        // Load data
//        gratitudeRef = db.collection("Steeps").document(currentUser.getUid()).collection("stepLogs");
//        loadDailyStepCount();
//
//        // Show goal dialog if no goal set
//        if (getSavedStepGoal() != 0) {
//            showGoalDialog();
//        } else {
//            goalTextView.setText("Mục tiêu: " + getSavedStepGoal() + " Bước");
//        }
//
//
//
////        // Create notification channel
////        sendGoalAchievedNotification();
//
//        // Bottom navigation buttons
//        ImageButton homeButton = findViewById(R.id.home_button);
//        ImageButton calendarButton = findViewById(R.id.calendar_button);
//        ImageButton notificationButton = findViewById(R.id.notification_button);
//        ImageButton profileButton = findViewById(R.id.profile_button);
//
//        homeButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, MainActivity2.class)));
//        calendarButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, PostActivity.class)));
//        notificationButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, activity_friends_list.class)));
//        profileButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, trangcanhan.class)));
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
//            if (!isInitialStepCountSet) {
//                initialStepCount = (int) event.values[0];
//                isInitialStepCountSet = true;
//            }
//            int currentStepCount = (int) event.values[0] - initialStepCount;
//            updateStepCount(currentStepCount);
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (stepCounterSensor != null) {
//            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (stepCounterSensor != null) {
//            sensorManager.unregisterListener(this);
//        }
//    }
//
//    private void updateStepCount(int stepCount) {
//        stepCountView.setText("Số bước chân hiện tại: " + stepCount);
//
//
//        // Calculate distance and calories
//        double distance = stepCount * 0.0008; // Average step length is 0.762 meters
//        double calories = stepCount * 0.05; // Average calories burned per step
//
//        distanceView.setText(String.format("Khoảng cách: %.2f km", distance));
//        caloriesView.setText(String.format("Calo: %.2f kcal", calories));
//
//        // Check if goal is achieved
//        if (stepCount >= stepGoal) {
//            sendGoalAchievedNotification();
//        }
//
//        if (currentUser != null) {
//            String timestamp = String.valueOf(System.currentTimeMillis());
//
//            Map<String, Object> stepData = new HashMap<>();
//            stepData.put("timestamp", timestamp);
//            stepData.put("steps", stepCount);
//
//            gratitudeRef.document(timestamp)
//                    .set(stepData)
//                    .addOnSuccessListener(aVoid -> {
//                        // Successfully added step count to Firestore
//                    })
//                    .addOnFailureListener(e -> {
//                        // Failed to add step count to Firestore
//                        Toast.makeText(VanDongTheChat.this, "Không lưu được số bước", Toast.LENGTH_SHORT).show();
//                    });
//        }
//    }
//
//    private void loadStepLogs() {
//        if (currentUser != null) {
//            gratitudeRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    stepLogs.clear();
//                    for (DocumentSnapshot document : task.getResult()) {
//                        String timestamp = document.getString("timestamp");
//                        int steps = document.getLong("steps").intValue();
//                        vanDongDTO log = new vanDongDTO(timestamp, steps);
//                        stepLogs.add(log);
//                    }
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(VanDongTheChat.this, "Không thể tải nhật ký bước", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    private void loadDailyStepCount() {
//        if (currentUser != null) {
//            gratitudeRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    int totalSteps = 0;
//                    long currentTime = System.currentTimeMillis();
//                    long oneDayMillis = 24 * 60 * 60 * 1000;
//
//                    for (DocumentSnapshot document : task.getResult()) {
//                        long timestamp = Long.parseLong(document.getString("timestamp"));
//                        if (currentTime - timestamp <= oneDayMillis) {
//                            totalSteps += document.getLong("steps").intValue();
//                        }
//                    }
//
//                    stepCountView.setText("Số Bước chân hôm nay: " + totalSteps);
//                    // Calculate distance and calories
//                    double distance = totalSteps * 0.0008;
//                    double calories = totalSteps * 0.05;
//
//                    distanceView.setText(String.format("Khoảng cách: %.2f km", distance));
//                    caloriesView.setText(String.format("Calo: %.2f kcal", calories));
//
//                    // Check if goal is achieved
//                    if (totalSteps >= stepGoal) {
//                        sendGoalAchievedNotification();
//                    }
//                } else {
//                    Toast.makeText(VanDongTheChat.this, "Failed to load daily step count", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    private void showGoalDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Đặt mục tiêu bước");
//
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
//        builder.setView(input);
//
//        builder.setPositiveButton("Set", (dialog, which) -> {
//            String goal = input.getText().toString();
//            if (!goal.isEmpty()) {
//                stepGoal = Integer.parseInt(goal);
//                saveStepGoal(stepGoal);
//                goalTextView.setText("Mục tiêu: " + stepGoal + " Bước");
//            }
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//
//        builder.show();
//    }
//
//    private void startTracking() {
//        // Your start tracking logic here
//        Toast.makeText(this, "Đã bắt đầu theo dõi", Toast.LENGTH_SHORT).show();
//    }
//
//    private void pauseTracking() {
//        // Your pause tracking logic here
//        Toast.makeText(this, "Đã tạm dừng theo dõi", Toast.LENGTH_SHORT).show();
//    }
//
//    private void saveStepGoal(int goal) {
//        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("step_goal", goal);
//        editor.apply();
//    }
//
//    private int getSavedStepGoal() {
//        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
//        return preferences.getInt("step_goal", 0);
//    }
//
//
//
//    private void sendGoalAchievedNotification() {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification) // Use your own icon here
//                .setContentTitle("Chúc mừng!")
//                .setContentText("Bạn đã đạt được mục tiêu bước chân của mình!")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(1, builder.build());
//    }
//}
package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.StepLogAdapter;
import DTO.vanDongDTO;

public class VanDongTheChat extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int initialStepCount = 0;
    private boolean isInitialStepCountSet = false;
    private int stepGoal = 0;
    private boolean isTracking = false; // Trạng thái theo dõi

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference gratitudeRef;
    private FirebaseUser currentUser;
    private TextView stepCountView, goalTextView, distanceView, caloriesView;
    private ListView lvLog;
    private StepLogAdapter adapter;
    private List<vanDongDTO> stepLogs;

    private static final String CHANNEL_ID = "goal_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_van_dong_the_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // Khởi tạo các View
        stepCountView = findViewById(R.id.step_count_view);
        goalTextView = findViewById(R.id.goal_text_view);
        distanceView = findViewById(R.id.distance_view);
        caloriesView = findViewById(R.id.calories_view);
        lvLog = findViewById(R.id.lv_log);
        Button btnDisplayLog = findViewById(R.id.btn_display_log);
        Button btnStart = findViewById(R.id.btn_start);
        Button btnPause = findViewById(R.id.btn_pause);

        stepLogs = new ArrayList<>();
        adapter = new StepLogAdapter(this, stepLogs);
        lvLog.setAdapter(adapter);

        // Khởi tạo SensorManager và Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounterSensor == null) {
            Toast.makeText(this, "Không có cảm biến đếm bước!", Toast.LENGTH_SHORT).show();
        }

        btnDisplayLog.setOnClickListener(v -> loadStepLogs());
        btnStart.setOnClickListener(v -> startTracking());
        btnPause.setOnClickListener(v -> pauseTracking());

        // Tải dữ liệu
        gratitudeRef = db.collection("Steeps").document(currentUser.getUid()).collection("stepLogs");
        loadDailyStepCount();

        // Hiển thị hộp thoại mục tiêu nếu chưa đặt mục tiêu
        if (getSavedStepGoal() == 0) {
            showGoalDialog();
        } else {
            goalTextView.setText("Mục tiêu: " + getSavedStepGoal() + " Bước");
        }

        // Tạo kênh thông báo
        createNotificationChannel();

        // Các nút điều hướng dưới cùng
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, MainActivity2.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, PostActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, activity_friends_list.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(VanDongTheChat.this, trangcanhan.class)));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (!isInitialStepCountSet) {
                initialStepCount = (int) event.values[0];
                isInitialStepCountSet = true;
            }
            int currentStepCount = (int) event.values[0] - initialStepCount;
            updateStepCount(currentStepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTracking && stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void updateStepCount(int stepCount) {
        stepCountView.setText("Số bước chân hiện tại: " + stepCount);

        // Tính khoảng cách và calo
        double distance = stepCount * 0.0008; // Chiều dài bước trung bình là 0.762 mét
        double calories = stepCount * 0.05; // Lượng calo tiêu thụ trung bình mỗi bước

        distanceView.setText(String.format("Khoảng cách: %.2f km", distance));
        caloriesView.setText(String.format("Calo: %.2f kcal", calories));

        // Kiểm tra nếu đạt mục tiêu
        if (stepCount >= stepGoal) {
            sendGoalAchievedNotification();
        }

        if (currentUser != null) {
            String timestamp = String.valueOf(System.currentTimeMillis());

            Map<String, Object> stepData = new HashMap<>();
            stepData.put("timestamp", timestamp);
            stepData.put("steps", stepCount);

            gratitudeRef.document(timestamp)
                    .set(stepData)
                    .addOnSuccessListener(aVoid -> {
                        // Thêm thành công số bước vào Firestore
                    })
                    .addOnFailureListener(e -> {
                        // Thêm thất bại
                        Toast.makeText(VanDongTheChat.this, "Không lưu được số bước", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadStepLogs() {
        if (currentUser != null) {
            gratitudeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    stepLogs.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        String timestamp = document.getString("timestamp");
                        int steps = document.getLong("steps").intValue();
                        vanDongDTO log = new vanDongDTO(timestamp, steps);
                        stepLogs.add(log);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VanDongTheChat.this, "Không thể tải nhật ký bước", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadDailyStepCount() {
        if (currentUser != null) {
            gratitudeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int totalSteps = 0;
                    long currentTime = System.currentTimeMillis();
                    long oneDayMillis = 24 * 60 * 60 * 1000;

                    for (DocumentSnapshot document : task.getResult()) {
                        String timestampStr = document.getString("timestamp");
                        long timestamp = Long.parseLong(timestampStr);

                        if (currentTime - timestamp < oneDayMillis) {
                            totalSteps += document.getLong("steps").intValue();
                        }
                    }
                    updateStepCount(totalSteps);
                } else {
                    Toast.makeText(VanDongTheChat.this, "Không thể tải số bước hàng ngày", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startTracking() {
        isTracking = true;
        isInitialStepCountSet = false;
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
        Toast.makeText(this, "Bắt đầu theo dõi bước chân", Toast.LENGTH_SHORT).show();
    }

    private void pauseTracking() {
        isTracking = false;
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this);
        }
        Toast.makeText(this, "Dừng theo dõi bước chân", Toast.LENGTH_SHORT).show();
    }

    private void sendGoalAchievedNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Chúc mừng!")
                .setContentText("Bạn đã đạt mục tiêu bước chân hôm nay!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void showGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mục tiêu bước chân");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            stepGoal = Integer.parseInt(input.getText().toString());
            saveStepGoal(stepGoal);
            goalTextView.setText("Mục tiêu: " + stepGoal + " Bước");
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveStepGoal(int stepGoal) {
        SharedPreferences sharedPreferences = getSharedPreferences("StepGoalPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepGoal", stepGoal);
        editor.apply();
    }

    private int getSavedStepGoal() {
        SharedPreferences sharedPreferences = getSharedPreferences("StepGoalPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("stepGoal", 0);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Goal Notification Channel";
            String description = "Channel for step goal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

