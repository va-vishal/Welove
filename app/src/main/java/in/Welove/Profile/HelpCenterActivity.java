package in.Welove.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import in.Welove.BaseActivity;
import in.Welove.HelpCenter.DeleteAccountActivity;
import in.Welove.PrivacyActivity;
import in.Welove.R;
import in.Welove.RefundAndCancelation;
import in.Welove.TermsActivity;


public class HelpCenterActivity extends BaseActivity {

    private CardView appSupportLayout;
    private CardView termsAndConditionsLayout;
    private CardView privacyPoliciesLayout;
    private CardView safetyTipsLayout;
    private CardView deleteAccountLayout,Refundpolicieslayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

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
        appSupportLayout = findViewById(R.id.appsupportlayout);
        termsAndConditionsLayout = findViewById(R.id.termsandconditionslayout);
        privacyPoliciesLayout = findViewById(R.id.privacypolicieslayout);
        safetyTipsLayout = findViewById(R.id.safteytipslayout);
        deleteAccountLayout = findViewById(R.id.deleteaccountlayout);
        Refundpolicieslayout=findViewById(R.id.Refundpolicieslayout);

        appSupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to send an email
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                // Set the email recipient, subject, and body
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vishalods225@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Please Mention below your Registered Email id,username,and What is your issue Facing we will connect back Soon...\n Email id : ?\n username : ?");

                // Check if there's an email client installed
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } else {
                    // Handle the case where no email client is installed
                    Toast.makeText(HelpCenterActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        termsAndConditionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        privacyPoliciesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });

        safetyTipsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, SafetyTipsActivity.class);
                startActivity(intent);
            }
        });

        deleteAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, DeleteAccountActivity.class);
                startActivity(intent);
            }
        });
        Refundpolicieslayout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RefundAndCancelation.class);
            startActivity(intent);
        });
    }
}
