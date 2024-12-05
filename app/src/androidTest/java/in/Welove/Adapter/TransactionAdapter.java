package in.Welove.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import in.Welove.Model.Transaction;
import in.Welove.R;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);


            if (transaction.isApproval()) {
                holder.tv_transaction_approval.setText("Approved");
            } else {
                holder.tv_transaction_approval.setText("Waiting");
            }
            holder.tv_transaction_approval.setVisibility(View.GONE);

        holder.tvTransactionType.setText(transaction.getType());
        if (transaction.getType().equals("debit")) {
            holder.tvTransactionAmount.setText(String.format("- ₹%.2f", transaction.getAmount()));
            holder.tvTransactionAmount.setTextColor(Color.RED); // Set color to black for debit
        } else if(transaction.getType().equals("Processing")){
            holder.tvTransactionAmount.setText(String.format("+ ₹%.2f", transaction.getAmount()));
            holder.tvTransactionAmount.setTextColor(Color.GRAY);
        }else {
            holder.tvTransactionAmount.setText(String.format("+ ₹%.2f", transaction.getAmount()));
            holder.tvTransactionAmount.setTextColor(Color.GREEN);
        }
        holder.tvTransactionDescription.setText(transaction.getDescription());

        // Format timestamp into a readable date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(transaction.getTimestamp()));
        holder.tvTransactionTimestamp.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionType, tvTransactionAmount, tvTransactionDescription, tvTransactionTimestamp,tv_transaction_approval;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionType = itemView.findViewById(R.id.tv_transaction_type);
            tvTransactionAmount = itemView.findViewById(R.id.tv_transaction_amount);
            tvTransactionDescription = itemView.findViewById(R.id.tv_transaction_description);
            tvTransactionTimestamp = itemView.findViewById(R.id.tv_transaction_timestamp);
            tv_transaction_approval=itemView.findViewById(R.id.tv_transaction_approval);
        }
    }
}
