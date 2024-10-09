package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpoly.md19304.app_moblie.R;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.RequestViewHolder> {
    private List<DocumentSnapshot> requests;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnRequestActionListener listener;

    public FriendRequestsAdapter(OnRequestActionListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<DocumentSnapshot> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        DocumentSnapshot request = requests.get(position);
        String requesterId = request.getString("from");

        // Fetch the requester's name to display
        db.collection("taikhoan").document(requesterId).get().addOnSuccessListener(documentSnapshot -> {
            String name = documentSnapshot.getString("name");
            holder.requesterNameTextView.setText(name);
        });

        holder.acceptButton.setOnClickListener(v -> {
            acceptRequest(request.getId(), requesterId);
        });

        holder.rejectButton.setOnClickListener(v -> {
            rejectRequest(request.getId());
        });
    }

    @Override
    public int getItemCount() {
        return requests == null ? 0 : requests.size();
    }

    private void acceptRequest(String requestId, String requesterId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Update request status to 'accepted'
        db.collection("friend_requests").document(requestId)
                .update("status", "accepted")
                .addOnSuccessListener(aVoid -> {
                    // Add both users to each other's friends list
                    Map<String, Object> friendData = new HashMap<>();
                    friendData.put("friendId", requesterId);
                    db.collection("taikhoan").document(currentUserId)
                            .collection("friends").document(requesterId).set(friendData);

                    Map<String, Object> currentUserData = new HashMap<>();
                    currentUserData.put("friendId", currentUserId);
                    db.collection("taikhoan").document(requesterId)
                            .collection("friends").document(currentUserId).set(currentUserData);

                    if (listener != null) {
                        listener.onRequestChanged();
                    }
                });
    }

    private void rejectRequest(String requestId) {
        // Delete the friend request
        db.collection("friend_requests").document(requestId).delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onRequestChanged();
                    }
                });
    }

    public interface OnRequestActionListener {
        void onRequestChanged();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView requesterNameTextView;
        Button acceptButton, rejectButton;

        RequestViewHolder(View itemView) {
            super(itemView);
            requesterNameTextView = itemView.findViewById(R.id.requester_name);
            acceptButton = itemView.findViewById(R.id.btn_accept);
            rejectButton = itemView.findViewById(R.id.btn_reject);
        }
    }
}
