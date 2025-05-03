package com.example.akoleih.home.model.repository;

import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.network.api.CategoryRemoteDataSource;
import com.example.akoleih.home.network.api.DataSourceCallback;
import com.example.akoleih.home.network.api.MealRemoteDataSource;
import com.example.akoleih.home.network.model.CategoriesResponse;
import com.example.akoleih.home.network.model.MealResponse;

import java.util.ArrayList;
import java.util.List;

public class HomeRepositoryImpl implements HomeRepository {
    private final CategoryRemoteDataSource categoryDS;
    private final MealRemoteDataSource mealDS;

    public HomeRepositoryImpl(CategoryRemoteDataSource categoryDS,
                              MealRemoteDataSource mealDS) {
        this.categoryDS = categoryDS;
        this.mealDS = mealDS;
    }

    @Override
    public void getCategories(DataSourceCallback<List<Category>> callback) {
        categoryDS.fetchCategories(new DataSourceCallback<CategoriesResponse>() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                List<Category> cats = response.getCategories() != null
                        ? response.getCategories() : new ArrayList<>();
                callback.onSuccess(cats);
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    @Override
    public void getRandomMeal(DataSourceCallback<Meal> callback) {
        mealDS.fetchRandomMeal(new DataSourceCallback<MealResponse>() {
            @Override
            public void onSuccess(MealResponse response) {
                if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                    callback.onSuccess(response.getMeals().get(0));
                } else {
                    callback.onError("No random meal found");
                }
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    @Override
    public void getMealsByCategory(String category, DataSourceCallback<List<Meal>> callback) {
        mealDS.fetchMealsByCategory(category, new DataSourceCallback<MealResponse>() {
            @Override
            public void onSuccess(MealResponse response) {
                callback.onSuccess(response.getMeals() != null
                        ? response.getMeals() : new ArrayList<>());
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    @Override
    public void getMealDetails(String mealId, DataSourceCallback<Meal> callback) {
        mealDS.fetchMealDetails(mealId, new DataSourceCallback<MealResponse>() {
            @Override
            public void onSuccess(MealResponse response) {
                if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                    callback.onSuccess(response.getMeals().get(0));
                } else {
                    callback.onError("Meal details not found");
                }
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}