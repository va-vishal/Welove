package in.Welove.Authentication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.Welove.Adapter.CustomSpinnerAdapter;
import in.Welove.BaseActivity;
import in.Welove.CustomDialog;
import in.Welove.Custome.CustomDatePickerBottomSheet;
import in.Welove.Custome.CustomMessageDialog;
import in.Welove.R;


public class DOBActivity extends BaseActivity {
    private static final String TAG = "DobActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private EditText dobEditText,nameEditText,descriptionEditText;
    private Button saveButton,allowLocationButton;
    private FusedLocationProviderClient fusedLocationClient;
    private String selectedDOB;
    private int userAge;
    TextView age;
    ImageView plus,imageview;
    FirebaseAuth auth;
    CardView cardView,cardView2;
    Spinner gender_type,education_level,jobtype,motherToungue,maritgal_status,prefGender_type;
    FirebaseDatabase database;
    private Uri mediaUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String area,state;
    private double longitude,latitude;
    private AlertDialog loadingDialog;
    private Handler handler = new Handler();
    private Runnable timeoutRunnable;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dobactivity);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
             userId = user.getUid();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        cardView = findViewById(R.id.cardView);
        cardView2 = findViewById(R.id.cardView2);

        plus = findViewById(R.id.plus);
        imageview = findViewById(R.id.imageview);
        allowLocationButton = findViewById(R.id.location);
        descriptionEditText = findViewById(R.id.description);
        age = findViewById(R.id.age);
        nameEditText = findViewById(R.id.name);
        gender_type = findViewById(R.id.gender);
        education_level = findViewById(R.id.educationlevel);
        jobtype = findViewById(R.id.jobtype);
        motherToungue = findViewById(R.id.motherToungue);
        prefGender_type = findViewById(R.id.prefGender);
        maritgal_status = findViewById(R.id.maritgal_status);
        dobEditText = findViewById(R.id.dobEditText);
        saveButton = findViewById(R.id.buttonNext);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        cardView.setOnClickListener(v -> showMediaPickerDialog());
        plus.setOnClickListener(v -> showMediaPickerDialog());

        allowLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    getCurrentLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePickerBottomSheet dialog = new CustomDatePickerBottomSheet(DOBActivity.this, new CustomDatePickerBottomSheet.OnDateSetListener() {
                    @Override
                    public void onDateSet(int day, int month, int year) {
                        selectedDOB = day + "/" + (month + 1) + "/" + year;
                        dobEditText.setText(selectedDOB);
                        userAge = calculateAge(day, month, year);
                        if (userAge >= 18) {
                            System.out.println("User's age is: " + userAge);
                            age.setText("Age : " + userAge);
                        } else {
                            showCustomMessageDialog("Only 18 years old or above are allowed to create an account!!");
                        }
                    }
                });
                dialog.show();
            }
        });
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePickerBottomSheet dialog = new CustomDatePickerBottomSheet(DOBActivity.this, new CustomDatePickerBottomSheet.OnDateSetListener() {
                    @Override
                    public void onDateSet(int day, int month, int year) {
                        selectedDOB = day + "/" + (month + 1) + "/" + year;
                        dobEditText.setText(selectedDOB);
                        userAge = calculateAge(day, month, year);
                        if (userAge >= 18) {
                            System.out.println("User's age is: " + userAge);
                            age.setText("Age : " + userAge);
                            //updateUserInDatabase(selectedDOB, String.valueOf(userAge));
                        } else {
                            showCustomMessageDialog("Only 18 years old or above are allowed to create an account!!");
                        }
                    }
                });
                dialog.show();
            }
        });
        setupSpinner(gender_type, R.array.gender_options);
        setupSpinner(education_level, R.array.Educationlevel_options);
        setupSpinner(jobtype, R.array.jobtype_options);
        setupSpinner(motherToungue, R.array.mother_tongue_options);
        setupSpinner(maritgal_status, R.array.maritalstatus_options);
        setupSpinner(prefGender_type, R.array.pref_gender_options);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gather user profile data
                String name = nameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String gender = gender_type.getSelectedItem().toString();
                String educationLevel = education_level.getSelectedItem().toString();
                String jobType = jobtype.getSelectedItem().toString();
                String motherTongue = motherToungue.getSelectedItem().toString();
                String maritalStatus = maritgal_status.getSelectedItem().toString();
                String prefGender = prefGender_type.getSelectedItem().toString();
                String age = String.valueOf(userAge);
                Integer ageRange = 60, maxDistance = 500;

                Intent intent = getIntent();
                String email = intent.getStringExtra("email");
                String password = intent.getStringExtra("password");

                if (validateFields(name, description, gender, educationLevel, jobType, motherTongue, maritalStatus, prefGender)) {
                    // Create user profile data
                    double walletBalance = 0.0;
                    Map<String, Object> userProfile = new HashMap<>();
                    //userProfile.put("id", user.getUid());
                    userProfile.put("email", email);
                    userProfile.put("password", password);
                    userProfile.put("name", name);
                    userProfile.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/welove-f8e77.appspot.com/o/20241001_113735.png?alt=media&token=3d22facb-09c7-46f2-b971-3f680971d261");
                    userProfile.put("description", description);
                    userProfile.put("gender", gender);
                    userProfile.put("educationLevel", educationLevel);
                    userProfile.put("jobType", jobType);
                    userProfile.put("motherTongue", motherTongue);
                    userProfile.put("maritalStatus", maritalStatus);
                    userProfile.put("prefGender", prefGender);
                    userProfile.put("age", age);
                    userProfile.put("dob", selectedDOB);
                    userProfile.put("area", area);
                    userProfile.put("state", state);
                    userProfile.put("longitude", longitude);
                    userProfile.put("latitude", latitude);
                    userProfile.put("p", false);
                    userProfile.put("pp", false);
                    userProfile.put("hotStartDate", "");
                    userProfile.put("hotEndDate", "");
                    userProfile.put("hotProfieOrNormalProfile", false);
                    userProfile.put("hot", false);
                    userProfile.put("premiumStartDate", "");
                    userProfile.put("premiumEndDate", "");
                    userProfile.put("hideProfile", false);
                    userProfile.put("hideAge", false);
                    userProfile.put("hideLocation", false);
                    userProfile.put("hideName", false);
                    userProfile.put("maxDistance", maxDistance);
                    userProfile.put("ageRange", ageRange);
                    userProfile.put("walletBalance", walletBalance);

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                        reference.updateChildren(userProfile)
                                                .addOnSuccessListener(aVoid -> {
                                                    user.sendEmailVerification()
                                                            .addOnCompleteListener(emailTask -> {
                                                                if (emailTask.isSuccessful()) {
                                                                    uploadImage();
                                                                    Toast.makeText(DOBActivity.this, "Verification email sent! Please check your inbox.", Toast.LENGTH_SHORT).show();
                                                                    showSuccessDialog();
                                                                } else {
                                                                    Toast.makeText(DOBActivity.this, "Failed to send verification email: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(DOBActivity.this, "Failed to update user's profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                                    Toast.makeText(DOBActivity.this, "Failed to create user: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

    }
    private void showSuccessDialog() {
        CustomDialog customDialog = new CustomDialog(DOBActivity.this);
        customDialog.show(
                "Congratulations!",
                "Welcome to WeLove! Your account has been successfully created. Please verify your email to get started.",
                v -> {
                    customDialog.dismiss();
                    Intent homeIntent = new Intent(DOBActivity.this, LoginActivity1.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                    finish();
                },
                true // Hide the cancel button
        );
    }


    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Fetching your location...")
                .setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private boolean validateFields(String name, String description, String gender, String educationLevel, String jobType, String motherTongue, String maritalStatus, String prefGender) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please fill in your Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please fill in your Description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select your Gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (educationLevel.equals("Select Education Level")) {
            Toast.makeText(this, "Please select your Education Level", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (jobType.equals("Select Job Type")) {
            Toast.makeText(this, "Please select your Job Type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (motherTongue.equals("Select Mother Tongue")) {
            Toast.makeText(this, "Please select your Mother Tongue", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (maritalStatus.equals("Select Marital Status")) {
            Toast.makeText(this, "Please select your Marital Status", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (prefGender.equals("Select Preferred Gender")) {
            Toast.makeText(this, "Please select your Preferred Gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDOB == null || selectedDOB.isEmpty()) {
            Toast.makeText(this, "Please select your Date of Birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Make sure userAge is valid
        if (userAge < 18) {
            Toast.makeText(this, "You must be at least 18 years old to create an account", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true; // All validations passed
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        String[] options = getResources().getStringArray(arrayResId);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }
    private void getCurrentLocation() {
        showLoadingDialog();

        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
                Toast.makeText(DOBActivity.this, "Location retrieval timed out. Please try again.", Toast.LENGTH_SHORT).show();
            }
        };
        handler.postDelayed(timeoutRunnable, 10000);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hideLoadingDialog();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    hideLoadingDialog(); // Hide loading dialog
                    handler.removeCallbacks(timeoutRunnable); // Remove timeout

                    if (location != null) {
                        // Get latitude and longitude
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses.size() > 0) {
                                area = addresses.get(0).getLocality(); // Get area
                                state = addresses.get(0).getAdminArea(); // Get state
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(DOBActivity.this, "Location obtained", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DOBActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog(); // Hide loading dialog on failure
                    handler.removeCallbacks(timeoutRunnable); // Remove timeout
                    Toast.makeText(DOBActivity.this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        // Permission is already granted
        return true;
    }
    private void showCustomMessageDialog(String message) {
        DialogFragment dialog = new CustomMessageDialog(message);
        dialog.show(getSupportFragmentManager(), "CustomMessageDialog");
    }
    private int calculateAge(int day, int month, int year) {
        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Location permission is required to proceed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showMediaPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DOBActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_media_choice, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        AppCompatImageView buttonCamera = dialogView.findViewById(R.id.button_camera);
        AppCompatImageView buttonGallery = dialogView.findViewById(R.id.button_gallery);

        buttonCamera.setOnClickListener(v -> {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
            dialog.dismiss();
        });

        buttonGallery.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST || requestCode == REQUEST_IMAGE_CAPTURE) {
                handleImageSelectionOrCapture(requestCode, data);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                handleCroppedImageResult(data);
            } else {
                Toast.makeText(this, "Unknown request code: " + requestCode, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Action cancelled or failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageSelectionOrCapture(int requestCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            mediaUri = data.getData();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                mediaUri = getImageUri(DOBActivity.this, imageBitmap);
            }
        }
        if (mediaUri != null) {
            CropImage.activity(mediaUri)
                    .setAspectRatio(1, 1)  // Cropping to square aspect ratio
                    .start(this);
        } else {
            Toast.makeText(this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCroppedImageResult(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (result != null) {
            mediaUri = result.getUri();
            imageview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(mediaUri)
                    .into(imageview);
            uploadImage();
        } else {
            Toast.makeText(this, "Failed to get cropped image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String getFileExtension(Uri uri) {
        if (uri == null) return null;
        String extension;
        if (uri.getScheme().equals("content")) {
            extension = getContentResolver().getType(uri).split("/")[1];
        } else {
            extension = uri.getPath().substring(uri.getPath().lastIndexOf(".") + 1);
        }
        return extension;
    }

    private void uploadImage() {
        if (mediaUri != null) {
            String fileExtension = getFileExtension(mediaUri);
            if (fileExtension == null) {
                Toast.makeText(this, "Could not determine file extension", Toast.LENGTH_SHORT).show();
                return;
            }
            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + fileExtension);
            UploadTask uploadTask = reference.putFile(mediaUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    saveImageToDatabase(imageUrl);
                } else {
                    Toast.makeText(DOBActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e("ProfilePhotoActivity", "Upload failed: ", task.getException());
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(DOBActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfilePhotoActivity", "Upload failed: ", e);
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToDatabase(String imageurl) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("imageurl", imageurl);
            hashMap.put("id", user.getUid());

            reference.updateChildren(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DOBActivity.this, "Profile image uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DOBActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(DOBActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

}