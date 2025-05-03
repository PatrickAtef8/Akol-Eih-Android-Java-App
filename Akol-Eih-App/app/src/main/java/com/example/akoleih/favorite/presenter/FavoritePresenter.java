package com.example.akoleih.favorite.presenter;

import androidx.lifecycle.LiveData;
import com.example.akoleih.favorite.model.FavoriteMeal;
import java.util.List;

public interface FavoritePresenter {
    void attachView(FavoriteView view);
    void detachView();
    void loadFavorites();
    void addFavorite(FavoriteMeal meal);
    void deleteFavorite(FavoriteMeal meal);
    LiveData<List<FavoriteMeal>> getFavoritesLiveData();
}