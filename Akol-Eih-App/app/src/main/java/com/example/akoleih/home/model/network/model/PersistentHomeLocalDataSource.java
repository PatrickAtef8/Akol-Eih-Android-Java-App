package com.example.akoleih.home.model.network.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.model.network.api.HomeLocalDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PersistentHomeLocalDataSource implements HomeLocalDataSource {
    private final SharedPreferences prefs;
    private final Gson gson;
    private final String PREFS_NAME = "HomeCache";

    public PersistentHomeLocalDataSource(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        Log.d("PersistentLocalDS", "Initialized");
    }

    @Override
    public List<Category> getCachedCategories() {
        String json = prefs.getString("categories", "[]");
        Type type = new TypeToken<List<Category>>(){}.getType();
        List<Category> categories = gson.fromJson(json, type);
        Log.d("PersistentLocalDS", "Retrieved categories, size: " + (categories != null ? categories.size() : 0));
        return categories != null ? categories : new ArrayList<>();
    }

    @Override
    public void saveCategories(List<Category> categories) {
        String json = gson.toJson(categories != null ? categories : new ArrayList<>());
        prefs.edit().putString("categories", json).apply();
        Log.d("PersistentLocalDS", "Saved categories, size: " + (categories != null ? categories.size() : 0));
    }

    @Override
    public List<Meal> getCachedMeals(String category) {
        String json = prefs.getString("meals_" + category, "[]");
        Type type = new TypeToken<List<Meal>>(){}.getType();
        List<Meal> meals = gson.fromJson(json, type);
        Log.d("PersistentLocalDS", "Retrieved meals for " + category + ", size: " + (meals != null ? meals.size() : 0));
        return meals != null ? meals : new ArrayList<>();
    }

    @Override
    public void saveMeals(String category, List<Meal> meals) {
        String json = gson.toJson(meals != null ? meals : new ArrayList<>());
        prefs.edit().putString("meals_" + category, json).apply();
        Log.d("PersistentLocalDS", "Saved meals for " + category + ", size: " + (meals != null ? meals.size() : 0));
    }

    @Override
    public Meal getCachedRandomMeal() {
        String json = prefs.getString("random_meal", null);
        Meal meal = json != null ? gson.fromJson(json, Meal.class) : null;
        Log.d("PersistentLocalDS", "Retrieved random meal: " + (meal != null ? meal.getName() : "null"));
        return meal;
    }

    @Override
    public void cacheRandomMeal(Meal meal) {
        String json = gson.toJson(meal);
        prefs.edit().putString("random_meal", json).apply();
        Log.d("PersistentLocalDS", "Cached random meal: " + (meal != null ? meal.getName() : "null"));
    }

    @Override
    public Meal getCachedMealDetails(String mealId) {
        String json = prefs.getString("meal_details_" + mealId, null);
        Meal meal = json != null ? gson.fromJson(json, Meal.class) : null;
        Log.d("PersistentLocalDS", "Retrieved meal details for " + mealId + ": " + (meal != null ? meal.getName() : "null"));
        return meal;
    }

    @Override
    public void cacheMealDetails(Meal meal) {
        if (meal != null) {
            String json = gson.toJson(meal);
            prefs.edit().putString("meal_details_" + meal.getIdMeal(), json).apply();
            Log.d("PersistentLocalDS", "Cached meal details for " + meal.getIdMeal() + ": " + meal.getName());
        }
    }

    public void clearGuestData() {
        prefs.edit().clear().apply();
        Log.d("PersistentLocalDS", "Cleared all guest data");
    }
}