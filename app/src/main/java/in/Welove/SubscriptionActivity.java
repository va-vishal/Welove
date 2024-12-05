package in.Welove;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.*;
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
import in.Welove.Adapter.SubscriptionAdapter;
import in.Welove.Model.SubscriptionItem;
import in.Welove.Model.User;

public class SubscriptionActivity extends FragmentActivity {

    private static final String TAG = "SubscriptionActivity";
    private RecyclerView subscriptionRecyclerView;
    private SubscriptionAdapter subscriptionAdapter;
    private List<SubscriptionItem> subscriptionItems;
    private Button subscribe, pw, pm, p3m, ppw, ppm, pp3m;
    private ImageView Info;
    private CircleImageView userPic;
    private TextView nametext, marqueeTextView2;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private BillingClient billingClient;
    private SkuDetails selectedSkuDetails;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        // Initialize Firebase user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        subscriptionRecyclerView = findViewById(R.id.subscriptionRecyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        subscribe = findViewById(R.id.btn_sub);
        marqueeTextView2 = findViewById(R.id.marqueeTextView2);
        userPic = findViewById(R.id.userpic);
        nametext = findViewById(R.id.username);
        Info = findViewById(R.id.infoo);

        pw = findViewById(R.id.pw);
        pm = findViewById(R.id.pm);
        p3m = findViewById(R.id.p3m);
        ppw = findViewById(R.id.ppw);
        ppm = findViewById(R.id.ppm);
        pp3m = findViewById(R.id.pp3m);

