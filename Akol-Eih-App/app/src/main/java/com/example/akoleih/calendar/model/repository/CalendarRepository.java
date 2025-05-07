package com.example.akoleih.calendar.model.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.calendar.model.db.CalendarMeal;

import java.util.List;
import java.util.Set;

public interface CalendarRepository {
    LiveData<List<CalendarMeal>> getMealsForDate(long date);
    LiveData<Set<Long>> getPlannedDates();
    void addMeal(CalendarMeal meal);
    void removeMeal(CalendarMeal meal);

    void syncCalendarFromFirestore(DataCallback<Void> callback);
    void syncCalendarOnStartup(Context context);
    void clearCalendarData();


}