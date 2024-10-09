package fpoly.md19304.app_moblie;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import Adapter.FriendRequestsAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

public class activity_friend_request_response extends AppCompatActivity implements FriendRequestsAdapter.OnRequestActionListener {
    private RecyclerView recyclerView;
    private FriendRequestsAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request_response);

        recyclerView = findViewById(R.id.requests_recycler_view);
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendRequestsAdapter(this);
        recyclerView.setAdapter(adapter);

        loadFriendRequests();
    }

    private void loadFriendRequests() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("friend_requests")
                .whereEqualTo("to", currentUserId)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Handle error
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<DocumentSnapshot> requests = queryDocumentSnapshots.getDocuments();
                        adapter.setRequests(requests);
                    }
                });
    }

    @Override
    public void onRequestChanged() {
        loadFriendRequests(); // Reload friend requests after a request is accepted or rejected
        setResult(RESULT_OK); // Set result to notify activity_friends_list to refresh
    }
}
