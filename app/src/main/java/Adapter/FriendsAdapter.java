package Adapter;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpoly.md19304.app_moblie.NotifyConfig;
import fpoly.md19304.app_moblie.R;
import fpoly.md19304.app_moblie.long_biet_on;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
    private List<DocumentSnapshot> friends;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void setFriends(List<DocumentSnapshot> friends) {
        this.friends = friends;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        DocumentSnapshot friend = friends.get(position);
        String friendId = friend.getId(); // ID của người bạn
        String name = friend.getString("name");
        holder.nameTextView.setText(name);

        // Kiểm tra trạng thái yêu cầu kết bạn
        checkRequestStatus(friendId, holder.sendRequestButton);

        holder.sendRequestButton.setOnClickListener(v -> {
            sendFriendRequest(friendId);
        });
    }

    @Override
    public int getItemCount() {
        return friends == null ? 0 : friends.size();
    }

    private void sendFriendRequest(String friendId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tạo yêu cầu kết bạn
        Map<String, Object> request = new HashMap<>();
        request.put("from", currentUserId);
        request.put("to", friendId);
        request.put("status", "pending");

        db.collection("friend_requests").add(request)
                .addOnSuccessListener(documentReference -> {
                    // Yêu cầu kết bạn đã được gửi thành công
                    // Cập nhật giao diện hoặc thông báo cho người dùng
                    notifyDataSetChanged(); // Cập nhật UI nếu cần
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    e.printStackTrace();
                });
    }

    private void checkRequestStatus(String friendId, Button sendRequestButton) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("friend_requests")
                .whereEqualTo("from", currentUserId)
                .whereEqualTo("to", friendId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Xử lý lỗi
                        e.printStackTrace();
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot request = queryDocumentSnapshots.getDocuments().get(0);
                        String status = request.getString("status");
                        updateButtonStatus(status, sendRequestButton);
                    } else {
                        // Chưa gửi yêu cầu
                        sendRequestButton.setText("Gửi yêu cầu");
                        sendRequestButton.setEnabled(true);
                    }
                });
    }

    private void updateButtonStatus(String status, Button sendRequestButton) {
        switch (status) {
            case "accepted":
                sendRequestButton.setText("Bạn bè");
                sendRequestButton.setEnabled(false);
                break;
            case "pending":
                sendRequestButton.setText("Đã gửi yêu cầu");
                sendRequestButton.setEnabled(false);
                break;
            default:
                sendRequestButton.setText("Gửi yêu cầu");
                sendRequestButton.setEnabled(true);
                break;
        }
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        Button sendRequestButton;

        FriendViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friend_name);
            sendRequestButton = itemView.findViewById(R.id.btn_send_request);
        }
    }

}
