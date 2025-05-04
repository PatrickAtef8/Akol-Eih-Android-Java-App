package com.example.akoleih.favorite.presenter;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.FavoriteRepository;
import java.util.List;

public class FavoritePresenterImpl implements FavoritePresenter {
    private FavoriteView view;
    private final FavoriteRepository repo;
    private final LiveData<List<FavoriteMeal>> favoritesLiveData;

    public FavoritePresenterImpl(FavoriteRepository repo) {
        this.repo = repo;
        this.favoritesLiveData = repo.getFavorites();
    }

    @Override
    public void attachView(FavoriteView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public LiveData<List<FavoriteMeal>> getFavoritesLiveData() {
        return favoritesLiveData;
    }

    @Override
    public void loadFavorites() {
        favoritesLiveData.observe((LifecycleOwner) view, new Observer<List<FavoriteMeal>>() {
            @Override
            public void onChanged(List<FavoriteMeal> meals) {
                if (meals == null || meals.isEmpty()) {
                    view.showEmpty();
                } else {
                    view.showFavorites(meals);
                }
            }
        });
    }

    @Override
    public void addFavorite(FavoriteMeal meal) {
        repo.addFavorite(meal);
    }

    @Override
    public void deleteFavorite(FavoriteMeal meal) {
        repo.removeFavorite(meal);
    }
}