
// HomeRepository.java
package com.example.akoleih.home.model.repository;

import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.network.api.DataSourceCallback;

import java.util.List;

public interface HomeRepository {
    void getCategories(DataSourceCallback<List<Category>> callback);
    void getRandomMeal(DataSourceCallback<Meal> callback);
    void getMealsByCategory(String category, DataSourceCallback<List<Meal>> callback);
    void getMealDetails(String mealId, DataSourceCallback<Meal> callback);
}