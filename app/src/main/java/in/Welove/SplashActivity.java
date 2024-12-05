package in.Welove;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import in.Welove.Authentication.DOBActivity1;
import in.Welove.Authentication.LoginOrRegister;

public class SplashActivity extends AppCompatActivity {

    private ImageView splashlogo;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashlogo = findViewById(R.id.splash_logo);
        mAuth = FirebaseAuth.getInstance();

        // Register the ActivityResultLauncher for IntentSenderRequest
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() != RESULT_OK) {
                            Toast.makeText(SplashActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        startLogoAnimation();
    }

    private void startLogoAnimation() {
        splashlogo.setRotation(0f);
        splashlogo.animate()
                .rotation(360f)
                .setDuration(3000)
                .withEndAction(this::requestAllPermissions)
                .start();
    }

    private void requestAllPermissions() {
        String[] permissions = {
                Manifest.permission.VIBRATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[] {
                    Manifest.permission.VIBRATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }

        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            checkForAppUpdate();
                        } else {
                            Toast.makeText(SplashActivity.this, "All permissions are required to proceed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void checkForAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Start the update process immediately
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
            } else {
                // If no immediate update is required, proceed to the next step
                checkUserProfileCompletion();
            }
        }).addOnFailureListener(e -> {
            // Handle error if the update check fails
            Toast.makeText(SplashActivity.this, "Error checking for updates", Toast.LENGTH_SHORT).show();
            checkUserProfileCompletion();  // Proceed to the next step if update check fails
        });
    }

    private void checkUserProfileCompletion() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Check if the user has completed their profile
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean completeProfile = dataSnapshot.child("completeprofile").getValue(Boolean.class);
                        if (completeProfile != null && !completeProfile) {
                            // Redirect to DOBActivity1 if profile is incomplete
                            Intent intent = new Intent(SplashActivity.this, DOBActivity1.class);
                            startActivity(intent);
                            finish();
                        } else {
                            proceedToNextActivity();  // Profile is complete, proceed to next activity
                        }
                    } else {
                        proceedToNextActivity();  // No profile data, proceed to next activity
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SplashActivity.this, "Error checking profile data", Toast.LENGTH_SHORT).show();
                    proceedToNextActivity();  // Proceed even if profile data fetch fails
                }
            });
        } else {
            proceedToNextActivity();  // No user is logged in, proceed to login
        }
    }

    private void proceedToNextActivity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent = currentUser != null ? new Intent(SplashActivity.this, HomeActivity.class)
                : new Intent(SplashActivity.this, LoginOrRegister.class);
        startActivity(intent);
        finish();
    }

    private void showUpdateDialog() {
        new AlertDialog.Builder(this)
                .setTitle("App Update Required")
                .setMessage("A new version of the app is available. Please update to continue using the app.")
                .setCancelable(false)
                .setPositiveButton("Update", (dialog, which) -> startUpdateFlow())
                .setNegativeButton("Exit", (dialog, which) -> finish()) // Close the app if they don't want to update
                .show();
    }

    private void startUpdateFlow() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
            }
        });
    }
}
