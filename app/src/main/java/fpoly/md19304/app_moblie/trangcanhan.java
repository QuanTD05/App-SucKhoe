package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class trangcanhan extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView txtEmail, txtFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangcanhan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtEmail = findViewById(R.id.profile_phone); // Changed to match the TextView IDs in your layout
        txtFullName = findViewById(R.id.profile_name);

        LinearLayout menuItemVandong = findViewById(R.id.menu_item_vandong);
        LinearLayout menuItemTinhthan = findViewById(R.id.menu_item_tinhthan);
        LinearLayout menuItemNgunghi = findViewById(R.id.menu_item_ngunghi);
        LinearLayout menuItemAnuong = findViewById(R.id.menu_item_anuong);
        LinearLayout menuItemBaitap = findViewById(R.id.menu_item_baitap);
        LinearLayout menuItemTamly = findViewById(R.id.menu_item_tamly);
        LinearLayout signoutLayout = findViewById(R.id.signout);

        // Set onClickListeners for each menu item
        menuItemVandong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(trangcanhan.this, VanDongTheChat.class));
            }
        });

        menuItemTinhthan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(trangcanhan.this, long_biet_on.class));
            }
        });

        menuItemNgunghi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(trangcanhan.this, SleepSummary.class));
            }
        });

        menuItemAnuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Ăn uống click
                startActivity(new Intent(trangcanhan.this, an_uong.class));
            }
        });

        menuItemBaitap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Bài tập nâng cao sức khỏe click
                startActivity(new Intent(trangcanhan.this, Suc_khoe_tinh_than.class));
            }
        });

        menuItemTamly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Tâm Lý click
                startActivity(new Intent(trangcanhan.this, FaqActivity.class));
            }
        });

        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(trangcanhan.this, MainActivity2.class));
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle calendar button click
                startActivity(new Intent(trangcanhan.this, PostActivity.class));
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle notification button click
                startActivity(new Intent(trangcanhan.this, activity_friends_list.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle profile button click (đăng xuất)
                mAuth.signOut();
                startActivity(new Intent(trangcanhan.this, trangcanhan.class));
                finish();
            }
        });

        signoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện đăng xuất
                mAuth.signOut();

                // Chuyển hướng về màn hình đăng nhập
                Intent intent = new Intent(trangcanhan.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserDetails(user.getUid());
        }
    }

    private void loadUserDetails(String userId) {
        DocumentReference docRef = db.collection("taikhoan").document(userId);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String email = documentSnapshot.getString("email");
                            String name = documentSnapshot.getString("name");

                            txtEmail.setText(email);
                            txtFullName.setText(name);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "get failed with ", e);
                    }
                });
    }
}
