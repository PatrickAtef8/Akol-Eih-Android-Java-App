package com.example.akoleih.search.model.network.api;

import com.example.akoleih.search.model.network.model.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApiService {
    @GET("search.php")
    Call<SearchResponse> searchByName(@Query("s") String name);

    @GET("filter.php")
    Call<SearchResponse> searchByIngredient(@Query("i") String ingredient);

    @GET("filter.php")
    Call<SearchResponse> searchByCategory(@Query("c") String category);

    @GET("filter.php")
    Call<SearchResponse> searchByArea(@Query("a") String area);
}