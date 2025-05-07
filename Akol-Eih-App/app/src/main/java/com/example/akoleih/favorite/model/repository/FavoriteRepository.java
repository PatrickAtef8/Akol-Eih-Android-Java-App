package com.example.akoleih.favorite.model.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.favorite.model.FavoriteMeal;
import java.util.List;

public interface FavoriteRepository {
    LiveData<List<FavoriteMeal>> getFavorites();
    LiveData<FavoriteMeal> getFavoriteById(String id);
    void addFavorite(FavoriteMeal meal);
    void removeFavorite(FavoriteMeal meal);
    void syncFavoritesFromFirestore(DataCallback<Void> callback);
    void syncFavoritesOnStartup(Context context);
    void clearFavoriteData();
}