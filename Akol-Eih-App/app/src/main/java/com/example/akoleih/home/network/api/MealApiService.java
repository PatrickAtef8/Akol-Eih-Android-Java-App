// MealApiService.java
package com.example.akoleih.home.network.api;

import com.example.akoleih.home.network.model.MealResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    @GET("random.php")
    Call<MealResponse> getRandomMeal();

    @GET("filter.php")
    Call<MealResponse> getMealsByCategory(@Query("c") String category);

    @GET("lookup.php")
    Call<MealResponse> getMealDetails(@Query("i") String mealId);
}