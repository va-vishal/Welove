package in.Welove.Authentication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import in.Welove.BaseActivity;
import in.Welove.CustomDialog;
import in.Welove.PrivacyActivity;
import in.Welove.R;
import in.Welove.TermsActivity;


public class MainActivity extends BaseActivity {

    private CheckBox termsCheckbox;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 20;
    private TextView termsText, privacyText;
    private Button emailButton, googleButton, loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseApp and FirebaseAuth
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        database = FirebaseDatabase.getInstance(); // Initialize FirebaseDatabase

        emailButton = findViewById(R.id.emailbutton);
        googleButton = findViewById(R.id.googlebutton);
        loginButton = findViewById(R.id.loginbutton);
        termsText = findViewById(R.id.termsText);
        privacyText = findViewById(R.id.privacyText);
        termsCheckbox = findViewById(R.id.termsCheckbox);

        setButtonDrawable(emailButton, R.drawable.gmail, R.dimen.drawable_width, R.dimen.drawable_height);
        setButtonDrawable(googleButton, R.drawable.googlee, R.dimen.drawable_width, R.dimen.drawable_height);
        setButtonDrawable(loginButton, R.drawable.user, R.dimen.drawable_width, R.dimen.drawable_height);

        emailButton.setOnClickListener(v -> {
            if (termsCheckbox.isChecked()) {
                openActivity(EmailRegistrationActivity.class);
            } else {
                showTermsDialog();
            }
        });
        googleButton.setOnClickListener(v -> {
            if (termsCheckbox.isChecked()) {
                googleSignIn();
            } else {
                showTermsDialog();
            }
        });
        termsText.setOnClickListener(v -> openActivity(TermsActivity.class));
        privacyText.setOnClickListener(v -> openActivity(PrivacyActivity.class));

        configureGoogleSignIn();
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Ensure this is correct
                .requestEmail() // Ensure email is requested
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void googleSignIn() {
        // Sign out first to make sure the account picker shows
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("MainActivity", "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        if (auth == null) {
            Log.e("MainActivity", "FirebaseAuth not initialized");
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            registerNewUser(user);
                        }
                    } else {
                        Log.e("MainActivity", "Firebase Authentication failed", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerNewUser(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String email = account.getEmail();
            database.getReference().child("users").orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(MainActivity.this, "Account already exists. Please log in.", Toast.LENGTH_LONG).show();
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity1.class);
                                startActivity(loginIntent);
                            } else {

                                HashMap<String, Object> userData = new HashMap<>();
                                userData.put("id", user.getUid());
                                userData.put("email", email);
                                userData.put("name", user.getDisplayName());
                                userData.put("completeprofile",false);
                                userData.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/welove-f8e77.appspot.com/o/20241001_113735.png?alt=media&token=3d22facb-09c7-46f2-b971-3f680971d261");

                                database.getReference().child("users").child(user.getUid()).setValue(userData)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Successfully Signed up.", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MainActivity.this, DOBActivity1.class);
                                                intent.putExtra("userId", user.getUid()); // Passing the user ID
                                                startActivity(intent);
                                                finish(); // Optional: Finish the current activity
                                            } else {
                                                Log.e("MainActivity", "Database write failed", task.getException());
                                                Toast.makeText(MainActivity.this, "Failed to sign up. Please check your internet connection or try again later.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("MainActivity", "Database query cancelled", databaseError.toException());
                            Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showTermsDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.show(
                getString(R.string.terms_and_conditions_title),
                getString(R.string.terms_and_conditions_prompt),
                v -> customDialog.dismiss(),
                true
        );
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
    }

    private void setButtonDrawable(Button button, int drawableResId, int widthResId, int heightResId) {
        Drawable drawable = getResources().getDrawable(drawableResId);
        int width = (int) getResources().getDimension(widthResId);
        int height = (int) getResources().getDimension(heightResId);

        drawable.setBounds(0, 0, width, height);
        button.setCompoundDrawables(drawable, null, null, null);
    }
}
