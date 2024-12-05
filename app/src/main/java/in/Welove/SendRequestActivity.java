package in.Welove;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import in.Welove.Model.Transaction;
import in.Welove.Profile.WalletActivity;

public class SendRequestActivity extends BaseActivity {

    private EditText emailEditText, usernameEditText, amountEditText, utrUpiEditText, additionalInfoEditText;
    private Button submitButton;
    private DatabaseReference transactionsRef;
    private FirebaseUser currentUser;
    private static final String TAG = "SendRequestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        emailEditText = findViewById(R.id.emailedittext);
        usernameEditText = findViewById(R.id.usernameEDittext);
        amountEditText = findViewById(R.id.amount_edit_text);
        utrUpiEditText = findViewById(R.id.utr_upi);
        additionalInfoEditText = findViewById(R.id.additional_info_edit_text);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSubmit();
            }
        });
    }

    // Method to handle the form submission
    private void handleSubmit() {
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String amount = amountEditText.getText().toString();
        String utrUpi = utrUpiEditText.getText().toString();
        String additionalInfo = additionalInfoEditText.getText().toString();

        // Validate if required fields are not empty
        if (email.isEmpty() || username.isEmpty() || amount.isEmpty() || utrUpi.isEmpty()) {
            Toast.makeText(SendRequestActivity.this, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        addTransaction(Integer.parseInt(amount), "Payment Request is under processing", false);

        // Create the email content
        String subject = "Payment Request";
        String body = "Email: " + email + "\n" +
                "Username: " + username + "\n" +
                "Amount: " + amount + "\n" +
                "UTR/Transaction ID: " + utrUpi + "\n" +
                "Additional Info: " + additionalInfo;

        // Create the email intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vishnugana.prajeeth@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        // Verify that there are email clients installed
        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendRequestActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
        String whatsappNumber = "7013993149"; // Make sure to include the country code for WhatsApp
        String whatsappMessage = "Payment Request Details:\n" +
                "Email: " + email + "\n" +
                "Username: " + username + "\n" +
                "Amount: " + amount + "\n" +
                "UTR/Transaction ID: " + utrUpi + "\n" +
                "Additional Info: " + additionalInfo;
        try {
            String url = "https://api.whatsapp.com/send?phone=+91" + whatsappNumber + "&text=" + Uri.encode(whatsappMessage);
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            whatsappIntent.setData(Uri.parse(url));
            whatsappIntent.setPackage("com.whatsapp");
            startActivity(whatsappIntent);
            //redirectToWalletActivity();
        } catch (Exception e) {
            Toast.makeText(SendRequestActivity.this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTransaction(int amountToDeduct, String description, boolean isSuccessful) {
        long timestamp = System.currentTimeMillis();
        Transaction transaction = new Transaction(amountToDeduct, "Processing", timestamp, description);
        transactionsRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUser.getUid())
                .child("transactions");

        transactionsRef.push().setValue(transaction)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Transaction added successfully.");
                    } else {
                        Log.e(TAG, "Failed to add transaction.");
                    }
                });
    }

    private void redirectToWalletActivity() {
        Intent intent = new Intent(SendRequestActivity.this, WalletActivity.class);
        startActivity(intent);
        finish();
    }
}
