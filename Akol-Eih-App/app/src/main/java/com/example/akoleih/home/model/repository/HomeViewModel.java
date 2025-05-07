package com.example.akoleih.home.model.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.model.network.CategoryRemoteDataSourceImpl;
import com.example.akoleih.home.model.network.MealRemoteDataSourceImpl;
import com.example.akoleih.home.model.network.api.DataSourceCallback;
import com.example.akoleih.home.model.network.model.PersistentHomeLocalDataSource;

import java.util.Arrays;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final HomeRepositoryImpl repo;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Meal> randomMeal = new MutableLiveData<>();
    private final MutableLiveData<List<Meal>> mealsByCategory = new MutableLiveData<>();
    private final MutableLiveData<Meal> mealDetails = new MutableLiveData<>();
    private final MutableLiveData<List<String>> countries = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final PersistentHomeLocalDataSource localDataSource;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public HomeViewModel(Application application) {
        super(application);
        localDataSource = new PersistentHomeLocalDataSource(application);
        repo = new HomeRepositoryImpl(
                new CategoryRemoteDataSourceImpl(),
                new MealRemoteDataSourceImpl(),
                localDataSource,
                application
        );
        Log.d("HomeViewModel", "Initialized");
    }

    public LiveData<List<Category>> getCategoriesLive() { return categories; }
    public LiveData<Meal> getRandomMealLive() { return randomMeal; }
    public LiveData<List<Meal>> getMealsByCategoryLive() { return mealsByCategory; }
    public LiveData<Meal> getMealDetailsLive() { return mealDetails; }
    public LiveData<List<String>> getCountriesLive() { return countries; }
    public LiveData<String> getErrorLive() { return error; }

    public void loadCategories() {
        repo.getCategories(new DataSourceCallback<List<Category>>() {
            @Override public void onSuccess(List<Category> data) {
                localDataSource.saveCategories(data);
                mainHandler.post(() -> categories.setValue(data));
                Log.d("HomeViewModel", "Categories loaded, size: " + (data != null ? data.size() : 0));
            }
            @Override public void onError(String msg) {
                mainHandler.post(() -> error.setValue(msg));
                Log.e("HomeViewModel", "Error loading categories: " + msg);
            }
        });
    }

    public void loadRandomMeal() {
        repo.getRandomMeal(new DataSourceCallback<Meal>() {
            @Override public void onSuccess(Meal m) {
                localDataSource.cacheRandomMeal(m);
                mainHandler.post(() -> randomMeal.setValue(m));
                Log.d("HomeViewModel", "Random meal loaded: " + (m != null ? m.getName() : "null"));
            }
            @Override public void onError(String msg) {
                mainHandler.post(() -> error.setValue(msg));
                Log.e("HomeViewModel", "Error loading random meal: " + msg);
            }
        });
    }

    public void loadMealsByCategory(String category) {
        repo.getMealsByCategory(category, new DataSourceCallback<List<Meal>>() {
            @Override public void onSuccess(List<Meal> data) {
                localDataSource.saveMeals(category, data);
                mainHandler.post(() -> mealsByCategory.setValue(data));
                Log.d("HomeViewModel", "Meals loaded for category " + category + ", size: " + (data != null ? data.size() : 0));
            }
            @Override public void onError(String msg) {
                mainHandler.post(() -> error.setValue(msg));
                Log.e("HomeViewModel", "Error loading meals by category: " + msg);
            }
        });
    }

    public void loadMealsByCountry(String country) {
        repo.getMealsByCountry(country, new DataSourceCallback<List<Meal>>() {
            @Override public void onSuccess(List<Meal> data) {
                localDataSource.saveMeals(country, data);
                mainHandler.post(() -> mealsByCategory.setValue(data));
                Log.d("HomeViewModel", "Meals loaded for country " + country + ", size: " + (data != null ? data.size() : 0));
            }
            @Override public void onError(String msg) {
                mainHandler.post(() -> error.setValue(msg));
                Log.e("HomeViewModel", "Error loading meals by country: " + msg);
            }
        });
    }

    public void loadMealDetails(String mealId) {
        repo.getMealDetails(mealId, new DataSourceCallback<Meal>() {
            @Override public void onSuccess(Meal m) {
                localDataSource.cacheMealDetails(m);
                mainHandler.post(() -> mealDetails.setValue(m));
                Log.d("HomeViewModel", "Meal details loaded for " + mealId);
            }
            @Override public void onError(String msg) {
                mainHandler.post(() -> error.setValue(msg));
                Log.e("HomeViewModel", "Error loading meal details: " + msg);
            }
        });
    }

    public void loadCountries() {
        List<String> countryList = Arrays.asList(
                "American", "British", "Canadian", "Chinese",
                "Croatian", "Dutch", "Egyptian", "Filipino",
                "French", "Greek", "Indian", "Irish",
                "Italian", "Jamaican", "Japanese", "Kenyan",
                "Malaysian", "Mexican", "Moroccan", "Polish",
                "Portuguese", "Russian", "Spanish", "Thai",
                "Tunisian", "Turkish", "Ukrainian", "Uruguayan",
                "Vietnamese"
        );
        mainHandler.post(() -> countries.setValue(countryList));
        Log.d("HomeViewModel", "Countries loaded, size: " + countryList.size());
    }
}