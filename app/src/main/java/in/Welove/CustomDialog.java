package in.Welove;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog {

    private Dialog dialog;

    public CustomDialog(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_forcommoncontent); // Layout for the custom dialog
    }

    // Method to show the dialog with the custom layout, title, message, and button listeners
    public void show(String title, String message, View.OnClickListener onYesClickListener, boolean hideCancelButton) {
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button btnYes = dialog.findViewById(R.id.btn_yes);
        Button btnNo = dialog.findViewById(R.id.btn_no);

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_transparent);
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        btnYes.setOnClickListener(onYesClickListener);

        // If hideCancelButton is true, hide the "Cancel" button
        if (hideCancelButton) {
            btnNo.setVisibility(View.GONE);
        } else {
            btnNo.setVisibility(View.VISIBLE);
            btnNo.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.setCancelable(false);
        dialog.show();
    }

    // Method to set the text and functionality for the "Cancel" button
    public void setNegativeButton(String text, View.OnClickListener onClickListener) {
        Button btnNo = dialog.findViewById(R.id.btn_no);
        btnNo.setText(text);
        btnNo.setOnClickListener(onClickListener);
    }

    // Method to dismiss the dialog
    public void dismiss() {
        dialog.dismiss();
    }
}
