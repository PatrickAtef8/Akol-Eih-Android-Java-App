package com.example.akoleih.calendar.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.presenter.CalendarPresenter;
import com.example.akoleih.calendar.view.adapter.CalendarAdapter;
import com.example.akoleih.calendar.view.adapter.listener.OnDeleteClickListener;
import com.example.akoleih.calendar.view.adapter.listener.OnPlannedMealClickListener;
import com.example.akoleih.calendar.view.util.CalendarDecorators;
import com.example.akoleih.calendar.view.viewinterface.CalendarView;
import com.example.akoleih.home.view.fragments.thirdfragment.HomeMealDetailsThirdFragment;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarFragment extends Fragment implements CalendarView, OnPlannedMealClickListener {
    private static final String TAG = "CalendarFragment";
    private RecyclerView mealsRecycler;
    private CalendarAdapter adapter;
    private CalendarPresenter presenter;
    private MaterialCalendarView calendarView;
    private final Set<CalendarDay> plannedDates = new HashSet<>();
    private final Map<CalendarDay, String> mealThumbnails = new HashMap<>();
    private final Map<CalendarDay, Integer> mealCounts = new HashMap<>();

    public CalendarFragment(CalendarPresenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        mealsRecycler = view.findViewById(R.id.mealsRecycler);
        mealsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CalendarAdapter(
                new OnDeleteClickListener() {
                    @Override
                    public void onDeleteClick(CalendarMeal meal, int position) {
                        presenter.onMealDeleteRequested(meal, position);
                    }
                },
                this
        );
        mealsRecycler.setAdapter(adapter);

        CalendarDay today = CalendarDay.today();
        calendarView.setSelectedDate(today);
        // Removed setMinimumDate(today) to allow past date selection
        calendarView.state().edit()
                .commit();
        calendarView.addDecorator(CalendarDecorators.createPastDatesDecorator(today));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this, this);

        getParentFragmentManager().setFragmentResultListener(
                "mealAdded",
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(String key, Bundle result) {
                        long addedDate = result.getLong("addedDate");
                        presenter.loadPlannedDates();
                        LocalDate local = Instant.ofEpochMilli(addedDate)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        calendarView.setSelectedDate(CalendarDay.from(local));
                        presenter.loadMealsForDate(addedDate);
                        Log.d(TAG, "Meal added for date: " + local);
                    }
                }
        );

        calendarView.setOnDateChangedListener((widget, date, sel) -> {
            long millis = date.getDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            Log.d(TAG, "Date selected: " + date + ", millis: " + millis);
            presenter.loadMealsForDate(millis);
        });

        // Initial load
        presenter.loadPlannedDates();
        long todayMillis = CalendarDay.today().getDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
        presenter.loadMealsForDate(todayMillis);
        Log.d(TAG, "Initial load for today: " + todayMillis);
    }

    @Override
    public void showCalendarMeals(List<CalendarMeal> meals) {
        adapter.setMeals(meals);
        Log.d(TAG, "Showing meals, count: " + (meals != null ? meals.size() : 0));
    }

    @Override
    public void showPlannedDates(Set<Long> dates) {
        plannedDates.clear();
        mealThumbnails.clear();
        mealCounts.clear();

        for (Long millis : dates) {
            LocalDate local = Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            CalendarDay day = CalendarDay.from(local);
            plannedDates.add(day);

            presenter.loadMealThumbnailForDate(millis);
            presenter.getMealCountForDate(millis).observe(getViewLifecycleOwner(), count -> {
                mealCounts.put(day, count);
                calendarView.invalidateDecorators();
                Log.d(TAG, "Meal count for " + day + ": " + count);
            });
        }

        calendarView.removeDecorators();
        calendarView.addDecorator(CalendarDecorators.createPastDatesDecorator(CalendarDay.today()));
        calendarView.addDecorator(CalendarDecorators.createMealThumbnailDecorator(plannedDates, mealThumbnails, mealCounts, this));
        Log.d(TAG, "Planned dates updated, count: " + dates.size());
    }

    @Override
    public void showMealThumbnail(long date, String thumbnailUrl) {
        LocalDate local = Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        CalendarDay day = CalendarDay.from(local);
        if (thumbnailUrl != null) {
            mealThumbnails.put(day, thumbnailUrl);
        } else {
            mealThumbnails.remove(day);
        }
        calendarView.invalidateDecorators();
        Log.d(TAG, "Thumbnail for " + day + ": " + thumbnailUrl);
    }

    @Override
    public void showError(String msg) {
        if (isAdded()) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + msg);
        }
    }

    @Override
    public void refreshCalendar() {
        presenter.loadPlannedDates();
        CalendarDay sel = calendarView.getSelectedDate();
        if (sel != null) {
            long millis = sel.getDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            presenter.loadMealsForDate(millis);
            Log.d(TAG, "Calendar refreshed, selected date: " + sel);
        }
    }

    @Override
    public void showUndoSnackbar() {
        Snackbar sb = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG);
        configureSnackbarView(sb);
        setupSnackbarContent(sb);
        addSnackbarAnimations(sb);
        sb.show();
        Log.d(TAG, "Showing undo snackbar");
    }

    @Override
    public void restoreMeal(CalendarMeal meal, int position) {
        adapter.addItem(position, meal);
        Log.d(TAG, "Restored meal at position: " + position);
    }

    @Override
    public void onMealClick(CalendarMeal meal) {
        if (isAdded()) {
            HomeMealDetailsThirdFragment fragment = HomeMealDetailsThirdFragment.newInstance(meal.getMealId());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            Log.d(TAG, "Meal clicked, ID: " + meal.getMealId());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        calendarView.removeDecorators();
        Log.d(TAG, "Fragment view destroyed");
    }

    @SuppressLint("RestrictedApi")
    private void configureSnackbarView(Snackbar sb) {
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) sb.getView();
        layout.removeAllViews();
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setPadding(0, 0, 0, 0);

        View custom = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_snackbar, null);
        DisplayMetrics dm = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int margin = getResources().getDimensionPixelSize(R.dimen.snackbar_margin);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
                dm.widthPixels - 2 * margin,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        p.bottomMargin = getResources().getDimensionPixelSize(R.dimen.snackbar_bottom_margin);
        custom.setLayoutParams(p);
        layout.addView(custom);
    }

    private void setupSnackbarContent(Snackbar sb) {
        @SuppressLint("RestrictedApi") View custom = ((Snackbar.SnackbarLayout) sb.getView()).getChildAt(0);
        ImageView icon = custom.findViewById(R.id.icon);
        TextView text = custom.findViewById(R.id.text);
        Button action = custom.findViewById(R.id.action);

        icon.setImageResource(R.drawable.ic_calendar);
        text.setText("Meal deleted");
        action.setText(R.string.undo);

        action.setOnClickListener(v -> {
            presenter.onUndoRequested();
            sb.dismiss();
            Log.d(TAG, "Undo clicked");
        });

        sb.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    presenter.onDeleteConfirmed();
                    Log.d(TAG, "Delete confirmed");
                }
            }
        });
    }

    private void addSnackbarAnimations(Snackbar sb) {
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) sb.getView();
        Animation in = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_right);
        layout.startAnimation(in);
        sb.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar sb, int event) {
                Animation out = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_left);
                layout.startAnimation(out);
            }
        });
    }
}