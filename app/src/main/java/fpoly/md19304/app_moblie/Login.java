package fpoly.md19304.app_moblie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtUser;
    private EditText edtPass;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView txtForgot = findViewById(R.id.txtForgot);
        TextView txtSignUp = findViewById(R.id.txtSignUp);

        btnLogin.setOnClickListener(v -> login());

        txtSignUp.setOnClickListener(v -> startActivity(new Intent(Login.this, register.class)));

        txtForgot.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void login() {
        String email = edtUser.getText().toString();
        String pass = edtPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user);
                        }
                    } else {
                        Toast.makeText(Login.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quên mật khẩu");
        builder.setMessage("Nhập email để đặt lại mật khẩu");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Không thể gửi email đặt lại mật khẩu.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(Login.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void checkUserRole(FirebaseUser user) {
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("taikhoan")
                    .document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String role = document.getString("role");
                                Intent intent;
                                if ("Chuyên gia".equals(role)) {
                                    intent = new Intent(Login.this, MainActivity2.class);
                                } else {
                                    intent = new Intent(Login.this, MainActivity2.class); // Adjust according to your user activity
                                }
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
        }
    }
}