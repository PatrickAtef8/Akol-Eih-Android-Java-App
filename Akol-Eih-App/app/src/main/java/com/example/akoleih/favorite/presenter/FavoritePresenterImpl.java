package com.example.akoleih.favorite.presenter;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.FavoriteRepository;
import java.util.List;

public class FavoritePresenterImpl implements FavoritePresenter {
    private FavoriteView view;
    private LifecycleOwner lifecycleOwner;
    private final FavoriteRepository repo;
    private final LiveData<List<FavoriteMeal>> favoritesLiveData;

    public FavoritePresenterImpl(FavoriteRepository repo) {
        this.repo = repo;
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
    public LiveData<List<FavoriteMeal>> getFavoritesLiveData() {
        return favoritesLiveData;
    }

    @Override
    public void loadFavorites() {
        if (lifecycleOwner == null || view == null) return;

        favoritesLiveData.observe(lifecycleOwner, meals -> {
            if (view == null) return;

            if (meals == null || meals.isEmpty()) {
                view.showEmpty();
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
        }
    }
}