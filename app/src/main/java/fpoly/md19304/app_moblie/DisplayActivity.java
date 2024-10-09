package fpoly.md19304.app_moblie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import Adapter.PostAdapter;
import DTO.Post;

public class DisplayActivity extends AppCompatActivity {

    private static final int POST_ACTIVITY_REQUEST_CODE = 1;

    private RecyclerView postsRecyclerView;
    private FirebaseFirestore db;
    private ArrayList<Post> postsList;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView dangbai = findViewById(R.id.dangbai);
        dangbai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, PostActivity.class);
                startActivityForResult(intent, POST_ACTIVITY_REQUEST_CODE);
            }
        });

        db = FirebaseFirestore.getInstance();
        postsList = new ArrayList<>();
        adapter = new PostAdapter(this, postsList);
        postsRecyclerView.setAdapter(adapter);

        fetchPosts();
    }

    private void fetchPosts() {
        db.collection("posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            postsList.add(post);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DisplayActivity.this, "Lỗi tìm đăng bài viết", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == POST_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the posts
            fetchPosts();
        }
    }
}
