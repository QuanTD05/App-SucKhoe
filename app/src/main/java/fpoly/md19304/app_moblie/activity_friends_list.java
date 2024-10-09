package fpoly.md19304.app_moblie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

import Adapter.FriendsAdapter;

public class activity_friends_list extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private FriendsAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration friendsListener;

    private static final int REQUEST_CODE_FRIEND_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        ImageView add = findViewById(R.id.loimoi);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(activity_friends_list.this, activity_friend_request_response.class);
            startActivityForResult(intent, REQUEST_CODE_FRIEND_REQUEST);
        });

        recyclerView = findViewById(R.id.friends_recycler_view);
        searchView = findViewById(R.id.searchEditText);
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendsAdapter();
        recyclerView.setAdapter(adapter);

        loadFriends("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadFriends(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadFriends(newText);
                return true;
            }
        });
    }

    private void loadFriends(String query) {
        if (friendsListener != null) {
            friendsListener.remove();
        }

        Query friendsQuery = db.collection("taikhoan")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff");

        friendsListener = friendsQuery.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                e.printStackTrace();
                return;
            }

            if (snapshots != null) {
                List<DocumentSnapshot> documents = snapshots.getDocuments();
                adapter.setFriends(documents);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FRIEND_REQUEST && resultCode == RESULT_OK) {
            refreshFriendsList(); // Reload friends list after handling friend request
        }
    }

    public void refreshFriendsList() {
        loadFriends(""); // Call loadFriends to refresh the list
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (friendsListener != null) {
            friendsListener.remove();
        }
    }
}
