// network/CategoryRemoteDataSourceImpl.java
package com.example.akoleih.home.model.network;

import com.example.akoleih.home.model.network.api.CategoryApiService;
import com.example.akoleih.home.model.network.api.CategoryRemoteDataSource;
import com.example.akoleih.home.model.network.api.DataSourceCallback;
import com.example.akoleih.home.model.network.model.CategoriesResponse;
import retrofit2.Call;
import retrofit2.Response;

public class CategoryRemoteDataSourceImpl implements CategoryRemoteDataSource {
    private final CategoryApiService api;

    public CategoryRemoteDataSourceImpl() {
        this.api = RetrofitClient.getInstance()
                .create(CategoryApiService.class);
    }

    @Override
    public void fetchCategories(DataSourceCallback<CategoriesResponse> callback) {
        api.getCategories().enqueue(new retrofit2.Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    callback.onSuccess(resp.body());
                } else {
                    callback.onError("Server error: " + resp.code());
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}