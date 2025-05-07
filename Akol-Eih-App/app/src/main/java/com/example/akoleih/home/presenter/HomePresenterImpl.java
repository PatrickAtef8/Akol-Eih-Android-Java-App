package com.example.akoleih.home.presenter;

import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.model.repository.HomeRepository;
import com.example.akoleih.home.model.network.api.DataSourceCallback;
import com.example.akoleih.home.view.homeview.HomeView;

import java.util.List;

public class HomePresenterImpl implements HomePresenter {
    private HomeView view;
    private final HomeRepository repo;

    public HomePresenterImpl(HomeView view, HomeRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    @Override
    public void getCategories() {
        repo.getCategories(new DataSourceCallback<List<Category>>() {
            @Override public void onSuccess(List<Category> categories) {
                if (view != null) view.showCategories(categories);
            }
            @Override public void onError(String message) {
                if (view != null) view.showError(message);
            }
        });
    }

    @Override
    public void getRandomMeal() {
        repo.getRandomMeal(new DataSourceCallback<Meal>() {
            @Override public void onSuccess(Meal meal) {
                if (view != null) view.showRandomMeal(meal);
            }
            @Override public void onError(String message) {
                if (view != null) view.showError(message);
            }
        });
    }

    @Override
    public void getMealsByCategory(String category) {
        repo.getMealsByCategory(category, new DataSourceCallback<List<Meal>>() {
            @Override public void onSuccess(List<Meal> meals) {
                if (view != null) view.showMealsByCategory(meals);
            }
            @Override public void onError(String message) {
                if (view != null) view.showError(message);
            }
        });
    }
    @Override
    public void getMealDetails(String mealId) {
        repo.getMealDetails(mealId, new DataSourceCallback<Meal>() {
            @Override public void onSuccess(Meal meal) {
                if (view != null) view.showMealDetails(meal);
            }
            @Override public void onError(String message) {
                if (view != null) view.showError(message);
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}