package fpoly.md19304.app_moblie;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import DTO.Post;

public class PostActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;

    private EditText editTextContent;
    private ImageView imageViewSelectedImage;
    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        editTextContent = findViewById(R.id.editTextPostContent);
        imageViewSelectedImage = findViewById(R.id.imageViewSelectedImage);
        Button submitButton = findViewById(R.id.buttonSubmitPost);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
                GuiThongBao();
            }
        });



        findViewById(R.id.buttonSelectImage).setOnClickListener(v -> openGallery());
        findViewById(R.id.buttonTakePhoto).setOnClickListener(v -> openCamera());
    }
    private void createPost() {
        String content = editTextContent.getText().toString().trim();
        String imageUrl = imageUri != null ? imageUri.toString() : "";

        FirebaseUser currentUser = auth.getCurrentUser();
        String username = currentUser != null ? getUsernameFromEmail(currentUser.getEmail()) : "Anonymous"; // Use email as username

        // Tạo một đối tượng Post mà không có ID
        Post post = new Post(null, "Title", content, imageUrl, new Timestamp(new java.util.Date()), username);

        if (imageUri != null) {
            // Tải hình ảnh lên Firebase Storage
            uploadImageToFirebaseStorage(imageUri, imageDownloadUrl -> {
                // Cập nhật URL của hình ảnh
                post.setImageUrl(imageDownloadUrl);
                // Lưu đối tượng Post vào Firestore và nhận ID
                savePostToFirestore(post);
            });
        } else {
            // Lưu đối tượng Post vào Firestore và nhận ID
            savePostToFirestore(post);
        }
    }

    private String getUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.split("@")[0]; // Lấy phần trước dấu "@"
        }
        return "Anonymous"; // Hoặc trả về giá trị mặc định khác nếu email không hợp lệ
    }


    private void savePostToFirestore(Post post) {
        // Lưu tài liệu vào Firestore mà không cung cấp ID
        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    // Nhận ID của tài liệu mới
                    String postId = documentReference.getId();
                    // Cập nhật đối tượng Post với ID
                    post.setId(postId);
                    // Cập nhật tài liệu trong Firestore với ID
                    db.collection("posts").document(postId).set(post)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(PostActivity.this, "Đã gửi bài đăng", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(PostActivity.this, "Failed to post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("PostActivity", "Error saving post", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostActivity.this, "Failed to post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("PostActivity", "Error saving post", e);
                });
    }


    private void uploadImageToFirebaseStorage(Uri fileUri, final OnSuccessListener<String> onSuccessListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> onSuccessListener.onSuccess(uri.toString()))
                        .addOnFailureListener(e -> Toast.makeText(PostActivity.this, "Không lấy được URL hình ảnh", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(PostActivity.this, "Không thể tải hình ảnh lên", Toast.LENGTH_SHORT).show());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                handleImageUri(imageUri); // Handle image from gallery
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                if (data != null && data.getExtras() != null) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    if (bitmap != null) {
                        Uri imageUri = getImageUri(bitmap);
                        handleImageUri(imageUri); // Handle image from camera
                    }
                }
            }
        }
    }

    private void handleImageUri(Uri imageUri) {
        // Display the image in ImageView
        imageViewSelectedImage.setImageURI(imageUri);
        this.imageUri = imageUri; // Update imageUri for use in createPost()
    }

    private Uri getImageUri(Bitmap bitmap) {
        // Create file name
        String filename = "photo_" + System.currentTimeMillis() + ".jpg";
        // Save image to storage
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            // Create file path and save image
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, filename, null);
            return Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    void GuiThongBao(){
        // khai bao Intent de chay  activity MSG khi người dùng bấm notifi
        Intent intentDemo = new Intent(getApplicationContext(), DisplayActivity.class);
        intentDemo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // GỬI dữ liệu vào Intent để chạy trong activity MSG nhận dữ liệu
        intentDemo.putExtra("dulieu","Nội dung gửi từ Notify Vào màn MSG");
        // tạo stack để chứa các activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(PostActivity.this);
        stackBuilder.addNextIntentWithParentStack(intentDemo);
        // lấy penddingIntent để gửi vaào notifi
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // khởi tạo bitmap để đọc viết trước dòng khởi tạo layout notifi
        Bitmap anh = BitmapFactory.decodeResource(getResources(),R.drawable.lo);
        // khởi tạo notifi
        Notification customNotifi = new NotificationCompat.Builder(PostActivity.this, NotifyConfig.CHANEL_ID)
                .setSmallIcon(R.drawable.ic_notification).setContentTitle("Bài viết")
                .setContentText("Bạn đã thêm một bài viết")
                .setContentIntent(pendingIntent)
                // thiết kế style

                .setLargeIcon(anh)
                .setColor(Color.RED)

                .build();

        // khởi tạo  trình quản lý notifi
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(PostActivity.this);
        //kiểm tra quyền gửi thông báo đã được cấp hay chưa
        if(ActivityCompat.checkSelfPermission(PostActivity.this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            //chưa được cấp quyền ==> hiện giao diện cấp quyền
            ActivityCompat.requestPermissions(PostActivity.this,
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
