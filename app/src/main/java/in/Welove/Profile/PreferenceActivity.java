package in.Welove.Profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import in.Welove.Authentication.LoginActivity1;
import in.Welove.BaseActivity;
import in.Welove.R;


public class PreferenceActivity extends BaseActivity {

    private TextView area, state, distancetext, agetext;
    private Button update, viewprofile;
    private SeekBar distanceseek, ageSeekbar;
    private Switch hidelocation, hidename, hideAge, hideProfile,hotOrNarmalProfile;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private CardView hotlayout;
    private final int minAge = 18;
    private int maxAge = 60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentuser = auth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        if (currentuser != null) {
            // User is logged in, update preferences and UI
            updatePreferencesStateToUi();

            // Initialize Views
            progressBar = findViewById(R.id.progressBar);
            agetext = findViewById(R.id.agetext);
            distancetext = findViewById(R.id.distancetext);
            update = findViewById(R.id.update);
            viewprofile = findViewById(R.id.viewprofile);
            distanceseek = findViewById(R.id.distanceseek);
            hotlayout = findViewById(R.id.hotlayout);
            ageSeekbar = findViewById(R.id.ageSeekbar);
            hidelocation = findViewById(R.id.hidelocation);
            hidename = findViewById(R.id.hidename);
            hideAge = findViewById(R.id.hideAge);
            hideProfile = findViewById(R.id.hideprofile);
            hotOrNarmalProfile = findViewById(R.id.hotOrNarmalProfile);

            // Set Seekbar Max Values
            ageSeekbar.setMax(42);
            distanceseek.setMax(500);

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            // Setup listeners for switches and seekbars
            setupSwitchListeners();
            setupSeekBarListeners();

            // Handle button clicks
            update.setOnClickListener(v -> updateLocation());
            viewprofile.setOnClickListener(v -> startActivity(new Intent(PreferenceActivity.this, ProfileActivity.class)));
        } else {
            // No user is logged in, redirect to Login screen
            Intent intent = new Intent(PreferenceActivity.this, LoginActivity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // End current activity so user cannot go back to it
        }
    }


