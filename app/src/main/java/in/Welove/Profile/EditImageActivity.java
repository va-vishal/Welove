package in.Welove.Profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Arrays;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import in.Welove.BaseActivity;
import in.Welove.CustomDialog;
import in.Welove.Model.User;
import in.Welove.R;
import in.Welove.SubscriptionActivity;


public class EditImageActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView pic1, pic2, pic3, pic4, pic5, imageView1, imageView2, imageView3, imageView4, imageView5;
    private ImageView lock1, lock2;
    private ImageView remove1, remove2, remove3, remove4, remove5, pencil;
    private CardView card1, card2, card3, card4, card5;
    private Uri mediaUri;
    private ImageView currentImageView, currentPic, currentRemove; // Track the current ImageView being updated
    private CircleImageView profileimagep;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("uploads");
        auth = FirebaseAuth.getInstance();

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

        // Initialize views
        pencil = findViewById(R.id.pencil);
        profileimagep = findViewById(R.id.profileimagep);
        pic1 = findViewById(R.id.pic1);
        pic2 = findViewById(R.id.pic2);
        pic3 = findViewById(R.id.pic3);
        pic4 = findViewById(R.id.pic4);
        pic5 = findViewById(R.id.pic5);
        remove1 = findViewById(R.id.remove1);
        remove2 = findViewById(R.id.remove2);
        remove3 = findViewById(R.id.remove3);
        remove4 = findViewById(R.id.remove4);
        remove5 = findViewById(R.id.remove5);
        imageView1 = findViewById(R.id.imageview1);
        imageView2 = findViewById(R.id.imageview2);
        imageView3 = findViewById(R.id.imageview3);
        imageView4 = findViewById(R.id.imageview4);
        imageView5 = findViewById(R.id.imageview5);
        lock2 = findViewById(R.id.lock2);
        lock1 = findViewById(R.id.lock1);

        // Set initial visibility
        setInitialVisibility();

        // Initialize card views
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);

        // Set click listeners for card views
        card1.setOnClickListener(v -> {
            currentImageView = imageView1;
            currentPic = pic1;
            currentRemove = remove1;
            openGallery();
        });
        card2.setOnClickListener(v -> {
            currentImageView = imageView2;
            currentPic = pic2;
            currentRemove = remove2;
            openGallery();
        });
        card3.setOnClickListener(v -> {
            currentImageView = imageView3;
            currentPic = pic3;
            currentRemove = remove3;
            openGallery();
        });
        card4.setOnClickListener(v -> {
            checkPremiumStatus(() -> {
                currentImageView = imageView4;
                currentPic = pic4;
                currentRemove = remove4;
                openGallery();
            });
        });

        card5.setOnClickListener(v -> {
            checkPremiumStatus(() -> {
                currentImageView = imageView5;
                currentPic = pic5;
                currentRemove = remove5;
                openGallery();
            });
        });

        pencil.setOnClickListener(v -> {
            currentImageView = profileimagep;
            openGallery();
        });

        remove1.setOnClickListener(v -> removeImageFromDatabase("imageurl1"));
        remove2.setOnClickListener(v -> removeImageFromDatabase("imageurl2"));
        remove3.setOnClickListener(v -> removeImageFromDatabase("imageurl3"));
        remove4.setOnClickListener(v -> removeImageFromDatabase("imageurl4"));
        remove5.setOnClickListener(v -> removeImageFromDatabase("imageurl5"));

        List<String> phrasesToHighlight = Arrays.asList("picture and Account", "Fake", "Block Your Account Permanetly");
        highlightTextInTextView(R.id.text3, phrasesToHighlight, Color.RED);

        loadUserDetails();
    }

    private void setInitialVisibility() {
        pic1.setVisibility(View.VISIBLE);
        pic2.setVisibility(View.VISIBLE);
        pic3.setVisibility(View.VISIBLE);
        pic4.setVisibility(View.VISIBLE);
        pic5.setVisibility(View.VISIBLE);

        remove1.setVisibility(View.GONE);
        remove2.setVisibility(View.GONE);
        remove3.setVisibility(View.GONE);
        remove4.setVisibility(View.GONE);
        remove5.setVisibility(View.GONE);

        lock1.setVisibility(View.GONE);
        lock2.setVisibility(View.GONE);
    }

    private void loadUserDetails() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            Log.e("ProfileFragment", "User details fetched");
                            loadImage(user.getImageurl(), profileimagep);
                            updateImageView(user.getImageurl1(), imageView1, pic1, remove1);
                            updateImageView(user.getImageurl2(), imageView2, pic2, remove2);
                            updateImageView(user.getImageurl3(), imageView3, pic3, remove3);
                            updateImageView(user.getImageurl4(), imageView4, pic4, remove4);
                            updateImageView(user.getImageurl5(), imageView5, pic5, remove5);
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
        } else {
            Toast.makeText(EditImageActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImageView(String imageUrl, ImageView imageView, ImageView placeholder, ImageView removeButton) {
        if (imageUrl != null) {
            loadImage(imageUrl, imageView);
            imageView.setVisibility(View.VISIBLE);
            placeholder.setVisibility(View.GONE);
            removeButton.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.GONE);
        }
    }

    private void loadImage(String url, ImageView imageView) {
        if (url != null) {
            Picasso.get().load(url).placeholder(R.drawable.defaultimage).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.defaultimage);
        }
    }

    private void removeImageFromDatabase(String imageUrlField) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.child(imageUrlField).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditImageActivity.this, "Image removed successfully", Toast.LENGTH_SHORT).show();
                    updateUIAfterRemoval(imageUrlField);
                } else {
                    Toast.makeText(EditImageActivity.this, "Failed to remove image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditImageActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIAfterRemoval(String imageUrlField) {
        if (imageUrlField.equals("imageurl1")) {
            imageView1.setVisibility(View.GONE);
            pic1.setVisibility(View.VISIBLE);
            remove1.setVisibility(View.GONE);
        } else if (imageUrlField.equals("imageurl2")) {
            imageView2.setVisibility(View.GONE);
            pic2.setVisibility(View.VISIBLE);
            remove2.setVisibility(View.GONE);
        } else if (imageUrlField.equals("imageurl3")) {
            imageView3.setVisibility(View.GONE);
            pic3.setVisibility(View.VISIBLE);
            remove3.setVisibility(View.GONE);
        } else if (imageUrlField.equals("imageurl4")) {
            imageView4.setVisibility(View.GONE);
            pic4.setVisibility(View.VISIBLE);
            remove4.setVisibility(View.GONE);
            lock1.setVisibility(View.GONE);
        } else if (imageUrlField.equals("imageurl5")) {
            imageView5.setVisibility(View.GONE);
            pic5.setVisibility(View.VISIBLE);
            remove5.setVisibility(View.GONE);
            lock2.setVisibility(View.GONE);
        }
    }

    private void highlightTextInTextView(int textViewId, List<String> phrasesToHighlight, int color) {
        TextView textView = findViewById(textViewId);
        Spannable spannable = new SpannableString(textView.getText());

        for (String phrase : phrasesToHighlight) {
            int startIndex = spannable.toString().indexOf(phrase);
            while (startIndex >= 0) {
                int endIndex = startIndex + phrase.length();
                spannable.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = spannable.toString().indexOf(phrase, endIndex);
            }
        }

        textView.setText(spannable);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mediaUri = data.getData();
            CropImage.activity(mediaUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if (currentImageView != null) {
                    currentImageView.setImageURI(resultUri);
                    uploadImageToFirebase(resultUri);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Crop error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void uploadImageToFirebase(Uri uri) {
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            String imageUrl = downloadUrl.toString();
            FirebaseUser firebaseUser = auth.getCurrentUser();
            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                if (currentImageView == profileimagep) {
                    userRef.child("imageurl").setValue(imageUrl).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditImageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (currentImageView == imageView1) {
                    userRef.child("imageurl1").setValue(imageUrl).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditImageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (currentImageView == imageView2) {
                    userRef.child("imageurl2").setValue(imageUrl).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditImageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (currentImageView == imageView3) { // Assuming there's a third imageView (imageView3)
                    userRef.child("imageurl3").setValue(imageUrl).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditImageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (currentImageView == imageView4) { // Assuming there's a third imageView (imageView3)
                    userRef.child("imageurl4").setValue(imageUrl).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditImageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (currentImageView == imageView5) { // Assuming there's a third imageView (imageView3)
                    userRef.child("imageurl5").setValue(imageUrl).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditImageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(EditImageActivity.this, "No image view selected", Toast.LENGTH_SHORT).show();
                }
            }
        })).addOnFailureListener(e -> Toast.makeText(EditImageActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void checkPremiumStatus(Runnable onSuccess) {
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

                    // If the user is Premium or Premium Plus, proceed
                    if ((isPremium != null && isPremium) || (isPremiumPlus != null && isPremiumPlus)) {
                        onSuccess.run();
                    } else {
                        CustomDialog customDialog = new CustomDialog(EditImageActivity.this);
                        View.OnClickListener onSubscribeClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(EditImageActivity.this, SubscriptionActivity.class);
                                startActivity(intent);
                                customDialog.dismiss();
                            }
                        };
                        customDialog.show(
                                "Subscription Required",
                                "You must subscribe to a plan to upload images. Would you like to subscribe now?",
                                onSubscribeClickListener,
                                false // Don't hide the Cancel button
                        );
                        customDialog.setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog.dismiss(); // Close the dialog if the user chooses "Cancel"
                            }
                        });

                    }
                } else {
                    Toast.makeText(EditImageActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditImageActivity.this, "Error checking premium status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
