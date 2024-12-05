package in.Welove.HelpCenter;

import android.annotation.SuppressLint;;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.TimeUnit;
import in.Welove.Authentication.LoginActivity1;
import in.Welove.R;

public class DeleteAccountActivity extends AppCompatActivity {

    private Button deleteButton;
    private Button cancelButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        deleteButton = findViewById(R.id.deletebutton);
        cancelButton = findViewById(R.id.cancelbutton);

        // Initially hide both buttons
        deleteButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        checkDeletionRequest(); // Check if there's an existing deletion request

        deleteButton.setOnClickListener(v -> showDeletePopupWindow(v));
        cancelButton.setOnClickListener(v -> cancelDeletionRequest());
    }

    private void checkDeletionRequest() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.child("delete_requested_at").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long deleteRequestedAt = dataSnapshot.getValue(Long.class);
                if (deleteRequestedAt != null) {
                    // If a deletion request exists, show cancel button
                    deleteButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.VISIBLE);
                } else {
                    // No deletion request, show delete button
                    deleteButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DeleteAccountActivity.this, "Failed to check deletion request.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeletePopupWindow(View anchorView) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.custom_dialog, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        Button cancel = popupView.findViewById(R.id.cancel);
        Button confirm = popupView.findViewById(R.id.confirm);

        cancel.setOnClickListener(view -> popupWindow.dismiss());

        confirm.setOnClickListener(view -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("users").child(userId);

            userRef.child("delete_requested_at").setValue(System.currentTimeMillis());
            scheduleDeleteAccount(userId);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();

            Toast.makeText(DeleteAccountActivity.this, "Account deletion scheduled. You have been signed out.", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
            checkDeletionRequest();
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void cancelDeletionRequest() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(userId);

        // Remove the deletion request timestamp
        userRef.child("delete_requested_at").removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                WorkManager.getInstance(this).cancelAllWorkByTag(userId); // Cancel the scheduled worker
                Toast.makeText(DeleteAccountActivity.this, "Account deletion request cancelled.", Toast.LENGTH_SHORT).show();
                checkDeletionRequest(); // Refresh button visibility
            } else {
                Toast.makeText(DeleteAccountActivity.this, "Failed to cancel deletion request.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleDeleteAccount(String userId) {
        Data data = new Data.Builder()
                .putString("userId", userId)
                .build();
        OneTimeWorkRequest deleteAccountWorkRequest =
                new OneTimeWorkRequest.Builder(DeleteAccountWorker.class)
                        .setInitialDelay(30, TimeUnit.DAYS)
                        .setInputData(data)
                        .addTag(userId)
                        .build();

        WorkManager.getInstance(this).enqueue(deleteAccountWorkRequest);
    }

}
