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
    private final HomeRepository repository;

    public HomePresenter(HomeContract.View view, HomeRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getCategories() {
        LiveData<List<Category>> categoriesLiveData = repository.getCategories();
        categoriesLiveData.observeForever(new Observer<List<Category>>() {
                                              @Override
                                              public void onChanged(List<Category> categories) {
                                                  if (view != null && categories != null) {
                                                      view.showCategories(categories);
                                                  }
                                              }
                                          });

                repository.getError().observeForever(new Observer<String>() {
                    @Override
                    public void onChanged(String error) {
                        if (view != null && error != null) {
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
                                            if (view != null && meal != null) {
                                                view.showRandomMeal(meal);
                                            }
                                        }
                                    });

                repository.getError().observeForever(new Observer<String>() {
                    @Override
                    public void onChanged(String error) {
                        if (view != null && error != null) {
                            view.showError(error);
                        }
                    }
                });
    }

    @Override
    public void getMealsByCategory(String category) {
        LiveData<List<Meal>> mealsLiveData = repository.getMealsByCategory(category);
        mealsLiveData.observeForever(new Observer<List<Meal>>() {
            @Override
            public void onChanged(List<Meal> meals) {
                if (view != null && meals != null) {
                    view.showMealsByCategory(meals);
                }
            }
        });


        repository.getError().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (view != null && error != null) {
                    view.showError(error);
                }
            }
        });
    }

    @Override
    public void getMealDetails(String mealId) {
        LiveData<Meal> mealDetailsLiveData = repository.getMealDetails(mealId);
        mealDetailsLiveData.observeForever(new Observer<Meal>() {
            @Override
            public void onChanged(Meal meal) {
                if (view != null && meal != null) {
                    view.showMealDetails(meal);
                }
            }
        });

        repository.getError().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (view != null && error != null) {
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