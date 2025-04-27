package com.example.akoleih.home.network.api;

import com.example.akoleih.home.network.model.CategoriesResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoriesRemoteDataSource {
    @GET("categories.php")
    Call<CategoriesResponse> getCategories();

}