package in.Welove.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import in.Welove.Model.SubscriptionItem;
import in.Welove.R;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    private List<SubscriptionItem> subscriptionList;
    private int selectedPosition = -1; // Default to no selection
    private BillingFlowListener billingFlowListener;
    private Context context;

     public interface BillingFlowListener {
        void onPlanSelected(int position);
    }
    public SubscriptionAdapter(List<SubscriptionItem> subscriptionList, BillingFlowListener billingFlowListener,Context context) {
        this.subscriptionList = subscriptionList;
        this.billingFlowListener = billingFlowListener;
        this.context = context;

    }
    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription_card, parent, false);
        return new SubscriptionViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        SubscriptionItem item = subscriptionList.get(position);
        holder.planName.setText(item.getPlanName());
        holder.planPrice.setText(item.getPrice());
        holder.feature1.setText(item.getFeature1());
        holder.feature2.setText(item.getFeature2());
        holder.feature3.setText(item.getFeature3());
        holder.feature4.setText(item.getFeature4());

        String planName=item.getPlanName();

        if (planName.contains("Plus")) {
            // White text for clarity
            holder.planName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.planPrice.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature1.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature2.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature3.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature4.setTextColor(ContextCompat.getColor(context, R.color.white));

            // Premium look with dark gold background and image
            holder.premiumimage.setImageResource(R.drawable.vip);
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_gold));
        } else if (planName.contains("Hot")) {
            // White text for clarity
            holder.planName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.planPrice.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature1.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature2.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature3.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature4.setTextColor(ContextCompat.getColor(context, R.color.white));

            // Red accents for a vibrant and hot feel
            holder.premiumimage.setImageResource(R.drawable.custom_hot);
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.deep_red));
        } else {
            // White text for clarity
            holder.planName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.planPrice.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature1.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature2.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature3.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.feature4.setTextColor(ContextCompat.getColor(context, R.color.white));

            // Elegant and sleek with a rich, deep blue and classic premium image
            holder.premiumimage.setImageResource(R.drawable.premium);
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.deep_blue));
        }



        // Highlight the selected item by changing its background color
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.lightblue));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }

        // On click, update the selected position
        holder.itemView.setOnClickListener(v -> {
            // Only update if the position is different
            if (selectedPosition != position) {
                setSelectedPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    // Update the selected position and notify the activity
    public void setSelectedPosition(int position) {
        if (selectedPosition != position) {  // Only update if the position is different
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);  // Notify the previously selected item
            notifyItemChanged(selectedPosition);  // Notify the newly selected item


            if (billingFlowListener != null) {
                billingFlowListener.onPlanSelected(position);
            }
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public static class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        TextView planName, planPrice, feature1, feature2, feature3,feature4;
        ImageView premiumimage;
        LinearLayout linearLayout;

        public SubscriptionViewHolder(View itemView) {
            super(itemView);
            planName = itemView.findViewById(R.id.plan_name);
            planPrice = itemView.findViewById(R.id.plan_price);
            feature1 = itemView.findViewById(R.id.f1);
            feature2 = itemView.findViewById(R.id.f2);
            feature3 = itemView.findViewById(R.id.f3);
            feature4 = itemView.findViewById(R.id.f4);
            linearLayout=itemView.findViewById(R.id.itembackground);
            premiumimage=itemView.findViewById(R.id.premiumimage);
        }
    }
}
