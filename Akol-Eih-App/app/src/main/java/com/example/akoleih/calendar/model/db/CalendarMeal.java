package com.example.akoleih.calendar.model.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calendar_meals")
public class CalendarMeal {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String mealId;
    private String mealName;
    private String mealThumb;
    private long date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealThumb() {
        return mealThumb;
    }

    public void setMealThumb(String mealThumb) {
        this.mealThumb = mealThumb;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}