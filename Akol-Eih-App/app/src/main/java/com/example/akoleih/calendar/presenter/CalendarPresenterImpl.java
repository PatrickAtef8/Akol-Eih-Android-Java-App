package com.example.akoleih.calendar.presenter;

import android.util.Log;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.model.repository.CalendarRepository;
import com.example.akoleih.calendar.view.viewinterface.CalendarView;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.List;
import java.util.Set;

public class CalendarPresenterImpl implements CalendarPresenter {
    private static final String TAG = "CalendarPresenter";
    private CalendarView calendarView;
    private LifecycleOwner lifecycleOwner;
    private final CalendarRepository repository;
    private CalendarMeal pendingDeleteMeal;
    private int pendingDeletePosition = -1;

    public CalendarPresenterImpl(CalendarRepository repository) {
        this.repository = repository;
    }

    @Override
    public void attachView(CalendarView calendarView, LifecycleOwner lifecycleOwner) {
        this.calendarView = calendarView;
        this.lifecycleOwner = lifecycleOwner;
        Log.d(TAG, "View attached");
    }

    @Override
    public void detachView() {
        this.calendarView = null;
        this.lifecycleOwner = null;
        this.pendingDeleteMeal = null;
        this.pendingDeletePosition = -1;
        Log.d(TAG, "View detached");
    }

    @Override
    public void loadMealsForDate(long date) {
        if (calendarView != null && lifecycleOwner != null) {
            LiveData<List<CalendarMeal>> mealsLiveData = repository.getMealsForDate(date);
            mealsLiveData.observe(lifecycleOwner, meals -> {
                if (calendarView != null) {
                    calendarView.showCalendarMeals(meals);
                    Log.d(TAG, "Loaded meals for date " + date + ", count: " + (meals != null ? meals.size() : 0));
                }
            });
        }
    }

    @Override
    public void loadPlannedDates() {
        if (calendarView != null && lifecycleOwner != null) {
            LiveData<Set<Long>> datesLiveData = repository.getPlannedDates();
            datesLiveData.observe(lifecycleOwner, dates -> {
                if (calendarView != null) {
                    calendarView.showPlannedDates(dates);
                    Log.d(TAG, "Loaded planned dates, count: " + (dates != null ? dates.size() : 0));
                }
            });
        }
    }

    @Override
    public void addMealToCalendar(CalendarMeal meal) {
        repository.addMeal(meal);
        Log.d(TAG, "Added meal: " + meal.getMealId() + " for date: " + meal.getDate());
    }

    @Override
    public void removeMealFromCalendar(CalendarMeal meal) {
        repository.removeMeal(meal);
        Log.d(TAG, "Removed meal: " + meal.getMealId() + " from date: " + meal.getDate());
    }

    @Override
    public void loadMealThumbnailForDate(long date) {
        if (calendarView != null && lifecycleOwner != null) {
            LiveData<List<CalendarMeal>> mealsLiveData = repository.getMealsForDate(date);
            mealsLiveData.observe(lifecycleOwner, meals -> {
                String thumbnail = meals.isEmpty() ? null : meals.get(0).getMealThumb();
                if (calendarView != null) {
                    calendarView.showMealThumbnail(date, thumbnail);
                    Log.d(TAG, "Loaded thumbnail for date " + date + ": " + thumbnail);
                }
            });
        }
    }

    @Override
    public void onMealDeleteRequested(CalendarMeal meal, int position) {
        // Optional: Restrict deletion for past dates
        LocalDate mealDate = Instant.ofEpochMilli(meal.getDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();
        if (mealDate.isBefore(today)) {
            if (calendarView != null) {
                calendarView.showError("Cannot delete meals from past dates");
                Log.w(TAG, "Attempted to delete meal from past date: " + mealDate);
            }
            return;
        }

        this.pendingDeleteMeal = meal;
        this.pendingDeletePosition = position;
        repository.removeMeal(meal); // Delete immediately
        if (calendarView != null) {
            calendarView.showUndoSnackbar();
            Log.d(TAG, "Delete requested for meal: " + meal.getMealId() + " at position: " + position);
        }
    }

    @Override
    public void onUndoRequested() {
        if (pendingDeleteMeal != null && pendingDeletePosition >= 0 && calendarView != null) {
            repository.addMeal(pendingDeleteMeal);
            calendarView.restoreMeal(pendingDeleteMeal, pendingDeletePosition);
            Log.d(TAG, "Undo restore for meal: " + pendingDeleteMeal.getMealId());
            pendingDeleteMeal = null;
            pendingDeletePosition = -1;
        }
    }

    @Override
    public void onDeleteConfirmed() {
        Log.d(TAG, "Delete confirmed for pending meal");
        pendingDeleteMeal = null;
        pendingDeletePosition = -1;
    }

    @Override
    public LiveData<Integer> getMealCountForDate(long date) {
        return Transformations.map(repository.getMealsForDate(date), meals -> {
            int count = meals != null ? meals.size() : 0;
            Log.d(TAG, "Meal count for date " + date + ": " + count);
            return count;
        });
    }
}