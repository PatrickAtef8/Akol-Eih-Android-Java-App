// HomeRepositoryImpl.java
package com.example.akoleih.home.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.network.api.CategoriesRemoteDataSource;
import com.example.akoleih.home.network.api.MealRemoteDataSource;
import com.example.akoleih.home.network.model.CategoriesResponse;
import com.example.akoleih.home.network.model.MealResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepositoryImpl implements HomeRepository {
    private final CategoriesRemoteDataSource categoriesRemoteDataSource;
    private final MealRemoteDataSource mealRemoteDataSource;
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Meal> randomMealLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Meal>> mealsByCategoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<Meal> mealDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public HomeRepositoryImpl(CategoriesRemoteDataSource categoriesRemoteDataSource,
                              MealRemoteDataSource mealRemoteDataSource) {
        this.categoriesRemoteDataSource = categoriesRemoteDataSource;
        this.mealRemoteDataSource = mealRemoteDataSource;
    }

    @Override
    public LiveData<List<Category>> getCategories() {
        categoriesRemoteDataSource.getCategories().enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCategories() != null) {
                    categoriesLiveData.setValue(response.body().getCategories());
                } else {
                    categoriesLiveData.setValue(new ArrayList<>());
                    errorLiveData.setValue("No categories found");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                categoriesLiveData.setValue(new ArrayList<>());
                errorLiveData.setValue(t.getMessage());
            }
        });
        return categoriesLiveData;
    }

    @Override
    public LiveData<Meal> getRandomMeal() {
        mealRemoteDataSource.getRandomMeal().enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getMeals().isEmpty()) {
                    randomMealLiveData.setValue(response.body().getMeals().get(0));
                } else {
                    errorLiveData.setValue("Failed to load random meal");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
        return randomMealLiveData;
    }

    @Override
    public LiveData<List<Meal>> getMealsByCategory(String category) {
        mealRemoteDataSource.getMealsByCategory(category).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealsByCategoryLiveData.setValue(response.body().getMeals());
                } else {
                    mealsByCategoryLiveData.setValue(new ArrayList<>());
                    errorLiveData.setValue("No meals found for this category");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                mealsByCategoryLiveData.setValue(new ArrayList<>());
                errorLiveData.setValue(t.getMessage());
            }
        });
        return mealsByCategoryLiveData;
    }

    @Override
    public LiveData<Meal> getMealDetails(String mealId) {
        mealRemoteDataSource.getMealDetails(mealId).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getMeals().isEmpty()) {
                    mealDetailsLiveData.setValue(response.body().getMeals().get(0));
                } else {
                    errorLiveData.setValue("Meal details not found");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                errorLiveData.setValue(t.getMessage());
            }
        });
        return mealDetailsLiveData;
    }

    @Override
    public LiveData<String> getError() {
        return errorLiveData;
    }
}