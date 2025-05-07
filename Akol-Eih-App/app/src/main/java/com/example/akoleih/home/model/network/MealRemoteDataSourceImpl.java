package com.example.akoleih.home.model.network;

import com.example.akoleih.home.model.network.api.MealApiService;
import com.example.akoleih.home.model.network.api.MealRemoteDataSource;
import com.example.akoleih.home.model.network.api.DataSourceCallback;
import com.example.akoleih.home.model.network.model.MealResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealRemoteDataSourceImpl implements MealRemoteDataSource {
    private final MealApiService api;

    public MealRemoteDataSourceImpl() {
        this.api = RetrofitClient.getInstance()
                .create(MealApiService.class);
    }

    @Override
    public void fetchRandomMeal(DataSourceCallback<MealResponse> callback) {
        api.getRandomMeal().enqueue(wrap(callback));
    }

    @Override
    public void fetchMealsByCategory(String category, DataSourceCallback<MealResponse> callback) {
        api.getMealsByCategory(category).enqueue(wrap(callback));
    }

    @Override
    public void fetchMealsByCountry(String country, DataSourceCallback<MealResponse> callback) {
        api.getMealsByArea(country).enqueue(wrap(callback));
    }

    @Override
    public void fetchMealDetails(String mealId, DataSourceCallback<MealResponse> callback) {
        api.getMealDetails(mealId).enqueue(wrap(callback));
    }

    private Callback<MealResponse> wrap(DataSourceCallback<MealResponse> cb) {
        return new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    cb.onSuccess(resp.body());
                } else {
                    cb.onError("Server error: " + resp.code());
                }
            }
            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        };
    }
}