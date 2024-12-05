package in.Welove.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean isEnabled(int position) {
        // Disable the first item (position 0) as it is the hint
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;

        // Set the color of the disabled item to gray
        if (position == 0) {
            textView.setTextColor(getContext().getColor(android.R.color.darker_gray));
        } else {
            textView.setTextColor(getContext().getColor(android.R.color.black));
        }

        return view;
    }
}