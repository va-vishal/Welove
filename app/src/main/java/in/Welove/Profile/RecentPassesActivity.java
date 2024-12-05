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
import androidx.annotation.Nullable;
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


public class RecentPassesActivity extends BaseActivity {

    private RecyclerView recyclerViewPasses;
    private UserAdapter2 userAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ImageView passesImage;
    private TextView passesText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_passes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passesImage = findViewById(R.id.privacyimage);
        passesText = findViewById(R.id.privacytext);
        recyclerViewPasses = findViewById(R.id.recyclerViewDisliked);
        recyclerViewPasses.setHasFixedSize(true);
        recyclerViewPasses.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewPasses.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int spacing = 8;
                int pacing=-25;
                int position = parent.getChildAdapterPosition(view);
                int spanCount = 2;

                outRect.right = pacing;
                outRect.bottom = spacing;
                if (position % spanCount == 0) {
                    outRect.left = spacing;
                } else {
                    outRect.left = pacing;
                    outRect.right = spacing;
                }
                if (position < spanCount) {
                    outRect.top =spacing;
                }
            }
        });
        userList = new ArrayList<>();
        userAdapter = new UserAdapter2(this, userList);
        recyclerViewPasses.setAdapter(userAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

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
        getDislikedUsers();
    }
    private void getDislikedUsers() {
        if (currentUser != null) {
            DatabaseReference dislikesRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("dislikedList");

            dislikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String dislikedUserId = snapshot.getKey();
                        fetchUserDetails(dislikedUserId);
                    }
                    updateVisibility();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RecentPassesActivity.this, "Failed to get disliked users", Toast.LENGTH_SHORT).show();
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
                }
                updateVisibility();
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecentPassesActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVisibility() {
        if (userList.isEmpty()) {
            passesImage.setVisibility(View.VISIBLE);
            passesText.setVisibility(View.VISIBLE);
            recyclerViewPasses.setVisibility(View.GONE);
        } else {
            passesImage.setVisibility(View.GONE);
            passesText.setVisibility(View.GONE);
            recyclerViewPasses.setVisibility(View.VISIBLE);
        }
    }
}
