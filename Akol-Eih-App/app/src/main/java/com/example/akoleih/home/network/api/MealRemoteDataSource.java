
// MealRemoteDataSource.java
package com.example.akoleih.home.network.api;

import com.example.akoleih.home.network.model.MealResponse;

public interface MealRemoteDataSource {
    void fetchRandomMeal(DataSourceCallback<MealResponse> callback);
    void fetchMealsByCategory(String category, DataSourceCallback<MealResponse> callback);
    void fetchMealDetails(String mealId, DataSourceCallback<MealResponse> callback);
}
