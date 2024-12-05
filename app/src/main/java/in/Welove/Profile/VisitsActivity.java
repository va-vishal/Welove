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


public class VisitsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private UserAdapter2 userAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView visitsimage;
    private TextView visitstext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);

        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        visitsimage = findViewById(R.id.visitimage);
        visitstext = findViewById(R.id.visittext);
        recyclerView = findViewById(R.id.recyclerViewVisited);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        recyclerView.setAdapter(userAdapter);

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
        getVisitedUsers();
    }

    private void getVisitedUsers() {
        if (currentUser != null) {
            DatabaseReference visitsRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("visitsList");
            visitsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String visitedUserId = snapshot.getKey();
                        fetchUserDetails(visitedUserId);
                    }
                    updateVisibility();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VisitsActivity.this, "Failed to get visited users", Toast.LENGTH_SHORT).show();
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
                    userAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VisitsActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateVisibility() {
        if (userList.isEmpty()) {
            visitsimage.setVisibility(View.VISIBLE);
            visitstext.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            visitsimage.setVisibility(View.GONE);
            visitstext.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