        marqueeTextView2.setText("Your Subscription will END SOON!!, please subscribe to maintain uninterrupted services and enjoy.");
        marqueeTextView2.setSelected(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        subscriptionRecyclerView.setLayoutManager(layoutManager);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(subscriptionRecyclerView);

        subscriptionItems = new ArrayList<>();
        subscriptionAdapter = new SubscriptionAdapter(subscriptionItems, this::handleSubscriptionSelection, getApplicationContext());
        subscriptionRecyclerView.setAdapter(subscriptionAdapter);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (currentUser != null) {
            fetchUserDetails(currentUser.getUid());
        }
        isNetworkAvailable();

        setupSubscriptionItems();
        initializeBillingClient();

        subscribe.setOnClickListener(v -> {
            int selectedPosition = subscriptionAdapter.getSelectedPosition();
            if (selectedPosition == -1) {
                Toast.makeText(this, "Please select a plan", Toast.LENGTH_SHORT).show();
            } else if (selectedSkuDetails != null) {
                startPurchase(selectedSkuDetails);
            } else {
                Toast.makeText(this, "Subscription plan not available", Toast.LENGTH_SHORT).show();
            }
        });
        Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWithAnimation();
            }
        });
        pw.setOnClickListener(v -> {
            scrollToSubscription("Premium Week");
        });

        pm.setOnClickListener(v -> {
            scrollToSubscription("Premium Month");
        });
        p3m.setOnClickListener(v -> {
            scrollToSubscription("Premium 3 Months");
        });
        ppw.setOnClickListener(v -> {
            scrollToSubscription("Premium Plus Week");
        });
        ppm.setOnClickListener(v -> {
            scrollToSubscription("Premium Plus Month");
        });
        pp3m.setOnClickListener(v -> {
            scrollToSubscription("Premium Plus 3 Months");
        });
    }

    private void initializeBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                            for (Purchase purchase : purchases) {
                                handlePurchase(purchase);
                            }
                        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                            Toast.makeText(SubscriptionActivity.this, "Purchase canceled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SubscriptionActivity.this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
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
        skuList.add("premium_3months_1");
        skuList.add("premium_month_1");
        skuList.add("premium_week_1");
        skuList.add("premium_plus_week_1");
        skuList.add("premium_plus_month_1");
        skuList.add("premium_plus_3months_1");

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();

        billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        for (SubscriptionItem item : subscriptionItems) {
                            if (skuDetails.getSku().equals(item.getProductId())) {
                                item.setSkuDetails(skuDetails);
                                break;
                            }
                        }
                    }
                    subscriptionAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error querying subscriptions: " + billingResult.getDebugMessage());
                }
            }
        });
    }

    private void startPurchase(SkuDetails skuDetails) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Toast.makeText(this, "Error starting purchase flow", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Toast.makeText(this, "Subscription purchased successfully!", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Subscription Activated", Toast.LENGTH_SHORT).show();
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Toast.makeText(this, "Purchase acknowledged", Toast.LENGTH_SHORT).show();
                        updateUserSubscriptionDetails(purchase);
                    }
                });
            } else {
                updateUserSubscriptionDetails(purchase);
            }
        }
    }

    private void updateUserSubscriptionDetails(Purchase purchase) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            long startDate = System.currentTimeMillis();
            long endDate = 0;
            boolean isPremium = false;
            boolean isPremiumPlus = false;
            String planType = "";

            if (purchase.getSkus().contains("premium")) {
                isPremium = true;
                isPremiumPlus=false;
                planType = getPlanTypeFromSku(String.valueOf(purchase.getSkus()));
                endDate = calculateEndDate(startDate, planType);
            } else if (purchase.getSkus().contains("premium_plus")) {
                isPremium = false;
                isPremiumPlus = true;
                planType = getPlanTypeFromSku(String.valueOf(purchase.getSkus()));
                endDate = calculateEndDate(startDate, planType);
            }
            userRef.child("p").setValue(isPremium);
            userRef.child("pp").setValue(isPremiumPlus);
            userRef.child("premiumStartDate").setValue(startDate);
            userRef.child("premiumEndDate").setValue(endDate);
        }
    }

    private String getPlanTypeFromSku(String sku) {
        if (sku.contains("week")) {
            return "week";
        } else if (sku.contains("month")) {
            return "month";
        } else if (sku.contains("3months")) {
            return "3months";
        }
        return "";
    }

    private long calculateEndDate(long startDate, String planType) {
        long endDate = startDate;

        switch (planType) {
            case "week":
                endDate += 7 * 24 * 60 * 60 * 1000L;
                break;
            case "month":
                endDate += 30 * 24 * 60 * 60 * 1000L;
                break;
            case "3months":
                endDate += 90 * 24 * 60 * 60 * 1000L;
                break;
        }
        return endDate;
    }


    private void handleSubscriptionSelection(int position) {
        Toast.makeText(this, "Selected: " + subscriptionItems.get(position).getPlanName(), Toast.LENGTH_SHORT).show();
        selectedSkuDetails = subscriptionItems.get(position).getSkuDetails();
        Log.d(TAG, "Subscription plan selected: " + subscriptionItems.get(position).getPlanName());
    }

    private void fetchUserDetails(String userId) {
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    nametext.setText("Hello " + user.getName() + ", become a premium member and enjoy the benefits below.");
                    Picasso.get().load(user.getImageurl()).into(userPic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SubscriptionActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSubscriptionItems() {
        subscriptionItems.clear();

        subscriptionItems.add(new SubscriptionItem(
                "Premium Week",
                "₹99",
                "• Access to premium features for 1 WEEK",
                "• Unlimited Chats with Matched and Unmatched Users\n• Add Multiple Images\n• Get Visited Profiles\n• Get Liked Profiles",
                "• Verify Profile",
                "• Check Out Profiles",
                "premium_week_1"
        ));

        subscriptionItems.add(new SubscriptionItem(
                "Premium Month",
                "₹299",
                "• Access to premium features for 1 WEEK",
                "• Unlimited Chats with Matched and Unmatched Users\n• Add Multiple Images\n• Get Visited Profiles\n• Get Liked Profiles",
                "• Verify Profile",
                "• Check Out Profiles",
                "premium_month_1"
        ));

        subscriptionItems.add(new SubscriptionItem(
                "Premium 3 Months",
                "₹799",
                "• Access to premium features for 1 WEEK",
                "• Unlimited Chats with Matched and Unmatched Users\n• Add Multiple Images\n• Get Visited Profiles\n• Get Liked Profiles",
                "• Verify Profile",
                "• Check Out Profiles",
                "premium_3months_1"
        ));

        subscriptionItems.add(new SubscriptionItem(
                "Premium Plus Week",
                "₹199",
                "• Access to premium plus features for 1 WEEK",
                "• Unlimited Chats with Matched and Unmatched Users\n• Add Multiple Images\n• Get Visited Profiles\n• Get Liked Profiles\n• Verify Profile",
                "• Check Out Profiles",
                "• Video & Voice Call Access",
                "premium_plus_week_1"
        ));

        subscriptionItems.add(new SubscriptionItem(
                "Premium Plus Month",
                "₹599",
                "• Access to premium plus features for 1 WEEK",
                "• Unlimited Chats with Matched and Unmatched Users\n• Add Multiple Images\n• Get Visited Profiles\n• Get Liked Profiles\n• Verify Profile",
                "• Check Out Profiles",
                "• Video & Voice Call Access",
                "premium_plus_month_1"
        ));

        subscriptionItems.add(new SubscriptionItem(
                "Premium Plus 3 Months",
                "₹1599",
                "• Access to premium plus features for 1 WEEK",
                "• Unlimited Chats with Matched and Unmatched Users\n• Add Multiple Images\n• Get Visited Profiles\n• Get Liked Profiles\n• Verify Profile",
                "• Check Out Profiles",
                "• Video & Voice Call Access",
                "premium_plus_3months_1"
        ));

        subscriptionAdapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    private void showPopupWithAnimation() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_enter));
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 10, 0);
        ImageView image = popupView.findViewById(R.id.imageView2);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
    private void scrollToSubscription(String planName) {
        int position = -1;
        for (int i = 0; i < subscriptionItems.size(); i++) {
            if (subscriptionItems.get(i).getPlanName().equalsIgnoreCase(planName)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            subscriptionRecyclerView.smoothScrollToPosition(position);
        }
    }
}
