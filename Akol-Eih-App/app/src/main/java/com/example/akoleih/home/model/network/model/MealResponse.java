package com.example.akoleih.home.model.network.model;

import com.example.akoleih.home.model.Meal;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealResponse {
    @SerializedName("meals")
    private List<Meal> meals;

    @SerializedName("strTags")
    private String strTags;

    @SerializedName("strTime")
    private String strTime;

    public List<Meal> getMeals() { return meals; }
    public String getStrTags() { return strTags; }
    public String getStrTime() { return strTime; }
}