    private void setupSwitchListeners() {
        setupHidelocationListener();
        setupHidenameListener();
        setupHideAgeListener();
        setupHideProfileListener();
        setupHotProfilesListener();
    }
    private void setupHotProfilesListener() {
        hotOrNarmalProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hotOrNarmalProfile, "Only View", isChecked);
        });
    }


    private void setupHidelocationListener() {
        hidelocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hidelocation, "Your location", isChecked);
        });
    }

    private void setupHidenameListener() {
        hidename.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hidename, "Your Name", isChecked);
        });
    }

    private void setupHideAgeListener() {
        hideAge.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hideAge, "Your Age", isChecked);
        });
    }

    private void setupHideProfileListener() {
        hideProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleSwitchChange(hideProfile, "Your Profile", isChecked);
        });
    }

    private void handleSwitchChange(Switch switchView, String label, boolean isChecked) {
        String displayText;
        int textColor;
        int thumbResource;

        // Handle Hot Profiles separately
        if ("Only View".equals(label)) {
            displayText = label + (isChecked ? " Hot Profiles" : " Normal Profiles");
            textColor = isChecked ? Color.WHITE: Color.WHITE;
            thumbResource = isChecked ? R.drawable.hott : R.drawable.hott;
        } else {
            displayText = label + (isChecked ? " is Hidden now" : " is Visible now");
            textColor = isChecked ? Color.BLACK : Color.BLACK;
            thumbResource = isChecked ? R.drawable.ic_circle : R.drawable.ic_circle1;
        }

        // Apply updates to switch UI
        switchView.setText(displayText);
        switchView.setTextColor(textColor);
        switchView.setThumbResource(thumbResource);

        updatePreferenceInDatabase(label, isChecked);
    }

    private void updatePreferenceInDatabase(String preferenceKey, boolean isHidden) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        Map<String, Object> preferences = new HashMap<>();

        String databaseKey = "";

        // Map the preferenceKey to the database key
        switch (preferenceKey) {
            case "Your location":
                databaseKey = "hideLocation";
                break;
            case "Your Name":
                databaseKey = "hideName";
                break;
            case "Your Age":
                databaseKey = "hideAge";
                break;
            case "Your Profile":
                databaseKey = "hideProfile";
                break;
            case "Only View":
                databaseKey = "hotProfieOrNormalProfile";
                break;
        }

        if (databaseKey.isEmpty()) {
            Toast.makeText(PreferenceActivity.this, "Unknown preference: " + preferenceKey, Toast.LENGTH_SHORT).show();
            return;
        }
        preferences.put(databaseKey, isHidden);

        databaseReference.updateChildren(preferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "Preferences updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update preferences: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupSeekBarListeners() {
        distanceseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distancetext.setText("Maximum distance: " + progress + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                updateDistancePreferenceInDatabase(progress);
            }
        });

        ageSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int adjustedProgress = progress + 18; // Adjust progress to start from 18
                agetext.setText("Age Range 18 : " + adjustedProgress);
                maxAge = adjustedProgress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress() + 18;
                updateAgePreferenceInDatabase(progress);
            }
        });
    }

    private void updateDistancePreferenceInDatabase(int distance) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("maxDistance", distance);

        databaseReference.updateChildren(preferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "Distance preference updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update distance preference: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateAgePreferenceInDatabase(int age) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("ageRange", age);

        databaseReference.updateChildren(preferences)
                .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "Age preference updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update age preference: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updatePreferencesStateToUi() {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = database.getReference("users").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean isLocationHidden = snapshot.child("hideLocation").getValue(Boolean.class);
                    Boolean isNameHidden = snapshot.child("hideName").getValue(Boolean.class);
                    Boolean isAgeHidden = snapshot.child("hideAge").getValue(Boolean.class);
                    Boolean isProfileHidden = snapshot.child("hideProfile").getValue(Boolean.class);
                    Boolean isHot = snapshot.child("hotProfieOrNormalProfile").getValue(Boolean.class);
                    Boolean isHott = snapshot.child("hotProfile").getValue(Boolean.class);
                    Integer maxDistance = snapshot.child("maxDistance").getValue(Integer.class);
                    Integer ageRange = snapshot.child("ageRange").getValue(Integer.class);

                    if (isLocationHidden != null) {
                        hidelocation.setChecked(isLocationHidden);
                        hidelocation.setText("Your location" + (isLocationHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (isHot != null) {
                        hotOrNarmalProfile.setChecked(isHot);
                        hotOrNarmalProfile.setText("Only View " + (isHot ? "Hot Profiles" : "Normal Profiles"));
                        if(isHott){
                            hotlayout.setVisibility(View.VISIBLE);
                        }else{
                            hotlayout.setVisibility(View.GONE);
                        }
                    }
                    if (isNameHidden != null) {
                        hidename.setChecked(isNameHidden);
                        hidename.setText("Your Name" + (isNameHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (isAgeHidden != null) {
                        hideAge.setChecked(isAgeHidden);
                        hideAge.setText("Your Age" + (isAgeHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (isProfileHidden != null) {
                        hideProfile.setChecked(isProfileHidden);
                        hideProfile.setText("Your Profile" + (isProfileHidden ? " is Hidden now" : " is Visible now"));
                    }
                    if (maxDistance != null) {
                        distanceseek.setProgress(maxDistance);
                        distancetext.setText("Maximum distance: " + maxDistance + "km");
                    }
                    if (ageRange != null) {
                        ageSeekbar.setProgress(ageRange - 18);
                        agetext.setText("Age Range 18 : " + ageRange);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreferenceActivity.this, "Failed to load preferences: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                saveUserLocation(location);
            } else {
                Toast.makeText(PreferenceActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveUserLocation(Location location) {
        Geocoder geocoder = new Geocoder(PreferenceActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String area = address.getLocality();
                String state = address.getAdminArea();

                String userId = auth.getCurrentUser().getUid();
                DatabaseReference databaseReference = database.getReference("users").child(userId);

                Map<String, Object> locationUpdates = new HashMap<>();
                locationUpdates.put("area", area);
                locationUpdates.put("state", state);
                locationUpdates.put("latitude", location.getLatitude());
                locationUpdates.put("longitude", location.getLongitude());
                progressBar.setVisibility(View.GONE);
                databaseReference.updateChildren(locationUpdates)
                        .addOnSuccessListener(aVoid -> Toast.makeText(PreferenceActivity.this, "User's location updated successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(PreferenceActivity.this, "Failed to update user's location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PreferenceActivity.this, "Geocoder failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
