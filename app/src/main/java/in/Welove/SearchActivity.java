package in.Welove;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.Welove.Adapter.SearchAdapter;
import in.Welove.Authentication.LoginActivity1;
import in.Welove.Model.User;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<User> userList;  // Full user list
    private List<User> filteredList; // Filtered list for RecyclerView
    private EditText searchInput;
    private DatabaseReference reference;
    private FirebaseUser currentUser;
    private String currentUserGender; // Store current user's gender
    private String currentUserPreferredGender; // Store current user's preferred gender

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.searchback);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchInput = findViewById(R.id.searchInput);

        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        searchAdapter = new SearchAdapter(filteredList, this);
        recyclerView.setAdapter(searchAdapter);

        // Check network connection
        if (isNetworkConnected()) {
            // Check if the current user is logged in
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String currentUserId = currentUser.getUid();
                reference = FirebaseDatabase.getInstance().getReference("users");

                // Fetch current user details
                reference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User currentUserData = snapshot.getValue(User.class);
                        if (currentUserData != null) {
                            currentUserGender = currentUserData.getGender();
                            currentUserPreferredGender = currentUserData.getPrefGender();
                        }
                        fetchAllUsers(currentUserId);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

                // Handle search input
                searchInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String query = s.toString().toLowerCase().trim();
                        applySearchFilter(query);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            } else {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SearchActivity.this, LoginActivity1.class));
                finish();
            }
        } else {
            CustomDialog customDialog = new CustomDialog(SearchActivity.this);
            customDialog.show("No Internet", "Please check your network connection and try again.", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            }, true);
        }
    }

    private void fetchAllUsers(String currentUserId) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                filteredList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null && user.getId() != null && !user.getId().equals(currentUserId)) {
                        userList.add(user);

                        if (shouldShowUserByPreferredGender(user)) {
                            filteredList.add(user);
                        }
                    }
                }

                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean shouldShowUserByPreferredGender(User user) {
        if (currentUserPreferredGender == null || user.getGender() == null) {
            return false;
        }
        return user.getGender().equalsIgnoreCase(currentUserPreferredGender);
    }

    private void applySearchFilter(String query) {
        query = query.toLowerCase();
        filteredList.clear();

        if (query.isEmpty()) {
            for (User user : userList) {
                if (shouldShowUserByPreferredGender(user)) {
                    filteredList.add(user);
                }
            }
        } else {
            for (User user : userList) {
                if (user != null && user.getName() != null &&
                        user.getName().toLowerCase().contains(query) &&
                        shouldShowUserByPreferredGender(user)) {
                    filteredList.add(user);
                }
            }
        }

        searchAdapter.notifyDataSetChanged();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
