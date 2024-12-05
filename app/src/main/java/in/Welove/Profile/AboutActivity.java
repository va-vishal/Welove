package in.Welove.Profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.Welove.BaseActivity;
import in.Welove.Custome.CustomDatePickerBottomSheet;
import in.Welove.Model.User;
import in.Welove.R;


public class AboutActivity extends BaseActivity {

    private DatabaseReference databaseReference;
    private TextView text,profile, prefgender,name, gender, age, dob, mail, education, job, mothertongue, maritalstatus, description;
    private String selectedDOB;
    private int userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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
        // Initialize TextViews
        text=findViewById(R.id.text);
        prefgender=findViewById(R.id.prefgender);
        profile=findViewById(R.id.profile);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        dob = findViewById(R.id.dob);
        mail = findViewById(R.id.mail);
        education = findViewById(R.id.education);
        job = findViewById(R.id.job);
        mothertongue = findViewById(R.id.mothertongue);
        maritalstatus = findViewById(R.id.maritalstatus);
        description = findViewById(R.id.descriptionTextView);

        List<String> phrasesToHighlight = Arrays.asList("picture and Account", "Fake","Block Your Account Permanetly");
        highlightTextInTextView(R.id.text, phrasesToHighlight, Color.RED);
        setOnClickListeners();


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AboutActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User userProfile = dataSnapshot.getValue(User.class);
                        if (userProfile != null) {
                            // Set data to TextViews
                            name.setText("Name : "+userProfile.name);
                            gender.setText("Gender : "+userProfile.gender);
                            age.setText("Age : "+userProfile.age);
                            dob.setText("Dob : "+userProfile.dob);
                            mail.setText("Mail : "+userProfile.email);
                            education.setText("Education : "+userProfile.educationLevel);
                            job.setText("Job : "+userProfile.jobType);
                            mothertongue.setText("MotherTongue : "+userProfile.motherTongue);
                            maritalstatus.setText("Marital Status : "+userProfile.maritalStatus);
                            description.setText("Description : "+userProfile.description);
                            prefgender.setText("Prefered Gender : "+userProfile.prefGender);
                            prefgender.setVisibility(userProfile.prefGender != null ? View.VISIBLE : View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                  // Toast.makeText(this,"ErrorOccurred",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void setOnClickListeners() {
        setClickListener(name, this::showNameDialog);
        setClickListener(gender, this::showGenderDialog);
        setClickListener(dob, this::showDobDialog);
        setClickListener(mothertongue, this::showMotherTongueDialog);
        setClickListener(education, this::showEducationDialog);
        setClickListener(job, this::showJobDialog);
        setClickListener(maritalstatus, this::showMaritalStatusDialog);
        setClickListener(description, this::showDescriptionDialog);
        setClickListener(prefgender, this::showPrefGenderDialog);
    }

    private void showPrefGenderDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_gender, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        RadioGroup radioGroupGender = dialogView.findViewById(R.id.radioGroupGender);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Populate RadioButtons with gender options
        populatePrefGenderOptions(radioGroupGender);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // Set the background color to transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                if (selectedRadioButton != null) {
                    String selectedGender = selectedRadioButton.getText().toString();
                    gender.setText(selectedGender);

                    // Update Firebase database
                    updatePrefGenderInDatabase(selectedGender);
                } else {
                    Toast.makeText(AboutActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
    private void populatePrefGenderOptions(RadioGroup radioGroup) {
        String[] genders = {"Male", "Female", "Gay", "Lesbian"};
        for (String gender : genders) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(gender);
            radioGroup.addView(radioButton);
        }
    }
    private void updatePrefGenderInDatabase(String gender) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(auth.getCurrentUser().getUid());

        HashMap<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("prefGender", gender);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AboutActivity.this, "prefGender updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AboutActivity.this, "Failed to update prefGender : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showMotherTongueDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_mothertongue, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        RadioGroup radioGroupMotherTongue = dialogView.findViewById(R.id.radioGroupMotherTongue);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        populateMotherTongueOptions(radioGroupMotherTongue);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            int selectedId = radioGroupMotherTongue.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
            if (selectedRadioButton != null) {
                String selectedMotherTongue = selectedRadioButton.getText().toString();
                Log.d("selectedMotherTongue", " selectedMotherTongue: " + selectedMotherTongue); // Debug log
                mothertongue.setText(selectedMotherTongue);
                updateMotherTongueInDatabase(selectedMotherTongue);}
            dialog.dismiss();
        });}
    private void updateMotherTongueInDatabase(String selectedMotherTongue) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("motherTongue").setValue(selectedMotherTongue)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AboutActivity.this, "MotherTongue updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AboutActivity.this, "Failed to update MotherTongue", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void populateMotherTongueOptions(RadioGroup radioGroupMotherTongue) {
        String[] MotherTongues = {"Telugu", "Hindi", "English", "Tamil","KonKani","Bengali","punjab","Assamese","Marathi","Kannada","Malyalam","Urdu","Gujarathi","Odia"};
        for (String MotherTongue : MotherTongues) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(MotherTongue);
            radioGroupMotherTongue.addView(radioButton);
        }
    }
    private void showDobDialog() {
        CustomDatePickerBottomSheet dialog = new CustomDatePickerBottomSheet(AboutActivity.this, new CustomDatePickerBottomSheet.OnDateSetListener() {
            @Override
            public void onDateSet(int day, int month, int year) {
                selectedDOB = day + "/" + (month + 1) + "/" + year;
                dob.setText(selectedDOB);
                userAge = calculateAge(day, month, year);
                System.out.println("User's age is: " + userAge);
                updateUserInDatabase(selectedDOB, String.valueOf(userAge));
            }
        });
        dialog.show();
    }
    private void updateUserInDatabase(String dob, String age) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("dob", dob);
            userUpdates.put("age", age);

            userRef.updateChildren(userUpdates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AboutActivity.this, "DOB and age saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AboutActivity.this, "Failed to update user's DOB and age: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
    private void showNameDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_name, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        // Find the EditText and buttons in the dialog
        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Show the dialog
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // Set the background color to transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        // Handle Cancel button click
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            String Name = editTextName.getText().toString().trim();
            if (!Name.isEmpty()) {

                updateNameInDatabase(Name);
            }
            dialog.dismiss();
        });
    }
    private void updateNameInDatabase(String name) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("name").setValue(name)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully updated
                            Toast.makeText(AboutActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update
                            Toast.makeText(AboutActivity.this, "Failed to update Name", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void setClickListener(View view, Runnable dialogAction) {
        view.setOnClickListener(v -> dialogAction.run());
    }
    private void highlightTextInTextView(int textViewId, List<String> textsToHighlight, int color) {
        TextView textView = findViewById(textViewId);
        String text = textView.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        for (String textToHighlight : textsToHighlight) {
            int startIndex = text.indexOf(textToHighlight);
            while (startIndex >= 0) {
                int endIndex = startIndex + textToHighlight.length();
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
                spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.indexOf(textToHighlight, endIndex);
            }
        }

        textView.setText(spannableString);
    }
    private void showGenderDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_gender, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        RadioGroup radioGroupGender = dialogView.findViewById(R.id.radioGroupGender);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Populate RadioButtons with gender options
        populateGenderOptions(radioGroupGender);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // Set the background color to transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                if (selectedRadioButton != null) {
                    String selectedGender = selectedRadioButton.getText().toString();
                    gender.setText(selectedGender);

                    // Update Firebase database
                    updateGenderInDatabase(selectedGender);
                } else {
                    Toast.makeText(AboutActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
    private void populateGenderOptions(RadioGroup radioGroup) {
        String[] genders = {"Male", "Female", "Gay", "Lesbian"};
        for (String gender : genders) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(gender);
            radioGroup.addView(radioButton);
        }
    }
    private void updateGenderInDatabase(String gender) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(auth.getCurrentUser().getUid());

        HashMap<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("gender", gender);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AboutActivity.this, "Gender updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AboutActivity.this, "Failed to update Gender : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEducationDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_education_level, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        // Find the RadioGroup and buttons in the dialog
        RadioGroup radioGroupEducation = dialogView.findViewById(R.id.radioGroupEducation);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Populate the RadioGroup with education options
        populateEducationOptions(radioGroupEducation);

        // Show the dialog
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // Set the background color to transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        // Handle Cancel button click
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Handle OK button click
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupEducation.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                if (selectedRadioButton != null) {
                    String selectedEducation = selectedRadioButton.getText().toString();
                    education.setText(selectedEducation);

                    // Update Firebase database
                    updateEducationInDatabase(selectedEducation);
                }
                dialog.dismiss();
            }
        });
    }
    private void populateEducationOptions(RadioGroup radioGroupEducation) {
        // Define education options
        String[] educationOptions = {"High School", "Bachelor's", "Master's", "PhD"};
        for (String option : educationOptions) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(option);
            radioGroupEducation.addView(radioButton);
        }
    }
    private void updateEducationInDatabase(String newEducation) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("educationLevel").setValue(newEducation)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully updated
                            Toast.makeText(AboutActivity.this, "Education updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update
                            Toast.makeText(AboutActivity.this, "Failed to update education", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void showJobDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_job_position, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        // Find the RadioGroup and buttons in the dialog
        RadioGroup radioGroupJob = dialogView.findViewById(R.id.radioGroupJob);
        Button btnOk = dialogView.findViewById(R.id.btnOkJob);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelJob);

        // Populate the RadioGroup with job options
        populateJobOptions(radioGroupJob);

        // Show the dialog
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // Set the background color to transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        // Handle Cancel button click
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            int selectedId = radioGroupJob.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
            if (selectedRadioButton != null) {
                String selectedJob = selectedRadioButton.getText().toString();
                Log.d("JobSelection", "Selected job: " + selectedJob); // Debug log
                job.setText(selectedJob); // Ensure this updates the UI

                // Update Firebase database
                updateJobInDatabase(selectedJob);
            }
            dialog.dismiss();
        });
    }
    private void populateJobOptions(RadioGroup radioGroupJob) {
        // Define job options
        String[] jobOptions ={"Student"," Doctor", "Teacher", "Artist", "Manager","Chef","Driver","Pilot","Writer"};

        for (String option : jobOptions) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(option);
            radioGroupJob.addView(radioButton);
        }
    }
    private void updateJobInDatabase(String newJob) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("jobType").setValue(newJob)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully updated
                            Toast.makeText(AboutActivity.this, "Job position updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update
                            Toast.makeText(AboutActivity.this, "Failed to update job position", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void showMaritalStatusDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_marital_status, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        // Find the RadioGroup and buttons in the dialog
        RadioGroup radioGroupMaritalStatus = dialogView.findViewById(R.id.radioGroupMaritalStatus);
        Button btnOkMaritalStatus = dialogView.findViewById(R.id.btnOkMaritalStatus);
        Button btnCancelMaritalStatus = dialogView.findViewById(R.id.btnCancelMaritalStatus);

        // Populate the RadioGroup with marital status options
        populateMaritalStatusOptions(radioGroupMaritalStatus);

        // Show the dialog
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // Set the background color to transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        // Handle Cancel button click
        btnCancelMaritalStatus.setOnClickListener(v -> dialog.dismiss());

        // Handle OK button click
        btnOkMaritalStatus.setOnClickListener(v -> {
            int selectedId = radioGroupMaritalStatus.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
            if (selectedRadioButton != null) {
                String selectedMaritalStatus = selectedRadioButton.getText().toString();
                Log.d("MaritalStatusSelection", "Selected marital status: " + selectedMaritalStatus); // Debug log
                maritalstatus.setText(selectedMaritalStatus); // Ensure this updates the UI

                // Update Firebase database
                updateMaritalStatusInDatabase(selectedMaritalStatus);
            }
            dialog.dismiss();
        });
    }

    private void populateMaritalStatusOptions(RadioGroup radioGroupMaritalStatus) {
        // Define marital status options
        String[] maritalStatusOptions = {"Single", "Single with child", "Married", "Divorced", "Divorced with child", "Widowed", "Widowed with child"};

        for (String option : maritalStatusOptions) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(option);
            radioGroupMaritalStatus.addView(radioButton);
        }
    }

    private void updateMaritalStatusInDatabase(String newMaritalStatus) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("maritalStatus").setValue(newMaritalStatus)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AboutActivity.this, "Marital status updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AboutActivity.this, "Failed to update marital status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void showDescriptionDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_description, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setView(dialogView);

        // Find the EditText and buttons in the dialog
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Button btnOk = dialogView.findViewById(R.id.btnOkDescription);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelDescription);

        // Show the dialog
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            // Set the background color to transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        // Handle Cancel button click
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            if (!description.isEmpty()) {

                updateDescriptionInDatabase(description);
            }
            dialog.dismiss();
        });
    }
    private void updateDescriptionInDatabase(String newDescription) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("description").setValue(newDescription)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully updated
                            Toast.makeText(AboutActivity.this, "Description updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update
                            Toast.makeText(AboutActivity.this, "Failed to update description", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
