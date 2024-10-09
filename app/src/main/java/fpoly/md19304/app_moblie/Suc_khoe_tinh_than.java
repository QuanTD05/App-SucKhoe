package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Suc_khoe_tinh_than extends AppCompatActivity {
    private TextView txtFullName;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suc_khoe_tinh_than);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        txtFullName = findViewById(R.id.name_text);

        if (currentUser != null) {
            loadUserDetails(currentUser.getUid());
        }

        LinearLayout Yoga12 = findViewById(R.id.Yoga12);
        LinearLayout thien1 = findViewById(R.id.video_item_1);
        LinearLayout thien2 = findViewById(R.id.video_item_2);

        Yoga12.setOnClickListener(v -> startActivity(new Intent(Suc_khoe_tinh_than.this, Yoga.class)));

        thien1.setOnClickListener(v -> startVideoActivity("video1", "Video thiền 5 phút"));
        thien2.setOnClickListener(v -> startVideoActivity("video2", "Video thiền 10 phút"));

        ImageButton homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(v -> startActivity(new Intent(Suc_khoe_tinh_than.this, MainActivity2.class)));

        ImageButton calendarButton = findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(v -> startActivity(new Intent(Suc_khoe_tinh_than.this, DisplayActivity.class)));

        ImageButton notificationButton = findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(v -> startActivity(new Intent(Suc_khoe_tinh_than.this, activity_friends_list.class)));

        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v -> startActivity(new Intent(Suc_khoe_tinh_than.this, trangcanhan.class)));
    }

    private void startVideoActivity(String videoType, String videoTitle) {
        Intent intent = new Intent(Suc_khoe_tinh_than.this, VideoActivity.class);
        intent.putExtra("VIDEO_TYPE", videoType);
        intent.putExtra("VIDEO_TITLE", videoTitle);

        String videoUrl = getVideoUrl(videoType);
        intent.putExtra("VIDEO_URL", videoUrl);

        startActivity(intent);
    }

    private String getVideoUrl(String videoType) {
        switch (videoType) {
            case "video1":
                return "https://firebasestorage.googleapis.com/v0/b/appmobile-c561a.appspot.com/o/video%2F5658681867492.mp4?alt=media&token=4d0c06d5-a496-44f1-91e0-e08c594f6c0b";
            case "video2":
                return "https://firebasestorage.googleapis.com/v0/b/appmobile-c561a.appspot.com/o/video%2F5661618910604.mp4?alt=media&token=2c00ead9-c082-483a-939d-b60b1400b497";
            default:
                return "";
        }
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
}
