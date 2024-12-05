package in.Welove.Profile;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.Welove.Adapter.UserAdapter2;
import in.Welove.BaseActivity;
import in.Welove.Model.User;
import in.Welove.R;

public class LikedActivity extends BaseActivity {
    private RecyclerView recyclerViewliked;
    private UserAdapter2 userAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView likedimage;
    private TextView likedtext;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        // RecyclerView setup
        likedimage = findViewById(R.id.likedimage);
        likedtext = findViewById(R.id.likedtext);
        recyclerViewliked = findViewById(R.id.likedUsers);
        recyclerViewliked.setHasFixedSize(true);
        recyclerViewliked.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewliked.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int spacing = 8;
                int pacing=-25;// Adjust this value to set the desired spacing
                int position = parent.getChildAdapterPosition(view); // Get the item's position
                int spanCount = 2; // The number of columns in your grid

                // Apply spacing to all items
                outRect.right = pacing;
                outRect.bottom = spacing;

                // Apply left spacing only if it's not the second item in a row
                if (position % spanCount == 0) {
                    // First item in the row
                    outRect.left = spacing;
                } else {
                    // Second item in the row
                    outRect.left = pacing;
                    outRect.right = spacing;
                }

                // Add top margin only for the first row
                if (position < spanCount) {
                    outRect.top =spacing;
                }
            }
        });
        userList = new ArrayList<>();
        userAdapter = new UserAdapter2(this, userList);
        recyclerViewliked.setAdapter(userAdapter);

        // Firebase setup
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Toolbar navigation setup
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Fetch liked users
        getLikedUsers();
    }
    private void getLikedUsers() {
        if (currentUser != null) {
            DatabaseReference likedRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("likedList");
            likedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String likedUserId = snapshot.getKey();
                        fetchUserDetails(likedUserId);
                    }
                    updateVisibility();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LikedActivity.this, "Failed to get liked users", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchUserDetails(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User user = userSnapshot.getValue(User.class);
                if (user != null) {
                    userList.add(user);

                    updateVisibility();

                    userAdapter.notifyDataSetChanged(); // Ensure UI is updated
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LikedActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateVisibility() {
        if (userList.isEmpty()) {
            likedimage.setVisibility(View.VISIBLE);
            likedtext.setVisibility(View.VISIBLE);
            recyclerViewliked.setVisibility(View.GONE);
        } else {
            likedimage.setVisibility(View.GONE);
            likedtext.setVisibility(View.GONE);
            recyclerViewliked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}