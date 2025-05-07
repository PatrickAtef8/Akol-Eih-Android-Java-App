package com.example.akoleih.calendar.presenter;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.model.repository.CalendarRepository;
import com.example.akoleih.calendar.view.viewinterface.CalendarView;

import java.util.List;
import java.util.Set;

public class CalendarPresenterImpl implements CalendarPresenter {
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
    }

    @Override
    public void detachView() {
        this.calendarView = null;
        this.lifecycleOwner = null;
        this.pendingDeleteMeal = null;
        this.pendingDeletePosition = -1;
    }

    @Override
    public void loadMealsForDate(long date) {
        if (calendarView != null && lifecycleOwner != null) {
            LiveData<List<CalendarMeal>> mealsLiveData = repository.getMealsForDate(date);
            mealsLiveData.observe(lifecycleOwner, meals -> {
                if (calendarView != null) {
                    calendarView.showCalendarMeals(meals);
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
                }
            });
        }
    }

    @Override
    public void addMealToCalendar(CalendarMeal meal) {
        repository.addMeal(meal);
    }

    @Override
    public void removeMealFromCalendar(CalendarMeal meal) {
        repository.removeMeal(meal);
    }

    @Override
    public void loadMealThumbnailForDate(long date) {
        if (calendarView != null && lifecycleOwner != null) {
            LiveData<List<CalendarMeal>> mealsLiveData = repository.getMealsForDate(date);
            mealsLiveData.observe(lifecycleOwner, meals -> {
                String thumbnail = meals.isEmpty() ? null : meals.get(0).getMealThumb();
                if (calendarView != null) {
                    calendarView.showMealThumbnail(date, thumbnail);
                }
            });
        }
    }

    @Override
    public void onMealDeleteRequested(CalendarMeal meal, int position) {
        this.pendingDeleteMeal = meal;
        this.pendingDeletePosition = position;
        repository.removeMeal(meal); // Delete immediately
        if (calendarView != null) {
            calendarView.showUndoSnackbar();
        }
    }

    @Override
    public void onUndoRequested() {
        if (pendingDeleteMeal != null && pendingDeletePosition >= 0 && calendarView != null) {
            repository.addMeal(pendingDeleteMeal); // Restore by re-adding
            calendarView.restoreMeal(pendingDeleteMeal, pendingDeletePosition);
            pendingDeleteMeal = null;
            pendingDeletePosition = -1;
        }
    }

    @Override
    public void onDeleteConfirmed() {
        // No action needed, as deletion happens immediately
        pendingDeleteMeal = null;
        pendingDeletePosition = -1;
    }

    @Override
    public LiveData<Integer> getMealCountForDate(long date) {
        return Transformations.map(repository.getMealsForDate(date), meals -> meals != null ? meals.size() : 0);
    }
}