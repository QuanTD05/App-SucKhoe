package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Yoga extends AppCompatActivity {
    private TextView txtFullName;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_yoga);
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
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserDetails(user.getUid());
        }
        // Home button
        ImageButton homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(v -> startActivity(new Intent(Yoga.this, MainActivity2.class)));

        // Calendar button
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(v -> startActivity(new Intent(Yoga.this, PostActivity.class)));

        // Notification button
        ImageButton notificationButton = findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(v -> startActivity(new Intent(Yoga.this, activity_friends_list.class)));

        // Profile button
        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v -> startActivity(new Intent(Yoga.this, trangcanhan.class)));

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