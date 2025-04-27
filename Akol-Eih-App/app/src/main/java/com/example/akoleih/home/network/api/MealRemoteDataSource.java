package com.example.akoleih.home.network.api;

import com.example.akoleih.home.network.model.MealResponse;
import retrofit2.Call;
import retrofit2.http.GET;
public interface MealRemoteDataSource {
    @GET("random.php")
    Call<MealResponse> getRandomMeal();
}