// FavoritePresenterImpl.java
package com.example.akoleih.favorite.presenter;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.FavoriteRepository;
import com.example.akoleih.favorite.view.navigation.FavoriteNavigator;
import java.util.List;

public class FavoritePresenterImpl implements FavoritePresenter {
    private FavoriteView view;
    private FavoriteNavigator navigator;
    private LifecycleOwner lifecycleOwner;
    private final FavoriteRepository repo;
    private final LiveData<List<FavoriteMeal>> favoritesLiveData;

    private FavoriteMeal lastRemovedMeal;
    private int lastRemovedPosition;

    public FavoritePresenterImpl(FavoriteRepository repo, FavoriteNavigator navigator) {
        this.repo = repo;
        this.navigator = navigator;
        this.favoritesLiveData = repo.getFavorites();
    }

    @Override
    public void attachView(FavoriteView view, LifecycleOwner lifecycleOwner) {
        this.view = view;
        this.lifecycleOwner = lifecycleOwner;
        loadFavorites();
    }

    @Override
    public void detachView() {
        this.view = null;
        this.lifecycleOwner = null;
    }

    @Override
    public void loadFavorites() {
        if (lifecycleOwner == null || view == null) return;

        favoritesLiveData.observe(lifecycleOwner, meals -> {
            if (view == null) return;

            if (meals == null || meals.isEmpty()) {
                view.showEmptyState();
            } else {
                view.showFavorites(meals);
            }
        });
    }

    @Override
    public void addFavorite(FavoriteMeal meal) {
        if (repo != null) {
            repo.addFavorite(meal);
        }
    }

    @Override
    public void deleteFavorite(FavoriteMeal meal) {
        if (repo != null) {
            repo.removeFavorite(meal);
            this.lastRemovedMeal = meal;
            view.showUndoOption();
        }
    }

    @Override
    public void undoLastDelete() {
        if (lastRemovedMeal != null) {
            addFavorite(lastRemovedMeal);
            view.restoreFavorite(lastRemovedMeal, lastRemovedPosition);
            lastRemovedMeal = null;
        }
    }

    @Override
    public void onMealClicked(FavoriteMeal meal) {
        if (navigator != null) {
            navigator.navigateToMealDetails(meal.getIdMeal());
        }
    }
}