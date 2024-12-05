package in.Welove;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteMessageWorker extends Worker {
    public static final String MESSAGE_ID = "message_id";
    public static final String RECEIVER_ID = "receiver_id";

    public DeleteMessageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String messageId = getInputData().getString(MESSAGE_ID);
        String receiverId = getInputData().getString(RECEIVER_ID);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (messageId != null && receiverId != null) {
            // Reference for the current user (sender)
            DatabaseReference senderMessageRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUserId)
                    .child("Chatlist")
                    .child(receiverId)
                    .child("Chats")
                    .child(messageId);

            // Reference for the receiver
            DatabaseReference receiverMessageRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(receiverId)
                    .child("Chatlist")
                    .child(currentUserId)
                    .child("Chats")
                    .child(messageId);

            // Delete the message for both sender and receiver
            senderMessageRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DeleteMessageWorker", "Message deleted successfully for sender.");
                } else {
                    Log.e("DeleteMessageWorker", "Error deleting message for sender: " + task.getException());
                }
            });

            receiverMessageRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DeleteMessageWorker", "Message deleted successfully for receiver.");
                } else {
                    Log.e("DeleteMessageWorker", "Error deleting message for receiver: " + task.getException());
                }
            });
        }

        return Result.success();
    }
}
