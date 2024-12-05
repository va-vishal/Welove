package in.Welove.Model;

public class Transaction {
    private double amount;
    private String type; // "credit" or "debit"
    private long timestamp;
    private String description;
    private boolean approval;

    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    public Transaction(double amount, String type, long timestamp, String description) {
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.description = description;
    }

    public Transaction(boolean approval) {
        this.approval = approval;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isApproval() {
        return approval;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
    }
}

