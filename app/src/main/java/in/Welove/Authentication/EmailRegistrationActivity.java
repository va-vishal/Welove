package in.Welove.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import in.Welove.BaseActivity;
import in.Welove.CustomDialog;
import in.Welove.R;

public class EmailRegistrationActivity extends BaseActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button nextButton;
    private TextView loginTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_registration);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        nextButton = findViewById(R.id.next_button);
        loginTextView = findViewById(R.id.txt_Login);

        nextButton.setOnClickListener(v -> validateInputAndProceed());

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(EmailRegistrationActivity.this, LoginActivity1.class);
            startActivity(intent);
            finish();
        });
    }

    private void validateInputAndProceed() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }
        if (password.length() < 8) {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Confirm password is required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                if (isNewUser) {
                    Intent dobIntent = new Intent(EmailRegistrationActivity.this, DOBActivity.class);
                    dobIntent.putExtra("email", email);
                    dobIntent.putExtra("password", password);
                    startActivity(dobIntent);

                } else {
                    showEmailAlreadyRegisteredDialog();
                }
            } else {
                Toast.makeText(EmailRegistrationActivity.this, "Failed to check email. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmailAlreadyRegisteredDialog() {
        CustomDialog customDialog = new CustomDialog(EmailRegistrationActivity.this);
        customDialog.show(
                "Email Already Registered",
                "This email is already registered. Please log in instead.",
                v -> customDialog.dismiss(),
                true
        );
    }

}
