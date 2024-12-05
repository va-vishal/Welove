package in.Welove.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.Welove.BaseActivity;
import in.Welove.R;
import in.Welove.SendRequestActivity;

public class PaymentRequestActivity extends BaseActivity {

    private Button makePaymentRequestButton;
    private ImageView qrImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        makePaymentRequestButton = findViewById(R.id.make_payment_request_button);
        qrImageView = findViewById(R.id.qr_image_view);

        qrImageView.setVisibility(View.VISIBLE);

        makePaymentRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PaymentRequestActivity.this, SendRequestActivity.class);
                startActivity(intent);
            }
        });
    }
}
