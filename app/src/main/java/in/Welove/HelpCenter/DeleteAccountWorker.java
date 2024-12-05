package in.Welove.HelpCenter;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountWorker extends Worker {

    private final DatabaseReference usersRef;

    public DeleteAccountWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get userId passed from the inputData
        String userId = getInputData().getString("userId");

        if (userId != null) {
            deleteUserAccount(userId);
            return Result.success();
        } else {
            Log.e("DeleteAccountWorker", "No userId found in input data.");
            return Result.failure();
        }
    }

    private void deleteUserAccount(String userId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Ensure the user to be deleted is the one who is authenticated
        if (currentUser != null && currentUser.getUid().equals(userId)) {
            // Delete user from Firebase Authentication
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DeleteAccountWorker", "User account deleted from FirebaseAuth.");

                    // Remove user data from Firebase Database
                    usersRef.child(userId).removeValue().addOnCompleteListener(removeTask -> {
                        if (removeTask.isSuccessful()) {
                            Log.d("DeleteAccountWorker", "User data removed from Firebase Database.");
                        } else {
                            Log.e("DeleteAccountWorker", "Failed to remove user data from Firebase Database: " + removeTask.getException().getMessage());
                        }
                    });
                } else {
                    Log.e("DeleteAccountWorker", "Failed to delete user from FirebaseAuth: " + task.getException().getMessage());
                }
            });
        } else {
            Log.e("DeleteAccountWorker", "User not authenticated or userId mismatch.");
        }
    }
}
