package com.example.akoleih.home.model.network.api;

import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;

import java.util.List;

public interface HomeLocalDataSource {
    List<Category> getCachedCategories();
    void saveCategories(List<Category> categories);

    List<Meal> getCachedMeals(String category);
    void saveMeals(String category, List<Meal> meals);

    Meal getCachedRandomMeal();
    void cacheRandomMeal(Meal meal);

    Meal getCachedMealDetails(String mealId);
    void cacheMealDetails(Meal meal);

    void clearGuestData();
}