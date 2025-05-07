// CategoryApiService.java
package com.example.akoleih.home.model.network.api;

import com.example.akoleih.home.model.network.model.CategoriesResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApiService {
    @GET("categories.php")
    Call<CategoriesResponse> getCategories();
}
