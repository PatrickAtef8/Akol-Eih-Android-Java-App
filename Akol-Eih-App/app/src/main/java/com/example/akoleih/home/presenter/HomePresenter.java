package com.example.akoleih.home.presenter;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.akoleih.home.contract.HomeContract;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.model.repository.HomeRepository;

import java.util.List;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View view;
    private HomeRepository repository;

    public HomePresenter(HomeContract.View view, HomeRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getCategories() {
        LiveData<List<Category>> categoriesLiveData = repository.getCategories();
        categoriesLiveData.observeForever(categories -> {
            if (categories != null) {
                view.showCategories(categories);
            }
        });

        repository.getError().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    view.showError(error);
                }
            }
        });
    }


    @Override
    public void getRandomMeal() {
        LiveData<Meal> mealLiveData = repository.getRandomMeal();
        mealLiveData.observeForever(new Observer<Meal>() {
            @Override
            public void onChanged(Meal meal) {
                if (meal != null) {
                    view.showRandomMeal(meal);
                }
            }
        });

        repository.getError().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    view.showError(error);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}