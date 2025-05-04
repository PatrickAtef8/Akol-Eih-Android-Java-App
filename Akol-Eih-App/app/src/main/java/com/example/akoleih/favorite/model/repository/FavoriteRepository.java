package com.example.akoleih.favorite.model.repository;


import androidx.lifecycle.LiveData;

import com.example.akoleih.favorite.model.FavoriteMeal;

import java.util.List;

public interface FavoriteRepository {
    LiveData<List<FavoriteMeal>> getFavorites();

    LiveData<FavoriteMeal> getFavoriteById(String id);

    void addFavorite(FavoriteMeal meal);
    void removeFavorite(FavoriteMeal meal);
}
