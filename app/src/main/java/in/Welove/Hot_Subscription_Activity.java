package in.Welove;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import in.Welove.Model.User;

public class Hot_Subscription_Activity extends AppCompatActivity implements PurchasesUpdatedListener {

    private static final String TAG = "Hot_Subscription_Activity";
    private static final String PRODUCT_ID = "hot_month";

    private Button subscribe1;
    private ImageView Info1;
    private CircleImageView userPic1;
    private TextView nametext1, marqueeTextView3;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private BillingClient billingClient;
    private SkuDetails selectedProductDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_subscription);

        Toolbar toolbar = findViewById(R.id.toolbar);
        subscribe1 = findViewById(R.id.btn_sub1);
        marqueeTextView3 = findViewById(R.id.marqueeTextView3);
        userPic1 = findViewById(R.id.userpic1);
        nametext1 = findViewById(R.id.username1);
        Info1 = findViewById(R.id.infoo1);

        marqueeTextView3.setText("Your Subscription will END SOON!!, please subscribe to maintain uninterrupted services and enjoy.");
        marqueeTextView3.setSelected(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchUserDetails(currentUser.getUid());
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection. Redirecting to Home.", Toast.LENGTH_SHORT).show();
            navigateToHomeActivity();
            return;
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        initializeBillingClient();

        Info1.setOnClickListener(v -> showPopupWithAnimation());

        subscribe1.setOnClickListener(v -> {
            if (selectedProductDetails != null) {
                startPurchaseFlow();
            } else {
                Toast.makeText(this, "Product details not loaded yet. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this) // Set the PurchasesUpdatedListener here
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryAvailableSubscriptions();
                } else {
                    Log.e(TAG, "Billing setup failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.e(TAG, "Billing service disconnected");
            }
        });
    }

    private void queryAvailableSubscriptions() {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();

        billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        if (skuDetails.getSku().equals(PRODUCT_ID)) {
                            selectedProductDetails = skuDetails;
                        }
                    }
                } else {
                    Log.e(TAG, "Error querying subscriptions: " + billingResult.getDebugMessage());
                }
            }
        });
    }

    private void startPurchaseFlow() {
        if (selectedProductDetails != null) {
            Log.d(TAG, "Starting purchase flow for product ID: " + PRODUCT_ID);

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(selectedProductDetails) // Use SkuDetails directly
                    .build();

            BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Billing Flow started successfully");
            } else {
                Log.e(TAG, "Error starting billing flow: " + billingResult.getDebugMessage());
            }
        } else {
            Log.w(TAG, "Subscribe button clicked, but product details are not loaded.");
            Toast.makeText(this, "Product details not loaded yet. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Purchase Failed: " + billingResult.getDebugMessage());
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();

            billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Purchase acknowledged successfully!");
                    Toast.makeText(this, "Subscription Activated", Toast.LENGTH_SHORT).show();

                    // Update Firebase with subscription details
                    updateUserSubscriptionDetails(purchase);
                } else {
                    Log.e(TAG, "Failed to acknowledge purchase: " + billingResult.getDebugMessage());
                }
            });
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged()) {
            Log.d(TAG, "Purchase already acknowledged.");
            updateUserSubscriptionDetails(purchase);
        }
    }

    private void updateUserSubscriptionDetails(Purchase purchase) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            long startDate = System.currentTimeMillis();
            long endDate = calculateEndDate(startDate);

            userRef.child("hotProfile").setValue(true);
            userRef.child("hot").setValue(true);
            userRef.child("hotStartDate").setValue(startDate);
            userRef.child("hotProfieOrNormalProfile").setValue(true);
            userRef.child("hotEndDate").setValue(endDate);
        }
    }

    private long calculateEndDate(long startDate) {
        return startDate + (30L * 24 * 60 * 60 * 1000); // Add 30 days to the current date
    }

    private void fetchUserDetails(String userId) {
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    nametext1.setText("Hello " + user.getName() + ", become a Hot member and enjoy the benefits below.");
                    Picasso.get().load(user.getImageurl()).into(userPic1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Hot_Subscription_Activity.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm != null ? cm.getActiveNetwork() : null;
        NetworkCapabilities capabilities = cm != null ? cm.getNetworkCapabilities(activeNetwork) : null;
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void showPopupWithAnimation() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Add animation to the popup
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        popupWindow.showAtLocation(subscribe1, Gravity.CENTER, 0, 0);
    }
}
