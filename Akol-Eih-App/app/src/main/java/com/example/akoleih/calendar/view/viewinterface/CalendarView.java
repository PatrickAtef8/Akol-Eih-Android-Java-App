package com.example.akoleih.calendar.view.viewinterface;

import com.example.akoleih.calendar.model.db.CalendarMeal;

import java.util.List;
import java.util.Set;

public interface CalendarView {
    void showCalendarMeals(List<CalendarMeal> meals);
    void showPlannedDates(Set<Long> dates);
    void showMealThumbnail(long date, String thumbnailUrl);
    void showError(String message);
    void refreshCalendar();
    void showUndoSnackbar();
    void restoreMeal(CalendarMeal meal, int position);
}