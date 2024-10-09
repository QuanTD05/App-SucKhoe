package fpoly.md19304.app_moblie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText edtRePass;
    private EditText edtPass;
    private EditText edtUser;
    private EditText edtFullName;
    private Spinner spinnerRole;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize EditText fields
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        edtRePass = findViewById(R.id.edtRepass);
        edtFullName = findViewById(R.id.edtFullName);
        spinnerRole = findViewById(R.id.spinnerRole);

        // Initialize Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Initialize Buttons
        Button btnRegister = findViewById(R.id.btnre);
        Button btnGoBack = findViewById(R.id.btnGoBack);

        // Handle Register button click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        // Handle Go Back button click
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this, Login.class));
                finish(); // Optional: finish() this activity to prevent user from returning to it with back button
            }
        });
    }

    private void Register() {
        final String email = edtUser.getText().toString().trim();
        final String pass = edtPass.getText().toString().trim();
        String repass = edtRePass.getText().toString().trim();
        final String name = edtFullName.getText().toString().trim();
        final String role = spinnerRole.getSelectedItem().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(repass)) {
            Toast.makeText(this, "Vui lòng nhập lại mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(repass)) {
            Toast.makeText(this, "Mật khẩu và nhập lại mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập Họ và tên!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Dismiss progress dialog
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                updateFirestore(user, name, role);
                                updateUI(user);
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateFirestore(FirebaseUser user, String name, String role) {
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> userObj = new HashMap<>();
            userObj.put("email", user.getEmail());
            userObj.put("name", name);
            userObj.put("role", role);

            db.collection("taikhoan")
                    .document(user.getUid())
                    .set(userObj)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Navigate to another activity or perform further actions after successful registration
            Intent intent = new Intent(register.this, Login.class);
            startActivity(intent);
            finish(); // Optional: finish() this activity to prevent user from returning to it with back button
        }
    }
//    private void updateUI(FirebaseUser user, String role) {
//        if (user != null) {
//            Intent intent;
//            if (role.equals("Chuyên gia")) {
//                intent = new Intent(register.this, ExpertActivity.class);
//            } else {
//                intent = new Intent(register.this, UserActivity.class);
//            }
//            startActivity(intent);
//            finish();
//        }
//    }

}
