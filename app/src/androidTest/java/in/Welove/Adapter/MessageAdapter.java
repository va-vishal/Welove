package in.Welove.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import in.Welove.Model.Chat;
import in.Welove.R;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Chat> mChat;
    private final String imageUrl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);

        String receiverId = chat.getReceiver();
        String senderId = chat.getSender();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = fuser.getUid();

        if (currentUserId.equals(senderId)) {
            Log.d("Chat", "currentUserid " +currentUserId);
            Log.d("Chat", "Sender " +senderId);
            Log.d("Chat", "receiverId  " + receiverId);
        } else {

        }
        holder.cardText.setVisibility(View.GONE);
        holder.cardImage.setVisibility(View.GONE);
        holder.txtSeen.setVisibility(View.GONE);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(new Date(chat.getTimestamp()));

        if ("text".equals(chat.getType())) {
            holder.cardText.setVisibility(View.VISIBLE);
            holder.showMessage.setText(chat.getMessage());
            holder.timestampText.setText(formattedTime);
        } else if ("image".equals(chat.getType())) {
            holder.cardImage.setVisibility(View.VISIBLE);
            holder.imageAdded.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(chat.getMessage())
                    .into(holder.imageAdded);

            holder.timestampImage.setText(formattedTime);

            holder.imageAdded.setOnClickListener(v -> {
                showFullScreenImage(holder.itemView, chat.getMessage());
            });
        }

        ConstraintLayout.LayoutParams text = (ConstraintLayout.LayoutParams) holder.cardText.getLayoutParams();
        ConstraintLayout.LayoutParams image = (ConstraintLayout.LayoutParams) holder.cardImage.getLayoutParams();

        if (mChat.get(position).getSender().equals(fuser.getUid())) {

            FrameLayout.LayoutParams timestampLayoutParams = (FrameLayout.LayoutParams) holder.timestampImage.getLayoutParams();
            timestampLayoutParams.gravity = Gravity.BOTTOM | Gravity.END; // Bottom-right corner
            holder.timestampImage.setLayoutParams(timestampLayoutParams);

            text.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            text.startToStart = ConstraintLayout.LayoutParams.UNSET;
            text.matchConstraintPercentWidth = 0.7f;

            holder.cardText.setLayoutParams(text);
            // Set constraints for the image message card
            image.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            image.startToStart = ConstraintLayout.LayoutParams.UNSET;
            image.width = 0; // Use match constraint
            image.matchConstraintPercentWidth = 0.6f; // Takes up 50% of the screen width
            holder.cardImage.setLayoutParams(image);

        } else {
            LinearLayout.LayoutParams showMessage = (LinearLayout.LayoutParams) holder.showMessage.getLayoutParams();
            showMessage.gravity=Gravity.END;
            holder.showMessage.setLayoutParams(showMessage);
            LinearLayout.LayoutParams time = (LinearLayout.LayoutParams) holder.timestampText.getLayoutParams();
            time.gravity=Gravity.START|Gravity.BOTTOM;
            holder.timestampText.setLayoutParams(time);

            text.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            text.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            text.matchConstraintPercentWidth = 0.6f;
            holder.cardText.setLayoutParams(text);

            image.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            image.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            image.width = 0; // Use match constraint
            image.matchConstraintPercentWidth = 0.7f; // Takes up 50% of the screen width
            holder.cardImage.setLayoutParams(image);
        }
        if (position == mChat.size() - 1) {  // Check if it's the last message
            if ((mChat.get(position).getSender().equals(currentUserId)) &&
                    (mChat.get(position).getReceiver().equals(receiverId))) {  // Check if the sender is the current user and receiver matches
                holder.txtSeen.setText(chat.isIsseen() ? "Seen" : "Sent");
                Log.d("Chat", "Message seen: " + chat.isIsseen());
                holder.txtSeen.setVisibility(View.VISIBLE);
            } else {
                holder.txtSeen.setVisibility(View.GONE);
            }
        } else {
            holder.txtSeen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    private void showFullScreenImage(View rootView, String imageUrl) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_image, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        int popupWidth = (int) (displayMetrics.widthPixels * 0.9); // 90% of screen width
        int popupHeight = (int) (displayMetrics.heightPixels * 0.9); // 90% of screen height

        PopupWindow popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);

        RoundedImageView popupImageView = popupView.findViewById(R.id.popupImageView);

        Glide.with(mContext)
                .load(imageUrl)
                .fitCenter() // Ensures the entire image fits in the view without cropping
                .into(popupImageView);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        WindowManager.LayoutParams layoutParams = ((android.app.Activity) mContext).getWindow().getAttributes();
        layoutParams.alpha = 0.4f; // Dim the screen
        ((android.app.Activity) mContext).getWindow().setAttributes(layoutParams);

        popupView.setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        popupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams layoutParams1 = ((android.app.Activity) mContext).getWindow().getAttributes();
            layoutParams1.alpha = 1.0f; // Restore the screen brightness
            ((android.app.Activity) mContext).getWindow().setAttributes(layoutParams1);
        });
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView showMessage;
        public RoundedImageView imageAdded;
        public TextView txtSeen;
        public TextView timestampText, timestampImage;
        public CardView cardText, cardImage;

        public ViewHolder(View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            imageAdded = itemView.findViewById(R.id.image_added);
            txtSeen = itemView.findViewById(R.id.txt_seen);
            timestampText = itemView.findViewById(R.id.timestamp_text);
            timestampImage = itemView.findViewById(R.id.timestamp_image);
            cardText = itemView.findViewById(R.id.card_text);
            cardImage = itemView.findViewById(R.id.card_image);
        }
    }
}
