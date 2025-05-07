package com.example.akoleih.calendar.presenter;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.view.viewinterface.CalendarView;

public interface CalendarPresenter {
    void loadMealsForDate(long date);
    void loadPlannedDates();
    void addMealToCalendar(CalendarMeal meal);
    void removeMealFromCalendar(CalendarMeal meal);
    void loadMealThumbnailForDate(long date);
    void attachView(CalendarView calendarView, LifecycleOwner lifecycleOwner);
    void detachView();
    void onMealDeleteRequested(CalendarMeal meal, int position);
    void onUndoRequested();
    void onDeleteConfirmed();
    LiveData<Integer> getMealCountForDate(long date);
}