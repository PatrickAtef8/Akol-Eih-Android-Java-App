package com.example.akoleih.calendar.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CalendarMealDao {

    @Query("SELECT * FROM calendar_meals WHERE date BETWEEN :start AND :end")
    LiveData<List<CalendarMeal>> getMealsByDateRange(long start, long end);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CalendarMeal meal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CalendarMeal> meals);

    @Delete
    void delete(CalendarMeal meal);

    @Query("DELETE FROM calendar_meals")
    void deleteAll();

    @Query("SELECT * FROM calendar_meals WHERE date = :dateMillis")
    LiveData<List<CalendarMeal>> getMealsForDate(long dateMillis);

    @Query("SELECT DISTINCT date FROM calendar_meals")
    LiveData<List<Long>> getAllPlannedDates();

    @Query("SELECT mealThumb FROM calendar_meals WHERE date = :dateMillis LIMIT 1")
    String getMealThumbnailForDate(long dateMillis);
}