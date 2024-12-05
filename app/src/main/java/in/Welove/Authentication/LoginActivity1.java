package in.Welove.Authentication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import in.Welove.BaseActivity;
import in.Welove.CustomDialog;
import in.Welove.HomeActivity;
import in.Welove.R;

public class LoginActivity1 extends BaseActivity {

    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In
    private static final String TAG = "LoginActivity1"; // Tag for logging
    private EditText emailEditText, passwordEditText;
    private Button loginButton, loginwithgoogle;
    private TextView registerButton, forgotPasswordButton;
    private ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        auth = FirebaseAuth.getInstance();
        initializeViews();
        configureGoogleSignIn();
        setUpListeners();
        Log.d(TAG, "onCreate: LoginActivity1 initialized");
    }

    private void initializeViews() {
        forgotPasswordButton = findViewById(R.id.forgotpassword);
        emailEditText = findViewById(R.id.identifier);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        setupPasswordVisibilityToggle();
        loginwithgoogle = findViewById(R.id.loginwithgoogle);
        Log.d(TAG, "initializeViews: Views initialized");


        setButtonDrawable(loginwithgoogle, R.drawable.googlee, R.dimen.drawable_width, R.dimen.drawable_height);
    }
    private void setButtonDrawable(Button button, int drawableResId, int widthResId, int heightResId) {
        Drawable drawable = getResources().getDrawable(drawableResId);
        int width = (int) getResources().getDimension(widthResId);
        int height = (int) getResources().getDimension(heightResId);

        drawable.setBounds(0, 0, width, height);
        button.setCompoundDrawables(drawable, null, null, null);
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Log.d(TAG, "configureGoogleSignIn: Google Sign-In configured");
    }
    private void setUpListeners() {
        loginwithgoogle.setOnClickListener(v -> {
            if (isConnected()) {
                signInWithGoogle();
            } else {
                showNoInternetMessage();
            }
        });
        loginButton.setOnClickListener(v -> {
            if (isConnected()) {
                loginUser();
            } else {
                showNoInternetMessage();
            }
        });

        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity1.this, MainActivity.class)));

        forgotPasswordButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity1.this, ForgotPasswordActivity.class)));
        Log.d(TAG, "setUpListeners: Listeners set up");
    }

    private void setupPasswordVisibilityToggle() {
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
        Log.d(TAG, "setupPasswordVisibilityToggle: Password visibility toggle set up");
    }

    private void togglePasswordVisibility() {
        if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_outline, 0);
            Log.d(TAG, "togglePasswordVisibility: Password visibility set to visible");
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off_outline, 0);
            Log.d(TAG, "togglePasswordVisibility: Password visibility set to hidden");
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (validateInput(email, password) && isEmail(email)) {
            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "loginUser: Attempting to log in user with email: " + email);
            signInWithEmail(email, password);
        } else {
            if (!isEmail(email)) {
                emailEditText.setError("Please enter a valid email address");
            }
        }
    }

    private void signInWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {

                                Toast.makeText(LoginActivity1.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "signInWithEmail: Login successful for user ID: " + user.getUid());
                                Intent homeIntent = new Intent(LoginActivity1.this, HomeActivity.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homeIntent);
                                finish();
                            } else {
                                showVerificationDialog();
                                Log.d(TAG, "signInWithEmail: Email not verified for user ID: " + user.getUid());
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity1.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "signInWithEmail: Login failed: " + task.getException().getMessage());
                    }
                });
    }
    private void showVerificationDialog() {
        CustomDialog customDialog = new CustomDialog(LoginActivity1.this);
        customDialog.show(
                "Email Not Verified",
                "Please verify your email address. A verification email has been sent to your email. You cannot log in until your email is verified for security reasons.",
                v -> {
                    customDialog.dismiss();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(emailTask -> {
                                    if (emailTask.isSuccessful()) {
                                        Toast.makeText(LoginActivity1.this, "A new verification email has been sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity1.this, "Failed to send verification email. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                },
                true
        );
    }




    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }
        return true;
    }

    private boolean isEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d(TAG, "signInWithGoogle: Google sign-in intent started");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            Log.d(TAG, "onActivityResult: Google sign-in result handled");
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "handleSignInResult: Google sign-in successful, account: " + account.getEmail());
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult: Google sign-in failed", e);
            Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "firebaseAuthWithGoogle: Login with Google successful");
                FirebaseUser user = auth.getCurrentUser();
                checkUserInDatabase(user);
            } else {
                Log.w(TAG, "firebaseAuthWithGoogle: Login with Google failed", task.getException());
                Toast.makeText(LoginActivity1.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserInDatabase(FirebaseUser user) {
        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Check the "completeProfile" field in the database
                    Boolean isProfileComplete = snapshot.child("completeprofile").getValue(Boolean.class);

                    if (isProfileComplete != null && isProfileComplete) {
                        Log.d(TAG, "checkUserInDatabase: Profile is complete, proceeding to HomeActivity");
                        // Navigate to HomeActivity
                        Intent homeIntent = new Intent(LoginActivity1.this, HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish(); // Optional: Closes LoginActivity1
                    } else {
                        Log.d(TAG, "checkUserInDatabase: Profile is not complete, redirecting to profile completion");
                        // Navigate to DOBActivity1
                        Intent dobIntent = new Intent(LoginActivity1.this, DOBActivity1.class);
                        dobIntent.putExtra("userId", userId); // Pass userId if needed in DOBActivity1
                        dobIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(dobIntent);
                        finish();
                    }
                } else {
                    Log.d(TAG, "checkUserInDatabase: User does not exist, redirecting to registration");
                    // Navigate to MainActivity for registration
                    Intent registerIntent = new Intent(LoginActivity1.this, MainActivity.class);
                    registerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(registerIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "checkUserInDatabase: Database error: " + error.getMessage());
                Toast.makeText(LoginActivity1.this, "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showNoInternetMessage() {
        Toast.makeText(LoginActivity1.this, "No internet connection. Please check your connection.", Toast.LENGTH_LONG).show();
    }
}
