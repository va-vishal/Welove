package in.Welove.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.Welove.Adapter.TransactionAdapter;
import in.Welove.BaseActivity;
import in.Welove.Model.Transaction;
import in.Welove.Model.User;
import in.Welove.R;

public class WalletActivity extends BaseActivity {

    private static final int GOOGLE_PAY_REQUEST_CODE = 123;

    private RoundedImageView profileImage;
    private TextView name, walletBalance;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private Button add_Money;
    private double currentBalance;
    private ValueEventListener userDetailsListener;
    private WebView mWebView;
    private Context context;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);



        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        profileImage = findViewById(R.id.profile_pic);
        name = findViewById(R.id.userName);
        walletBalance = findViewById(R.id.walletBalance);
        add_Money = findViewById(R.id.add_Money_to_walletButton);
        mWebView = findViewById(R.id.payment_webview);
        context = this;

        if (currentUser != null) {
            fetchUserDetails(currentUser.getUid());
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        add_Money.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(WalletActivity.this, PaymentRequestActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(WalletActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
        if (currentUser != null) {
            fetchTransactionHistory(currentUser.getUid());
        } else {
            Toast.makeText(WalletActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

    }
    private void fetchTransactionHistory(String userId) {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("transactions");

        transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Transaction> transactionList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    transactionList.add(transaction);
                }
                setupTransactionRecyclerView(transactionList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FetchTransactions", "Failed to fetch transaction history: " + databaseError.getMessage());
            }
        });
    }

    private void setupTransactionRecyclerView(List<Transaction> transactionList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTransactions);

        TransactionAdapter adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }


    private void fetchUserDetails(String userId) {
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userDetailsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        name.setText(user.getName());
                        currentBalance = user.getWalletBalance();
                        walletBalance.setText("₹ " + currentBalance);
                        Picasso.get().load(user.getImageurl()).placeholder(R.drawable.defaultimage).into(profileImage);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WalletActivity", "Failed to fetch user details: " + databaseError.getMessage());
            }
        };
        userRef.addValueEventListener(userDetailsListener);
    }

    private void showAddMoneyDialog(String userId) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_money, null);
        EditText amountEditText = dialogView.findViewById(R.id.amount_edit_text);
        Button okButton = dialogView.findViewById(R.id.ok_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(v -> {
            String amountStr = amountEditText.getText().toString().trim();
            if (!amountStr.isEmpty()) {
                try {
                    int amount = Integer.parseInt(amountStr);
                    if (amount <= 0) {
                        Toast.makeText(WalletActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(WalletActivity.this, PaymentRequestActivity.class);

                    intent.putExtra("amount", amount);
                    startActivity(intent);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(WalletActivity.this, "Invalid amount entered.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(WalletActivity.this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
            }
        });

// Cancel button listener to dismiss the dialog
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userRef != null && userDetailsListener != null) {
            userRef.removeEventListener(userDetailsListener);
        }
    }
}
