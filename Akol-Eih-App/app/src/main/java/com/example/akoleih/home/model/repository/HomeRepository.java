package com.example.akoleih.home.model.repository;

import androidx.lifecycle.LiveData;

import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import java.util.List;

public interface HomeRepository {
    LiveData<List<Category>> getCategories();
    LiveData<Meal> getRandomMeal();

    LiveData<List<Meal>> getMealsByCategory(String category);

    LiveData<Meal> getMealDetails(String mealId);

    LiveData<String> getError();
}