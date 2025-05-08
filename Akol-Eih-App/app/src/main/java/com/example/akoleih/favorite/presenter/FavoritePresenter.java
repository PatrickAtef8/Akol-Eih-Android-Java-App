// FavoritePresenter.java
package com.example.akoleih.favorite.presenter;

import androidx.lifecycle.LifecycleOwner;
import com.example.akoleih.favorite.model.FavoriteMeal;

public interface FavoritePresenter {
    void attachView(FavoriteView view, LifecycleOwner lifecycleOwner);
    void detachView();
    void loadFavorites();
    void addFavorite(FavoriteMeal meal);
    void deleteFavorite(FavoriteMeal meal);
    void undoLastDelete();
    void onMealClicked(FavoriteMeal meal);
}