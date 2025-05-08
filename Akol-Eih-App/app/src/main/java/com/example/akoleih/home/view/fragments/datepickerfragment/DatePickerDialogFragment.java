package com.example.akoleih.home.view.fragments.datepickerfragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.akoleih.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment {
    public interface OnDateSelectedListener {
        void onDateSelected(long date);
    }

    private OnDateSelectedListener listener;
    private long selectedDate;

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_date_picker_dialog, null);
        MaterialCalendarView calendarView = view.findViewById(R.id.calendarView);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // Set current date as minimum date
        Calendar currentCalendar = Calendar.getInstance();
        CalendarDay minDate = CalendarDay.from(
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH) + 1, // MaterialCalendarView months are 1-based
                currentCalendar.get(Calendar.DAY_OF_MONTH)
        );
        calendarView.state().edit()
                .setMinimumDate(minDate)
                .commit();
        selectedDate = currentCalendar.getTimeInMillis();

        // Add decorator to grey out past dates
        calendarView.addDecorator(new PastDatesDecorator(minDate));

        // Handle date selection
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                Calendar cal = Calendar.getInstance();
                cal.set(date.getYear(), date.getMonth() - 1, date.getDay()); // Convert to 0-based month
                selectedDate = cal.getTimeInMillis();
            }
        });

        // Create dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(view);

        // Set dialog size
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.9); // 90% of screen width
        int dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = dialogWidth;
        params.height = dialogHeight;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnConfirm.setOnClickListener(v -> {
            if (listener != null && selectedDate >= currentCalendar.getTimeInMillis()) {
                listener.onDateSelected(selectedDate);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
        return dialog;
    }

    // Decorator to grey out past dates
    private static class PastDatesDecorator implements DayViewDecorator {
        private final CalendarDay minDate;

        public PastDatesDecorator(CalendarDay minDate) {
            this.minDate = minDate;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.isBefore(minDate);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true); // Disable interaction
            // Use grey with 50% opacity (127 = 0x7F in ARGB)
            view.addSpan(new android.text.style.ForegroundColorSpan(Color.argb(127, 128, 128, 128)));
        }
    }
}