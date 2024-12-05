package in.Welove.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
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
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import in.Welove.Authentication.LoginActivity1;
import in.Welove.Hot_Subscription_Activity;
import in.Welove.Model.User;
import in.Welove.Profile.AboutActivity;
import in.Welove.Profile.EditImageActivity;
import in.Welove.Profile.HelpCenterActivity;
import in.Welove.Profile.LikeActivity;
import in.Welove.Profile.LikedActivity;
import in.Welove.Profile.PreferenceActivity;
import in.Welove.Profile.ProfileActivity;
import in.Welove.Profile.RecentPassesActivity;
import in.Welove.Profile.VisitsActivity;
import in.Welove.Profile.WalletActivity;
import in.Welove.R;
import in.Welove.SubscriptionActivity;

public class ProfileFragment extends BaseFragment {

    private CircleImageView profileImage;
    private TextView name, noOfLikes, noOfVisits,noOfMatches,noofpasses,noofliked,marqueeTextView;
    private Button  getpremium1,logout,hotProfiles,wallet;
    private CardView getPremiumButton;
    private CardView editImageCard, preferencesCard, matchedProfilesCard, recentPassedCard, aboutCard, walletCard,helpCenterCard, logoutCard;
    private LinearLayout like, visit,liked;
    private FirebaseUser currentUser;
    private List<User> userList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView membership;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profileimagep);
        name = view.findViewById(R.id.name);
        noOfLikes = view.findViewById(R.id.nooflike);
        noOfVisits = view.findViewById(R.id.noofvisits);
        noOfMatches = view.findViewById(R.id.noOfMatches);
        noofpasses = view.findViewById(R.id.noofpasses);
        getPremiumButton = view.findViewById(R.id.getpremium);
        logout = view.findViewById(R.id.logout);
        wallet=view.findViewById(R.id.wallet);
        like = view.findViewById(R.id.like);
        visit = view.findViewById(R.id.visit);
        editImageCard = view.findViewById(R.id.editImageCard);
        preferencesCard = view.findViewById(R.id.preferencesCard);
        matchedProfilesCard = view.findViewById(R.id.matchedProfilesCard);
        recentPassedCard = view.findViewById(R.id.recentPassedCard);
        aboutCard = view.findViewById(R.id.aboutCard);
        helpCenterCard = view.findViewById(R.id.helpCenterCard);
        walletCard=view.findViewById(R.id.walletCard);
        logoutCard = view.findViewById(R.id.logoutCard);


        hotProfiles = view.findViewById(R.id.hot_profiles);
        Drawable drawable = getResources().getDrawable(R.drawable.hot);
        drawable.setBounds(0, 0, dpToPx(32), dpToPx(32));
        hotProfiles.setPadding(hotProfiles.getPaddingLeft(), hotProfiles.getPaddingTop(), dpToPx(8) + hotProfiles.getPaddingRight(), hotProfiles.getPaddingBottom());
        hotProfiles.setCompoundDrawables(null, null, drawable, null);

        getpremium1=view.findViewById(R.id.getpremium1);
        Drawable drawable1 = getResources().getDrawable(R.drawable.premium);
        drawable1.setBounds(0, 0, dpToPx(35), dpToPx(35));
        getpremium1.setPadding(getpremium1.getPaddingLeft(), getpremium1.getPaddingTop(), dpToPx(8) + getpremium1.getPaddingRight(), getpremium1.getPaddingBottom());
        getpremium1.setCompoundDrawables(null, null, drawable1, null);

        marqueeTextView = view.findViewById(R.id.marqueeTextView);
        liked=view.findViewById(R.id.liked);
        noofliked=view.findViewById(R.id.noofliked);
        membership=view.findViewById(R.id.membership);

        membership.setVisibility(View.GONE);

        sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userList = new ArrayList<>();

        getpremium1.setOnClickListener(v -> {
           Intent intent=new Intent(getContext(), SubscriptionActivity.class);
           startActivity(intent);
        });
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            fetchUserProfile();
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity1.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        }
        setupButtonListeners();
    }
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void fetchUserProfile() {
        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        Log.e("ProfileFragment", "User details fetched");
                        name.setText(user.getName());
                        Picasso.get().load(user.getImageurl()).into(profileImage);
                        String endDate = user.getPremiumEndDate();

                        Boolean premium = user.getP();
                        Boolean premiump = user.getPp();

                        if (premium == null) premium = false;
                        if (premiump == null) premiump = false;
                        if (endDate.isEmpty() && !premium && !premiump) {
                            marqueeTextView.setText("Get Premium and Enjoy the benefits and stay Connect With your loved ones");
                            marqueeTextView.setSelected(true);
                        } else if (!endDate.isEmpty()) {
                            if (premium) {
                                marqueeTextView.setText("Welcome Back premium user, enjoy the benefits of premium membership. Your premium membership will end on " + endDate);
                                marqueeTextView.setSelected(true);
                                membership.setVisibility(View.VISIBLE);
                                membership.setImageResource(R.drawable.premium);
                            } else if (premiump) {
                                marqueeTextView.setText("Welcome Back premiumPlus user, enjoy the benefits of premiumPlus membership. Your premiumPlus membership will end on " + endDate);
                                marqueeTextView.setSelected(true);
                                membership.setVisibility(View.VISIBLE);
                                membership.setImageResource(R.drawable.vip);
                            }
                        }
                        Map<String, Boolean> likesMap = user.getLikesList();
                        noOfLikes.setText(likesMap != null ? String.valueOf(likesMap.size()) : "0");

                        Map<String, Boolean> visitsMap = user.getVisitsList();
                        noOfVisits.setText(visitsMap != null ? String.valueOf(visitsMap.size()) : "0");

                        Map<String, Boolean> matchesMap = user.getMatches();
                        noOfMatches.setText(matchesMap != null ? String.valueOf(matchesMap.size()) : "0");

                        Map<String, Boolean> recentpassesMap = user.getDislikedList();
                        noofpasses.setText(recentpassesMap != null ? String.valueOf(recentpassesMap.size()) : "0");

                        Map<String, Boolean> LikedMap = user.getLikedList();
                        noofliked.setText(LikedMap != null ? String.valueOf(LikedMap.size()) : "0");

                    } else {
                        Log.e("ProfileFragment", "User object is null");
                    }
                } else {
                    Log.e("ProfileFragment", "No data available for the user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }
    private void setupButtonListeners() {

        logout.setOnClickListener(v -> logoutUser());
        logoutCard.setOnClickListener(v -> logoutUser());

        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LikedActivity.class);
                startActivity(intent);
            }
        });
        like.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LikeActivity.class);
            startActivity(intent);
        });
        visit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VisitsActivity.class);
            startActivity(intent);
        });
        noOfLikes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LikeActivity.class);
            startActivity(intent);
        });
        noOfVisits.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VisitsActivity.class);
            startActivity(intent);

        });
        wallet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WalletActivity.class);
            startActivity(intent);
        });
        noOfMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MatchedFragment matchedFragment = new MatchedFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, matchedFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        noofliked.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LikedActivity.class);
            startActivity(intent);
        });
        noofpasses.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecentPassesActivity.class);
            startActivity(intent);
        });
        editImageCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditImageActivity.class);
            startActivity(intent);
        });
        preferencesCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PreferenceActivity.class);
            startActivity(intent);
        });
        matchedProfilesCard.setOnClickListener(v -> {
            MatchedFragment matchedFragment = new MatchedFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.fragment_container, matchedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        recentPassedCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecentPassesActivity.class);
            startActivity(intent);
        });
        aboutCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });
        walletCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Hot_Subscription_Activity.class);
            startActivity(intent);
        });
        hotProfiles.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Hot_Subscription_Activity.class);
            startActivity(intent);
        });
        helpCenterCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HelpCenterActivity.class);
            startActivity(intent);
        });

    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), "See you soon!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
