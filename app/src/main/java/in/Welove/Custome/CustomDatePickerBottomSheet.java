package in.Welove.Custome;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;

import in.Welove.R;


public class CustomDatePickerBottomSheet extends BottomSheetDialog {

    private NumberPicker numberPickerDay;
    private NumberPicker numberPickerMonth;
    private NumberPicker numberPickerYear;
    private Button buttonSetDate;
    private final OnDateSetListener onDateSetListener;

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    public interface OnDateSetListener {
        void onDateSet(int day, int month, int year);
    }

    public CustomDatePickerBottomSheet(@NonNull Context context, OnDateSetListener listener) {
        super(context);
        this.onDateSetListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);

        numberPickerDay = findViewById(R.id.numberPickerDay);
        numberPickerMonth = findViewById(R.id.numberPickerMonth);
        numberPickerYear = findViewById(R.id.numberPickerYear);
        buttonSetDate = findViewById(R.id.buttonSetDate);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        numberPickerDay.setMinValue(1);
        numberPickerDay.setMaxValue(31);
        numberPickerDay.setValue(currentDay);

        numberPickerMonth.setMinValue(0);
        numberPickerMonth.setMaxValue(11);
        numberPickerMonth.setDisplayedValues(MONTHS);
        numberPickerMonth.setValue(currentMonth);

        numberPickerYear.setMinValue(1900);
        numberPickerYear.setMaxValue(currentYear);
        numberPickerYear.setValue(currentYear);

        buttonSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDateSetListener != null) {
                    int day = numberPickerDay.getValue();
                    int month = numberPickerMonth.getValue();
                    int year = numberPickerYear.getValue();
                    onDateSetListener.onDateSet(day, month, year);
                }
                dismiss();
            }
        });
    }
}