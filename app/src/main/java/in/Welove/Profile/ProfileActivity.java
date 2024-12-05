package in.Welove.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import in.Welove.BaseActivity;
import in.Welove.CustomDialog;
import in.Welove.FCM.FcmNotificationSender;
import in.Welove.HomeActivity;
import in.Welove.Message.messagingActivity;
import in.Welove.Model.Notification;
import in.Welove.Model.User;
import in.Welove.R;
import in.Welove.SubscriptionActivity;


public class ProfileActivity extends BaseActivity {

    private TextView name, age, locationArea, locationState, jobType, educationText, genderText, motherTongueText, dobText, maritalText, descText, prefText;
    private ImageView premimage,locationImage, backImage, prefImage, jobImage, educationImage, genderImage, dobImage, maritalImage, descImage;
    private CardView cardImage1, cardImage2, cardImage3, cardImage4, cardImage5, cardImage6, backCard;
    private RoundedImageView image1, image2, image3, image4, image5, image6;
    private FloatingActionButton dislikeButton, likeButton, messageButton;
    private LinearLayout detailsContainer;
    private FirebaseUser firebaseUser;
    private String profileid;
    private ArrayList<String> likesd = new ArrayList<>();
    private ArrayList<String> disliked = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("publisherid")) {
            profileid = intent.getStringExtra("publisherid");
        } else {
            if (firebaseUser != null) {
                profileid = firebaseUser.getUid();
            }
        }
        initializeViews();
        fetchUserDetails();
        initializeListeners();

    }

    private void initializeViews() {
        detailsContainer = findViewById(R.id.detalis2);
        dislikeButton = findViewById(R.id.dislike_buttonp);
        likeButton = findViewById(R.id.like_buttonp);
        messageButton = findViewById(R.id.messagep);
        backCard = findViewById(R.id.backCard);
        backImage = findViewById(R.id.back_image);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        locationArea = findViewById(R.id.locationarea);
        locationState = findViewById(R.id.locationstate);
        jobType = findViewById(R.id.jobType);
        educationText = findViewById(R.id.educationtext);
        genderText = findViewById(R.id.gendertext);
        motherTongueText = findViewById(R.id.mother_tonguetext);
        dobText = findViewById(R.id.dobtext);
        maritalText = findViewById(R.id.maritaltext);
        descText = findViewById(R.id.desctext);
        prefText = findViewById(R.id.preftext);

        locationImage = findViewById(R.id.locationimage);
        jobImage = findViewById(R.id.jobImage);
        educationImage = findViewById(R.id.educationimage);
        genderImage = findViewById(R.id.genderImage);
        dobImage = findViewById(R.id.dobimage);
        maritalImage = findViewById(R.id.maritalimage);
        descImage = findViewById(R.id.descImage);
        prefImage = findViewById(R.id.prefimage);
        premimage=findViewById(R.id.premimage);

        cardImage1 = findViewById(R.id.card);
        cardImage2 = findViewById(R.id.cardImage2);
        cardImage3 = findViewById(R.id.cardImage3);
        cardImage4 = findViewById(R.id.cardImage4);
        cardImage5 = findViewById(R.id.cardImage5);
        cardImage6 = findViewById(R.id.cardImage6);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);

        setImageClickListeners();
        setActionButtonListeners();

    }

    private void initializeListeners() {
        DatabaseReference buttonref = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(firebaseUser.getUid());

        // Listener for likedList
        buttonref.child("likedList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likesd.clear(); // Clear the previous list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    likesd.add(snapshot.getKey()); // Add each liked post ID to the list
                }
                // Call updateButtonStates after loading likedList
                updateButtonStates(profileid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading likedList: ", databaseError.toException());
            }
        });

        // Listener for dislikedList
        buttonref.child("dislikedList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                disliked.clear(); // Clear the previous list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    disliked.add(snapshot.getKey()); // Add each disliked post ID to the list
                }
                // Call updateButtonStates after loading dislikedList
                updateButtonStates(profileid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error loading dislikedList: ", databaseError.toException());
            }
        });
    }

    // Method to update button states
    private void updateButtonStates(String otherUserId) {
        boolean isLiked = likesd.contains(otherUserId); // Check if the user is liked
        boolean isDisliked = disliked.contains(otherUserId); // Check if the user is disliked

        // Update the like button state
        if (isLiked) {
            likeButton.setImageResource(R.drawable.liked); // Update to the liked drawable
        } else {
            likeButton.setImageResource(R.drawable.heart); // Update to the unliked drawable
        }

        // Update the dislike button state
        if (isDisliked) {
            dislikeButton.setImageResource(R.drawable.disliked); // Update to the disliked drawable
        } else {
            dislikeButton.setImageResource(R.drawable.dislike); // Update to the undisliked drawable
        }
    }


    private void setActionButtonListeners() {
        messageButton.setOnClickListener(v -> startMessagingActivity());
        backCard.setOnClickListener(v -> finish());
        backImage.setOnClickListener(v -> finish());
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDislikeClick(profileid);
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(currentUserId);

                // Check if the current user is Premium or PremiumPlus
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Boolean isPremium = dataSnapshot.child("p").getValue(Boolean.class);
                            Boolean isPremiumPlus = dataSnapshot.child("pp").getValue(Boolean.class);

                            // If the user is Premium or Premium Plus, allow them to send a message
                            if ((isPremium != null && isPremium) || (isPremiumPlus != null && isPremiumPlus)) {
                                handleLikeClick(profileid);
                            }
                            // If not premium, check if the other user has liked the current user
                            else {
                                DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(profileid)  // The other user's likedList
                                        .child("matches");

                                matchRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            handleLikeClick(profileid);
                                        }
                                        else {
                                            CustomDialog customDialog = new CustomDialog(ProfileActivity.this);
                                            customDialog.show(
                                                    "Subscription Required",
                                                    "You must subscribe to a plan to message this user. Would you like to subscribe now?",
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(ProfileActivity.this, SubscriptionActivity.class);
                                                            startActivity(intent);
                                                            customDialog.dismiss(); // Dismiss the dialog after the action
                                                        }
                                                    },
                                                    false // Set to false to show the Cancel button
                                            );
                                            customDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    customDialog.dismiss(); // Dismiss the dialog when the user clicks "Cancel"
                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(ProfileActivity.this, "Error checking liked list.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Unable to check premium status. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity.this, "Error checking premium status.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setImageClickListeners() {
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image1, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image1, imageUrl);
                    }
                });
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image2, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image2, imageUrl);
                    }
                });
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image3, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image3, imageUrl);
                    }
                });
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image4, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image4, imageUrl);
                    }
                });
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image5, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image5, imageUrl);
                    }
                });
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageUrl(image6, new ImageUrlCallback() {
                    @Override
                    public void onImageUrlRetrieved(String imageUrl) {
                        showFullScreenImage(image6, imageUrl);
                    }
                });
            }
        });

    }

    private void getImageUrl(ImageView imageView, ImageUrlCallback callback) {
        if (profileid != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(profileid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User userProfile = dataSnapshot.getValue(User.class);
                        if (userProfile != null) {
                            String imageUrl = null;
                            if (imageView == image1) {
                                imageUrl = userProfile.getImageurl();
                            } else if (imageView == image2) {
                                imageUrl = userProfile.getImageurl1();
                            } else if (imageView == image3) {
                                imageUrl = userProfile.getImageurl2();
                            } else if (imageView == image4) {
                                imageUrl = userProfile.getImageurl3();
                            } else if (imageView == image5) {
                                imageUrl = userProfile.getImageurl4();
                            } else if (imageView == image6) {
                                imageUrl = userProfile.getImageurl5();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                            callback.onImageUrlRetrieved(imageUrl);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        }
    }

    public interface ImageUrlCallback {
        void onImageUrlRetrieved(String imageUrl);
    }


    private void fetchUserDetails() {
        if (profileid != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(profileid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User userProfile = dataSnapshot.getValue(User.class);
                        if (userProfile != null) {
                            populateUserProfile(userProfile);
                            loadImage(userProfile);
                            updateButtonStates(profileid);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadImage(User userProfile) {

        loadImageForMaritalStatus(userProfile.getMaritalStatus());
        loadImageForJob(userProfile.getJobType());
        loadImageForEducation(userProfile.getEducationLevel());
        loadImageForGender(userProfile.getGender());
        loadImageForPrefGender(userProfile.getPrefGender());
        loadImageForCondition(dobImage, userProfile.getDob(), R.drawable.calendar_month);
        loadImageForCondition(descImage, userProfile.getDescription(), R.drawable.search);

        Boolean isPremium = userProfile.getP();
        Boolean isPremiumPlus = userProfile.getPp();

        if (isPremium != null && isPremium) {
            premimage.setImageResource(R.drawable.premium);
            premimage.setVisibility(View.VISIBLE);
        } else if (isPremiumPlus != null && isPremiumPlus) {
            premimage.setImageResource(R.drawable.vip);
            premimage.setVisibility(View.VISIBLE);
        } else {
            premimage.setVisibility(View.GONE);
        }

    }
    private void populateUserProfile(User userProfile) {
        if (userProfile.getName() != null) name.setText(userProfile.getName());
        if (userProfile.getAge() != null) age.setText(userProfile.getAge());
        if (userProfile.getGender() != null) genderText.setText(userProfile.getGender());
        if (userProfile.getEducationLevel() != null)
            educationText.setText(userProfile.getEducationLevel());
        if (userProfile.getJobType() != null) jobType.setText(userProfile.getJobType());
        if (userProfile.getMotherTongue() != null)
            motherTongueText.setText(userProfile.getMotherTongue());
        if (userProfile.getDob() != null) dobText.setText(userProfile.getDob());
        if (userProfile.getMaritalStatus() != null)
            maritalText.setText(userProfile.getMaritalStatus());
        if (userProfile.getDescription() != null) descText.setText(userProfile.getDescription());

        if (userProfile.getArea() != null && userProfile.getState() != null) {
            locationArea.setText(userProfile.getArea());
            locationState.setText(userProfile.getState());
            detailsContainer.setVisibility(View.VISIBLE);
            if (!isFinishing()) {
                Glide.with(this).load(R.drawable.location).into(locationImage);
            }
        } else {
            detailsContainer.setVisibility(View.GONE);
        }

        if (userProfile.getPrefGender() != null) prefText.setText(userProfile.getPrefGender());

        loadProfileImages(userProfile);
        loadDynamicIcons(userProfile);
    }

    private void loadProfileImages(User userProfile) {
        loadImageIfExists(cardImage1, image1, userProfile.getImageurl());
        loadImageIfExists(cardImage2, image2, userProfile.getImageurl1());
        loadImageIfExists(cardImage3, image3, userProfile.getImageurl2());
        loadImageIfExists(cardImage4, image4, userProfile.getImageurl3());
        loadImageIfExists(cardImage5, image5, userProfile.getImageurl4());
        loadImageIfExists(cardImage6, image6, userProfile.getImageurl5());
    }

    private void loadImageIfExists(CardView cardView, ImageView imageView, String imageUrl) {
        if (imageUrl != null && !isFinishing()) {
            cardView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUrl).into(imageView);
        } else {
            cardView.setVisibility(View.GONE);
        }
    }

    private void loadDynamicIcons(User userProfile) {
        setIconIfAvailable(jobImage, userProfile.getJobType());
        setIconIfAvailable(educationImage, userProfile.getEducationLevel());
        setIconIfAvailable(genderImage, userProfile.getGender());
        setIconIfAvailable(dobImage, userProfile.getDob());
        setIconIfAvailable(maritalImage, userProfile.getMaritalStatus());
        setIconIfAvailable(descImage, userProfile.getDescription());
        setIconIfAvailable(prefImage, userProfile.getPrefGender());
    }

    private void setIconIfAvailable(ImageView imageView, String value) {
        imageView.setVisibility(value != null ? View.VISIBLE : View.GONE);
    }

    private void startMessagingActivity() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId);

        // Check if the current user is Premium or PremiumPlus
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isPremium = dataSnapshot.child("p").getValue(Boolean.class);
                    Boolean isPremiumPlus = dataSnapshot.child("pp").getValue(Boolean.class);

                    // If the user is Premium or Premium Plus, allow them to send a message
                    if ((isPremium != null && isPremium) || (isPremiumPlus != null && isPremiumPlus)) {
                        Intent intent = new Intent(ProfileActivity.this, messagingActivity.class);
                        intent.putExtra("userid", profileid); // assuming profileid is the recipient's user ID
                        startActivity(intent);
                    }
                    // If not premium, check if the other user has liked the current user
                    else {
                        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference()
                                .child("users")
                                .child(profileid)  // The other user's likedList
                                .child("matches");

                        matchRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // If the current user is in the other user's liked list
                                if (dataSnapshot.exists()) {
                                    Intent intent = new Intent(ProfileActivity.this, messagingActivity.class);
                                    intent.putExtra("userid", profileid);  // Ensure `profileid` is properly passed
                                    startActivity(intent);
                                }
                                // If the current user is not in the liked list
                                else {
                                    CustomDialog customDialog = new CustomDialog(ProfileActivity.this);
                                    customDialog.show(
                                            "Subscription Required",
                                            "You must subscribe to a plan to message this user. Would you like to subscribe now?",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(ProfileActivity.this, SubscriptionActivity.class);
                                                    startActivity(intent);
                                                    customDialog.dismiss();
                                                }
                                            },
                                            false
                                    );
                                    customDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            customDialog.dismiss(); // Dismiss the dialog when the user clicks "Cancel"
                                        }
                                    });

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ProfileActivity.this, "Error checking liked list.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Unable to check premium status. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error checking premium status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFullScreenImage(View rootView, String imageUrl) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_image, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int popupWidth = (int) (displayMetrics.widthPixels * 0.8); // 80% of screen width
        int popupHeight = (int) (displayMetrics.heightPixels * 0.8); // 80% of screen height

        PopupWindow popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);

        RoundedImageView popupImageView = popupView.findViewById(R.id.popupImageView);
        Glide.with(this).load(imageUrl).into(popupImageView);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.9f;
        getWindow().setAttributes(layoutParams);

        popupView.setOnClickListener(v -> {
            popupWindow.dismiss();
            WindowManager.LayoutParams layoutParams1 = getWindow().getAttributes();
            layoutParams1.alpha = 1.0f;
            getWindow().setAttributes(layoutParams1);
        });
    }

    private void loadImageForCondition(ImageView imageView, String condition, int drawableResId) {
        if (condition != null && imageView != null) {
            if (!isFinishing() && !isDestroyed()) {
                Glide.with(ProfileActivity.this).load(drawableResId).into(imageView);
            }
        }
    }

    private void loadImageForPrefGender(String prefGender) {
        if (prefGender != null && prefImage != null) {
            if (!isFinishing() && !isDestroyed()) {
                switch (prefGender) {
                    case "Female":
                        Glide.with(this).load(R.drawable.woman).into(prefImage);
                        break;
                    case "Male":
                        Glide.with(this).load(R.drawable.man).into(prefImage);
                        break;
                    case "Gay":
                        Glide.with(this).load(R.drawable.gay).into(prefImage);
                        break;
                    case "Lesbian":
                        Glide.with(this).load(R.drawable.lesbian).into(prefImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(prefImage);
                        break;
                }
            }
        }
    }

    private void loadImageForMaritalStatus(String maritalStatus) {
        if (maritalImage != null && maritalStatus != null) {
            int imageResId;
            switch (maritalStatus) {
                case "Single":
                    imageResId = R.drawable.single;
                    break;
                case "Married":
                    imageResId = R.drawable.married;
                    break;
                case "Single with child":
                    imageResId = R.drawable.singlewithchild;
                    break;
                case "Divorced":
                    imageResId = R.drawable.divorced;
                    break;
                case "Divorced with child":
                    imageResId = R.drawable.seperatedwithchild;
                    break;
                case "Widowed":
                    imageResId = R.drawable.widowed;
                    break;
                case "Widowed with child":
                    imageResId = R.drawable.widowedwithchild;
                    break;
                default:
                    imageResId = R.drawable.defaultimage;
                    break;
            }
            // Ensure the activity is not finishing or destroyed before loading the image
            if (!isFinishing() && !isDestroyed()) {
                Glide.with(this).load(imageResId).into(maritalImage);
            }
        }
    }

    // Methods to load images based on user details
    private void loadImageForJob(String job) {
        if (jobImage != null && job != null) {
            // Ensure the activity is not finishing or destroyed before loading the image
            if (!isFinishing() && !isDestroyed()) {
                switch (job) {
                    case "Student":
                        Glide.with(this).load(R.drawable.student).into(jobImage);
                        break;
                    case "Engineer":
                        Glide.with(this).load(R.drawable.engineer).into(jobImage);
                        break;
                    case "Doctor":
                        Glide.with(this).load(R.drawable.doctor).into(jobImage);
                        break;
                    case "Teacher":
                        Glide.with(this).load(R.drawable.teacher).into(jobImage);
                        break;
                    case "Artist":
                        Glide.with(this).load(R.drawable.guitar).into(jobImage);
                        break;
                    case "Manager":
                        Glide.with(this).load(R.drawable.manager).into(jobImage);
                        break;
                    case "Chef":
                        Glide.with(this).load(R.drawable.cooking).into(jobImage);
                        break;
                    case "Driver":
                        Glide.with(this).load(R.drawable.driver).into(jobImage);
                        break;
                    case "Pilot":
                        Glide.with(this).load(R.drawable.pilot).into(jobImage);
                        break;
                    case "Writer":
                        Glide.with(this).load(R.drawable.writer).into(jobImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(jobImage);
                        break;
                }
            }
        }
    }

    private void loadImageForEducation(String education) {
        if (education != null && educationImage != null) {
            // Ensure the activity is not finishing or destroyed before loading the image
            if (!isFinishing() && !isDestroyed()) {
                switch (education) {
                    case "High School":
                        Glide.with(this).load(R.drawable.school).into(educationImage);
                        break;
                    case "Bachelors":
                        Glide.with(this).load(R.drawable.bachelors).into(educationImage);
                        break;
                    case "Engineering":
                        Glide.with(this).load(R.drawable.bachelors).into(educationImage);
                        break;
                    case "Masters":
                        Glide.with(this).load(R.drawable.masters).into(educationImage);
                        break;
                    case "Phd":
                        Glide.with(this).load(R.drawable.phd).into(educationImage);
                        break;
                    case "Others":
                        Glide.with(this).load(R.drawable.phd).into(educationImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(educationImage);
                        break;
                }
            }
        }
    }

    private void loadImageForGender(String gender) {
        if (gender != null && genderImage != null) {
            if (!isFinishing() && !isDestroyed()) {
                switch (gender) {
                    case "Female":
                        Glide.with(this).load(R.drawable.woman).into(genderImage);
                        break;
                    case "Male":
                        Glide.with(this).load(R.drawable.man).into(genderImage);
                        break;
                    case "Gay":
                        Glide.with(this).load(R.drawable.gay).into(genderImage);
                        break;
                    case "Lesbian":
                        Glide.with(this).load(R.drawable.lesbian).into(genderImage);
                        break;
                    default:
                        Glide.with(this).load(R.drawable.defaultimage).into(genderImage);
                        break;
                }
            }
        }
    }


    private void handleLikeClick(String otherUserId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (likesd.contains(otherUserId)) {
            likesd.remove(otherUserId);
            removeFromLikedList(otherUserId);
            removeMatchFromFirebase(otherUserId);
        } else if (disliked.contains(otherUserId)) {
            Toast.makeText(ProfileActivity.this, "Undo dislike before liking", Toast.LENGTH_SHORT).show();
            return;
        } else {
            likesd.add(otherUserId);
            addToLikedList(otherUserId);
            sendUserActionNotification(otherUserId, "like");
            addNotification(otherUserId, "liked", "You have a new like", "Someone has liked your profile");
            checkForMatch(otherUserId, currentUserId);
        }
    }

    private void removeMatchFromFirebase(String user) {
        String userId = user;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("matches");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("matches");

        currentUserRef.child(userId).removeValue();
        userRef.child(currentUserId).removeValue();
    }

    private void addToLikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("users").child(currentUserId).child("likedList").child(userId).setValue(true);
        dbRef.child("users").child(userId).child("likesList").child(currentUserId).setValue(true);
    }

    private void removeFromLikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("users").child(currentUserId).child("likedList").child(userId).removeValue();
        dbRef.child("users").child(userId).child("likesList").child(currentUserId).removeValue();
    }

    private void handleDislikeClick(String otherUserId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (disliked.contains(otherUserId)) {
            disliked.remove(otherUserId);
            removeFromDislikedList(otherUserId);
        } else if (likesd.contains(otherUserId)) {
            Toast.makeText(ProfileActivity.this, "Undo like before disliking", Toast.LENGTH_SHORT).show();
            return;
        } else {
            disliked.add(otherUserId);
            addToDislikedList(otherUserId);
        }
    }

    private void addToDislikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("dislikedList")
                .child(userId).setValue(true);
    }

    private void removeFromDislikedList(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUserId)
                .child("dislikedList")
                .child(userId).removeValue();
    }

    private void sendUserActionNotification(String userId, String actionType) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }

        String currentUserId = currentUser.getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }

                String currentUserName = currentUser.getName(); // Assuming you have a getName() method
                String notificationBody = generateNotificationMessage(currentUserName, actionType);

                // Fetch the FCM token of the user you're sending the notification to
                DatabaseReference userReference1 = FirebaseDatabase.getInstance().getReference("users").child(userId).child("fcmToken");
                userReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Retrieve the FCM token
                        String userToken = dataSnapshot.getValue(String.class);
                        if (userToken == null || userToken.isEmpty()) {
                            Log.e("Notification", "FCM token is null or empty");
                            return;
                        }

                        // Send the notification using FCM
                        FcmNotificationSender fcmNotificationSender = new FcmNotificationSender(
                                userToken,
                                "Notification",  // Title of the notification
                                notificationBody,  // Message body
                                ProfileActivity.this  // Activity or Context for reference
                        );
                        fcmNotificationSender.sendNotification();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Notification", "Error retrieving FCM token: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Firebase error: " + databaseError.getMessage());
            }
        });
    }


    private String generateNotificationMessage(String userName, String actionType) {
        switch (actionType) {
            case "like":
                return userName + " liked your profile.";
            case "visit":
                return userName + " visited your profile.";
            case "match":
                return "It's a match with " + userName + "!";
            default:
                return "New notification from " + userName;
        }
    }

    private void checkForMatch(String otherUserId, String currentUserId) {
        // Reference to the "likedList" of the user being checked
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(otherUserId)
                .child("likedList");

        likesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sendUserActionNotification(otherUserId, "match");
                    addNotification(otherUserId, "match", "It's a match!", "You have found a match");
                    matchNotification(otherUserId, "match", "It's a match!", "You have found a match");
                    // Both users have liked each other, so it's a match
                    LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
                    View dialogView = inflater.inflate(R.layout.dialog_custom_match, null);

                    // Find the RoundedImageView within the inflated view
                    RoundedImageView currentUserImage = dialogView.findViewById(R.id.currentuserimage);
                    RoundedImageView otherUserImage = dialogView.findViewById(R.id.otheruserimage);
                    Button Button = dialogView.findViewById(R.id.startchat);
                    TextView keepsearching = dialogView.findViewById(R.id.keepsearching);
                    TextView textView = dialogView.findViewById(R.id.textView);

                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(currentUserId)
                            .child("imageurl");

                    currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String currentUserImageUrl = snapshot.getValue(String.class);
                                if (currentUserImageUrl != null) {
                                    // Load the current user image using Glide
                                    Glide.with(ProfileActivity.this)
                                            .load(currentUserImageUrl)
                                            .into(currentUserImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    DatabaseReference otherUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(otherUserId)
                            .child("imageurl");

                    otherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String otherUserImageUrl = snapshot.getValue(String.class);
                                if (otherUserImageUrl != null) {
                                    // Load the other user image using Glide
                                    Glide.with(ProfileActivity.this)
                                            .load(otherUserImageUrl)
                                            .into(otherUserImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            Log.e("ImageLoad", "Database error: " + error.getMessage());
                        }
                    });
                    if (ProfileActivity.this instanceof Activity && !((Activity) ProfileActivity.this).isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        dialog.show();

                        keepsearching.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ProfileActivity.this, messagingActivity.class);
                                intent.putExtra("userid", otherUserId);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Log.e("UserAdapter", "Activity is finishing or context is not valid, cannot show dialog.");
                    }

                    saveMatchesToFirebase(otherUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                Log.e("MatchCheck", "Database error: " + error.getMessage());
            }
        });
    }
    private void matchNotification(String targetedUserId, String notificationType, String title, String body) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }

        String currentUserId = currentUser.getUid();

        if (targetedUserId == null || targetedUserId.isEmpty()) {
            Log.e("Notification", "Targeted user ID is invalid");
            return;
        }

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(targetedUserId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }

                String otherUsername = user.getName();
                String personalizedBody;

                switch (notificationType) {
                    case "match":
                        personalizedBody = String.format("You have found amatch with %s ", otherUsername);
                        break;
                    default:
                        personalizedBody = body; // Use the default body if type doesn't match
                        break;
                }

                DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(currentUserId)
                        .child("Notifications")
                        .push(); // Generate a unique ID for the notification

                // Create a Notification object and set properties
                Notification notification = new Notification();
                notification.setNotificationId(notificationRef.getKey());
                notification.setTitle(title);
                notification.setBody(personalizedBody);
                notification.setNotificationType(notificationType);
                notification.setTimestamp(System.currentTimeMillis());
                notification.setSeen(false);
                notification.setOtheruserid(currentUserId);
                notification.setCurrentuserid(targetedUserId);

                // Push the notification to Firebase
                notificationRef.setValue(notification).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notification", "Notification recorded successfully");
                    } else {
                        Log.e("Notification", "Failed to record notification", task.getException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to fetch current user details", databaseError.toException());
            }
        });
    }
    private void saveMatchesToFirebase(String user) {
        String userId = user;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("matches");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("matches");

        currentUserRef.child(userId).setValue(true);
        userRef.child(currentUserId).setValue(true);
    }

    private void addNotification(String targetedUserId, String notificationType, String title, String body) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e("Notification", "Current user is null");
            return;
        }

        String currentUserId = currentUser.getUid();

        if (targetedUserId == null || targetedUserId.isEmpty()) {
            Log.e("Notification", "Targeted user ID is invalid");
            return;
        }

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    Log.e("Notification", "Current user data is null");
                    return;
                }

                String currentUsername = currentUser.getName();
                String personalizedBody;

                switch (notificationType) {
                    case "liked":
                        personalizedBody = String.format("%s has liked your profile", currentUsername);
                        break;
                    case "visited":
                        personalizedBody = String.format("%s has visited your profile", currentUsername);
                        break;
                    case "match":
                        personalizedBody = String.format("You and %s have a new match!", currentUsername);
                        break;
                    default:
                        personalizedBody = body; // Use the default body if type doesn't match
                        break;
                }

                DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(targetedUserId)
                        .child("Notifications")
                        .push(); // Generate a unique ID for the notification

                // Create a Notification object and set properties
                Notification notification = new Notification();
                notification.setNotificationId(notificationRef.getKey());
                notification.setTitle(title);
                notification.setBody(personalizedBody);
                notification.setNotificationType(notificationType);
                notification.setTimestamp(System.currentTimeMillis());
                notification.setSeen(false);
                notification.setOtheruserid(targetedUserId);
                notification.setCurrentuserid(currentUserId);

                // Push the notification to Firebase
                notificationRef.setValue(notification).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notification", "Notification recorded successfully");
                    } else {
                        Log.e("Notification", "Failed to record notification", task.getException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Failed to fetch current user details", databaseError.toException());
            }
        });
    }
}