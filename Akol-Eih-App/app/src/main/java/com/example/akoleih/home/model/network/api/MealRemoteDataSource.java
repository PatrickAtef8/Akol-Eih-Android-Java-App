
// MealRemoteDataSource.java
package com.example.akoleih.home.model.network.api;

import com.example.akoleih.home.model.network.model.MealResponse;

public interface MealRemoteDataSource {
    void fetchRandomMeal(DataSourceCallback<MealResponse> callback);
    void fetchMealsByCategory(String category, DataSourceCallback<MealResponse> callback);

    void fetchMealsByCountry(String country, DataSourceCallback<MealResponse> callback);

    void fetchMealDetails(String mealId, DataSourceCallback<MealResponse> callback);
}
