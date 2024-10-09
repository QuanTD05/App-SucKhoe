package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
public class an_uong extends AppCompatActivity {
    private TextView userNameView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private TextView tvGender;
    private String selectedGender;

    private TextView etHeight;
    private TextView etWeight;
    private TextView tvResult;
    private Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_an_uong);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvGender = findViewById(R.id.etGender);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);
        userNameView = findViewById(R.id.name_text);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Chuyển hướng về màn hình đăng nhập nếu chưa đăng nhập
            startActivity(new Intent(an_uong.this, Login.class));
            finish();
            return;
        }

        // Get the current user and display their name
        if (currentUser != null) {
            loadUserDetails(currentUser.getUid());
            loadUserBMI(currentUser.getUid());
        }

        tvGender.setOnClickListener(v -> showGenderDialog());

        btnCalculate.setOnClickListener(v -> {
            String heightStr = etHeight.getText().toString();
            String weightStr = etWeight.getText().toString();

            Double height = null;
            Double weight = null;

            try {
                height = Double.parseDouble(heightStr);
                weight = Double.parseDouble(weightStr);
            } catch (NumberFormatException e) {
                tvResult.setText("Vui lòng nhập đúng chiều cao và cân nặng!");
                return;
            }

            if (height != null && weight != null) {
                double bmi = calculateBMI(height, weight);
                String result = getBMIResult(bmi);
                tvResult.setText(result);
                saveUserBMI(currentUser.getUid(), height, weight);
            } else {
                tvResult.setText("Vui lòng nhập đúng chiều cao và cân nặng!");
            }
        });

        // Bottom navigation buttons
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(v -> startActivity(new Intent(an_uong.this, MainActivity2.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(an_uong.this, PostActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(an_uong.this, activity_friends_list.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(an_uong.this, trangcanhan.class)));
    }

    private void showGenderDialog() {
        final String[] genders = {"Nam", "Nữ", "Khác"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Giới Tính")
                .setItems(genders, (dialog, which) -> {
                    selectedGender = genders[which];
                    tvGender.setText(selectedGender);
                });
        builder.create().show();
    }

    private void loadUserDetails(String userId) {
        DocumentReference docRef = db.collection("taikhoan").document(userId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        userNameView.setText(name);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
    }

    private void loadUserBMI(String userId) {
        DocumentReference docRef = db.collection("taikhoan").document(userId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double height = documentSnapshot.getDouble("height");
                        Double weight = documentSnapshot.getDouble("weight");

                        if (height != null && weight != null) {
                            etHeight.setText(String.valueOf(height));
                            etWeight.setText(String.valueOf(weight));
                            double bmi = calculateBMI(height, weight);
                            String result = getBMIResult(bmi);
                            tvResult.setText(result);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
    }

    private void saveUserBMI(String userId, Double height, Double weight) {
        DocumentReference docRef = db.collection("taikhoan").document(userId);
        docRef.update("height", height, "weight", weight)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    private double calculateBMI(double height, double weight) {
        return weight / ((height / 100) * (height / 100));
    }

    private String getBMIResult(double bmi) {
        StringBuilder result = new StringBuilder(String.format("BMI: %.2f", bmi)).append("\n");

        if (bmi < 16) {
            result.append("Gầy độ III\nKhuyến cáo: Bạn nên bổ sung thực phẩm giàu đạm và năng lượng.");
        } else if (bmi < 17) {
            result.append("Gầy độ II\nKhuyến cáo: Hãy tăng cường các bữa ăn với nhiều protein và năng lượng.");
        } else if (bmi < 18.5) {
            result.append("Gầy độ I\nKhuyến cáo: Nên ăn nhiều bữa nhỏ với thực phẩm giàu dinh dưỡng.");
        } else if (bmi < 25) {
            result.append("Bình thường\nKhuyến cáo: Duy trì chế độ ăn uống cân đối và lối sống lành mạnh.");
        } else if (bmi < 30) {
            result.append("Thừa cân\nKhuyến cáo: Giảm calo, ăn nhiều rau xanh và trái cây, kết hợp tập thể dục.");
        } else if (bmi < 35) {
            result.append("Béo phì độ I\nKhuyến cáo: Hạn chế đồ ăn nhanh, tăng cường hoạt động thể chất.");
        } else if (bmi < 40) {
            result.append("Béo phì độ II\nKhuyến cáo: Tham khảo ý kiến chuyên gia dinh dưỡng và tăng cường tập luyện.");
        } else {
            result.append("Béo phì độ III\nKhuyến cáo: Cần có chế độ giảm cân nghiêm ngặt dưới sự giám sát của bác sĩ.");
        }

        return result.toString();
    }
}