package com.example.akoleih.home.model.repository;

import android.app.Application;
import android.util.Log;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.model.network.api.CategoryRemoteDataSource;
import com.example.akoleih.home.model.network.api.DataSourceCallback;
import com.example.akoleih.home.model.network.api.HomeLocalDataSource;
import com.example.akoleih.home.model.network.api.MealRemoteDataSource;
import com.example.akoleih.home.model.network.model.CategoriesResponse;
import com.example.akoleih.home.model.network.model.MealResponse;
import com.example.akoleih.home.model.network.model.PersistentHomeLocalDataSource;
import com.example.akoleih.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeRepositoryImpl implements HomeRepository {
    private final CategoryRemoteDataSource categoryDS;
    private final MealRemoteDataSource mealDS;
    private final HomeLocalDataSource localDS;
    private final Application application;

    public HomeRepositoryImpl(CategoryRemoteDataSource categoryDS,
                              MealRemoteDataSource mealDS,
                              HomeLocalDataSource localDS,
                              Application application) {
        this.categoryDS = categoryDS;
        this.mealDS = mealDS;
        this.localDS = localDS;
        this.application = application;
        Log.d("HomeRepositoryImpl", "Initialized");
    }

    @Override
    public void getCategories(DataSourceCallback<List<Category>> callback) {
        List<Category> cached = localDS.getCachedCategories();
        Log.d("HomeRepositoryImpl", "getCategories: Cache size: " + (cached != null ? cached.size() : 0));
        if (cached != null && !cached.isEmpty()) {
            callback.onSuccess(cached);
            return;
        }

        boolean isConnected = NetworkUtil.isConnected(application);
        Log.d("HomeRepositoryImpl", "getCategories: Network connected: " + isConnected);
        if (!isConnected) {
            callback.onError("NO_INTERNET");
            return;
        }

        categoryDS.fetchCategories(new DataSourceCallback<CategoriesResponse>() {
            @Override
            public void onSuccess(CategoriesResponse response) {
                List<Category> cats = response.getCategories() != null
                        ? response.getCategories()
                        : new ArrayList<>();
                localDS.saveCategories(cats);
                callback.onSuccess(cats);
                Log.d("HomeRepositoryImpl", "getCategories: Fetched " + cats.size() + " categories");
            }

            @Override
            public void onError(String message) {
                String error = isNetworkError(message) ? "NO_INTERNET" : message;
                Log.e("HomeRepositoryImpl", "getCategories: Raw error: " + message + ", Mapped error: " + error);
                callback.onError(error);
            }
        });
    }

    @Override
    public void getRandomMeal(DataSourceCallback<Meal> callback) {
        PersistentHomeLocalDataSource persistentLocalDS = (PersistentHomeLocalDataSource) localDS;
        Meal cachedMeal = persistentLocalDS.getCachedRandomMeal();
        Log.d("HomeRepositoryImpl", "getRandomMeal: Cached meal: " + (cachedMeal != null ? cachedMeal.getName() : "null"));
        if (cachedMeal != null) {
            callback.onSuccess(cachedMeal);
            return;
        }

        boolean isConnected = NetworkUtil.isConnected(application);
        Log.d("HomeRepositoryImpl", "getRandomMeal: Network connected: " + isConnected);
        if (!isConnected) {
            callback.onError("NO_INTERNET");
            return;
        }

        mealDS.fetchRandomMeal(new DataSourceCallback<MealResponse>() {
            @Override
            public void onSuccess(MealResponse response) {
                if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                    Meal meal = response.getMeals().get(0);
                    persistentLocalDS.cacheRandomMeal(meal);
                    callback.onSuccess(meal);
                    Log.d("HomeRepositoryImpl", "getRandomMeal: Fetched meal: " + meal.getName());
                } else {
                    callback.onError("No random meal found");
                    Log.e("HomeRepositoryImpl", "getRandomMeal: No meal found");
                }
            }

            @Override
            public void onError(String message) {
                String error = isNetworkError(message) ? "NO_INTERNET" : message;
                Log.e("HomeRepositoryImpl", "getRandomMeal: Raw error: " + message + ", Mapped error: " + error);
                callback.onError(error);
            }
        });
    }

    @Override
    public void getMealsByCategory(String category, DataSourceCallback<List<Meal>> callback) {
        List<Meal> cached = localDS.getCachedMeals(category);
        Log.d("HomeRepositoryImpl", "getMealsByCategory: Cache size for " + category + ": " + (cached != null ? cached.size() : 0));
        if (cached != null && !cached.isEmpty()) {
            callback.onSuccess(cached);
            return;
        }

        boolean isConnected = NetworkUtil.isConnected(application);
        Log.d("HomeRepositoryImpl", "getMealsByCategory: Network connected: " + isConnected);
        if (!isConnected) {
            callback.onError("NO_INTERNET");
            return;
        }

        mealDS.fetchMealsByCategory(category, new DataSourceCallback<MealResponse>() {
            @Override
            public void onSuccess(MealResponse response) {
                List<Meal> meals = response.getMeals() != null
                        ? response.getMeals()
                        : new ArrayList<>();
                localDS.saveMeals(category, meals);
                callback.onSuccess(meals);
                Log.d("HomeRepositoryImpl", "getMealsByCategory: Fetched " + meals.size() + " meals for " + category);
            }

            @Override
            public void onError(String message) {
                String error = isNetworkError(message) ? "NO_INTERNET" : message;
                Log.e("HomeRepositoryImpl", "getMealsByCategory: Raw error: " + message + ", Mapped error: " + error);
                callback.onError(error);
            }
        });
    }

    @Override
    public void getMealsByCountry(String country, DataSourceCallback<List<Meal>> callback) {
        List<Meal> cached = localDS.getCachedMeals(country);
        Log.d("HomeRepositoryImpl", "getMealsByCountry: Cache size for " + country + ": " + (cached != null ? cached.size() : 0));
        if (cached != null && !cached.isEmpty()) {
            callback.onSuccess(cached);
            return;
        }

        boolean isConnected = NetworkUtil.isConnected(application);
        Log.d("HomeRepositoryImpl", "getMealsByCountry: Network connected: " + isConnected);
        if (!isConnected) {
            callback.onError("NO_INTERNET");
            return;
        }

        mealDS.fetchMealsByCountry(country, new DataSourceCallback<MealResponse>() {
            @Override
            public void onSuccess(MealResponse response) {
                List<Meal> meals = response.getMeals() != null
                        ? response.getMeals()
                        : new ArrayList<>();
                localDS.saveMeals(country, meals);
                callback.onSuccess(meals);
                Log.d("HomeRepositoryImpl", "getMealsByCountry: Fetched " + meals.size() + " meals for " + country);
            }

            @Override
            public void onError(String message) {
                String error = isNetworkError(message) ? "NO_INTERNET" : message;
                Log.e("HomeRepositoryImpl", "getMealsByCountry: Raw error: " + message + ", Mapped error: " + error);
                callback.onError(error);
            }
        });
    }

    @Override
    public void getMealDetails(String mealId, DataSourceCallback<Meal> callback) {
        PersistentHomeLocalDataSource persistentLocalDS = (PersistentHomeLocalDataSource) localDS;
        Meal cachedMeal = persistentLocalDS.getCachedMealDetails(mealId);
        Log.d("HomeRepositoryImpl", "getMealDetails: Cached meal for " + mealId + ": " + (cachedMeal != null ? cachedMeal.getName() : "null"));
        if (cachedMeal != null) {
            callback.onSuccess(cachedMeal);
            return;
        }

        boolean isConnected = NetworkUtil.isConnected(application);
        Log.d("HomeRepositoryImpl", "getMealDetails: Network connected: " + isConnected);
        if (!isConnected) {
            callback.onError("NO_INTERNET");
            return;
        }

        mealDS.fetchMealDetails(mealId, new DataSourceCallback<MealResponse>() {
            @Override
            public void onSuccess(MealResponse response) {
                if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                    Meal meal = response.getMeals().get(0);
                    persistentLocalDS.cacheMealDetails(meal);
                    callback.onSuccess(meal);
                    Log.d("HomeRepositoryImpl", "getMealDetails: Fetched meal: " + meal.getName());
                } else {
                    callback.onError("Meal details not found");
                    Log.e("HomeRepositoryImpl", "getMealDetails: No meal found");
                }
            }

            @Override
            public void onError(String message) {
                String error = isNetworkError(message) ? "NO_INTERNET" : message;
                Log.e("HomeRepositoryImpl", "getMealDetails: Raw error: " + message + ", Mapped error: " + error);
                callback.onError(error);
            }
        });
    }

    private boolean isNetworkError(String message) {
        if (message == null) return false;
        String lowerMessage = message.toLowerCase();
        boolean isNetworkError = lowerMessage.contains("timeout") ||
                lowerMessage.contains("connectexception") ||
                lowerMessage.contains("connect to") ||
                lowerMessage.contains("network") ||
                lowerMessage.contains("api") ||
                lowerMessage.contains("unable to resolve host") ||
                lowerMessage.contains("failed to connect") ||
                lowerMessage.contains("can't connect to api");
        Log.d("HomeRepositoryImpl", "isNetworkError: Message: " + message + ", Is network error: " + isNetworkError);
        return isNetworkError;
    }
}