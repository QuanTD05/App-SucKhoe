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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapter.mentalAdapter;
import DTO.longBietOnDTO;

public class long_biet_on extends AppCompatActivity {

    private ListView listView;
    private ArrayList<longBietOnDTO> gratitudeList;
    private mentalAdapter adapter;

    // Firebase Firestore và Auth
    private FirebaseFirestore db;
    private CollectionReference gratitudeRef;
    private TextView txtFullName;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_biet_on);

        // Setup window insets to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        txtFullName = findViewById(R.id.name_text);

        if (currentUser != null) {
            gratitudeRef = db.collection("gratitude").document(currentUser.getUid()).collection("entries");
        } else {
            // Redirect to login screen if not authenticated
            finish();
            return;
        }

        listView = findViewById(R.id.listView);
        gratitudeList = new ArrayList<>();
        adapter = new mentalAdapter(this, gratitudeList, this::onItemEdit);
        listView.setAdapter(adapter);

        // Setup "+" ImageView click event
        ImageView plusImage = findViewById(R.id.plus_image);
        plusImage.setOnClickListener(v -> showAddGratitudeDialog());

        // Load data from Firestore
        loadGratitudeData();

        // Load user details
        if (currentUser != null) {
            loadUserDetails(currentUser.getUid());
        }

        // Bottom navigation buttons
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(v -> startActivity(new Intent(long_biet_on.this, MainActivity2.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(long_biet_on.this, PostActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(long_biet_on.this, activity_friends_list.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(long_biet_on.this, trangcanhan.class)));
    }

    private void showAddGratitudeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ghi lời cảm ơn");
        builder.setMessage("Nhập lời cảm ơn của bạn:");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Lưu lại", (dialog, which) -> {
            String gratitude = input.getText().toString().trim();
            if (!gratitude.isEmpty()) {
                // Create DTO object with the current date and the entered gratitude
                longBietOnDTO dto = new longBietOnDTO();
                dto.setNgay(new Date()); // Use current date
                dto.setDieuBietOn(gratitude);

                // Add to list and update adapter
                gratitudeList.add(dto);
                adapter.notifyDataSetChanged();

                // Save data to Firestore
                saveGratitudeToFirestore(dto);
            }
            GuiThongBao();
        });

        builder.setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveGratitudeToFirestore(longBietOnDTO dto) {
        Map<String, Object> gratitude = new HashMap<>();
        gratitude.put("date", dto.getNgay());
        gratitude.put("dieubieton", dto.getDieuBietOn());

        gratitudeRef.add(gratitude)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId())
                )
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error adding document", e)
                );
    }

    private void loadGratitudeData() {
        gratitudeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                gratitudeList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    longBietOnDTO dto = new longBietOnDTO();
                    dto.setNgay(document.getDate("date"));
                    dto.setDieuBietOn(document.getString("dieubieton"));
                    gratitudeList.add(dto);
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Error getting documents", task.getException());
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
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to load user details", e)
                );
    }

    private void onItemEdit(int position) {
        // Handle the edit button click event
        longBietOnDTO itemToEdit = gratitudeList.get(position);

        // Create a dialog for editing the item
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa lời cảm ơn");
        builder.setMessage("Chỉnh sửa lời cảm ơn:");

        final EditText input = new EditText(this);
        input.setText(itemToEdit.getDieuBietOn());
        builder.setView(input);

        builder.setPositiveButton("Lưu lại", (dialog, which) -> {
            String updatedGratitude = input.getText().toString().trim();
            if (!updatedGratitude.isEmpty()) {
                itemToEdit.setDieuBietOn(updatedGratitude);

                // Update the list and notify adapter
                adapter.notifyDataSetChanged();

                // Update Firestore
                updateGratitudeInFirestore(itemToEdit);
            }
        });

        builder.setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateGratitudeInFirestore(longBietOnDTO dto) {
        // Find the document to update
        // This is a placeholder implementation; you need to track document IDs for updating
        // Update Firestore with the new gratitude details
        Map<String, Object> updatedGratitude = new HashMap<>();
        updatedGratitude.put("date", dto.getNgay());
        updatedGratitude.put("dieubieton", dto.getDieuBietOn());

        // Example: You need to store and use document ID from Firestore for updating
        // String documentId = ...;
        // gratitudeRef.document(documentId).update(updatedGratitude)
        // .addOnSuccessListener(aVoid ->
        //        Log.d(TAG, "DocumentSnapshot successfully updated!")
        // )
        // .addOnFailureListener(e ->
        //        Log.e(TAG, "Error updating document", e)
        // );
    }
    void GuiThongBao(){
        // khai bao Intent de chay  activity MSG khi người dùng bấm notifi
        Intent intentDemo = new Intent(getApplicationContext(), long_biet_on.class);
        intentDemo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // GỬI dữ liệu vào Intent để chạy trong activity MSG nhận dữ liệu
        intentDemo.putExtra("dulieu","Nội dung gửi từ Notify Vào màn MSG");
        // tạo stack để chứa các activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(long_biet_on.this);
        stackBuilder.addNextIntentWithParentStack(intentDemo);
        // lấy penddingIntent để gửi vaào notifi
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // khởi tạo bitmap để đọc viết trước dòng khởi tạo layout notifi
        Bitmap anh = BitmapFactory.decodeResource(getResources(),R.drawable.lo);
        // khởi tạo notifi
        Notification customNotifi = new NotificationCompat.Builder(long_biet_on.this, NotifyConfig.CHANEL_ID)
                .setSmallIcon(R.drawable.ic_notification).setContentTitle("Lòng biết ơn")
                .setContentText("Bạn đã thêm một điều biết ơn")
                .setContentIntent(pendingIntent)
                // thiết kế style
                .setLargeIcon(anh)
                .setColor(Color.RED)

                .build();

        // khởi tạo  trình quản lý notifi
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(long_biet_on.this);
        //kiểm tra quyền gửi thông báo đã được cấp hay chưa
        if(ActivityCompat.checkSelfPermission(long_biet_on.this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            //chưa được cấp quyền ==> hiện giao diện cấp quyền
            ActivityCompat.requestPermissions(long_biet_on.this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 999);
            // thoat khoi hàm
            return;
        }else {
            // đã cấp quyền
            int id_notifi = (int) new Date().getTime(); // tạo chổi sô tránh trung
            notificationManagerCompat.notify(id_notifi, customNotifi);
        }
    }
}
