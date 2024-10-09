package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.Post;
import fpoly.md19304.app_moblie.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    // Lưu trạng thái like của mỗi bài viết
    private Map<String, Boolean> likeStatusMap = new HashMap<>();

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.usernameTextView.setText(post.getUsername());
        holder.contentTextView.setText(post.getContent());

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getImageUrl())
                    .into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        boolean isLiked = likeStatusMap.getOrDefault(post.getId(), false);
        holder.likeButton.setImageDrawable(ContextCompat.getDrawable(context, isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline));

        holder.likeButton.setOnClickListener(v -> {
            boolean newLikeStatus = !isLiked;
            likeStatusMap.put(post.getId(), newLikeStatus);
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(context, newLikeStatus ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline));
            handleLikeButtonClick(post.getId(), newLikeStatus);
        });

        holder.commentButton.setOnClickListener(v -> {
            if (post.getId() != null) {
                showCommentDialog(post.getId());
            } else {
                Toast.makeText(context, "Invalid post ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        TextView contentTextView;
        ImageView postImageView;
        ImageButton likeButton;
        ImageButton commentButton;

        PostViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
        }
    }

    private void handleLikeButtonClick(String postId, boolean isLiked) {
        // Cập nhật trạng thái like trong cơ sở dữ liệu
        Map<String, Object> likeData = new HashMap<>();
        likeData.put("liked", isLiked);

        db.collection("posts").document(postId)
                .update("liked", isLiked)
                .addOnSuccessListener(aVoid -> {
                    // Thao tác thành công
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }

    // Trình bày việc xử lý bình luận trong một hộp thoại
    private void showCommentDialog(String postId) {
        if (postId == null || postId.isEmpty()) {
            // Xử lý trường hợp postId không hợp lệ
            Toast.makeText(context, "Invalid post ID", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_comment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView commenterEmailTextView = dialogView.findViewById(R.id.commenterEmailTextView);
        EditText commentEditText = dialogView.findViewById(R.id.commentEditText);
        Button submitCommentButton = dialogView.findViewById(R.id.submitCommentButton);
        RecyclerView commentsRecyclerView = dialogView.findViewById(R.id.commentsRecyclerView);

        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : "Anonymous";
        commenterEmailTextView.setText(email);

        // Cấu hình RecyclerView cho bình luận
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        final CommentAdapter commentAdapter = new CommentAdapter(context, new ArrayList<>());
        commentsRecyclerView.setAdapter(commentAdapter);

        // Tải bình luận từ Firestore
        db.collection("posts").document(postId).collection("comments")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> comments = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        comments.add(document.getData());
                    }
                    // Cập nhật CommentAdapter với dữ liệu mới
                    commentAdapter.setComments(comments);
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        submitCommentButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            if (!comment.isEmpty()) {
                saveComment(postId, email, comment);
                dialog.dismiss();
            }
        });
    }


    private void saveComment(String postId, String email, String comment) {
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("email", email);
        commentData.put("comment", comment);
        commentData.put("timestamp", new com.google.firebase.Timestamp(new java.util.Date()));

        db.collection("posts").document(postId).collection("comments").add(commentData)
                .addOnSuccessListener(documentReference -> {
                    // Thao tác thành công
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }

}
