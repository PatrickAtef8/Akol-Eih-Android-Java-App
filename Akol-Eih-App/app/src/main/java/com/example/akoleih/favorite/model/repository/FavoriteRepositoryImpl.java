package com.example.akoleih.favorite.model.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.favorite.model.db.AppDatabase;
import com.example.akoleih.favorite.model.db.FavoriteMealDao;
import com.example.akoleih.favorite.model.FavoriteMeal;
import java.util.List;

public class FavoriteRepositoryImpl implements FavoriteRepository {
    private final FavoriteMealDao dao;

    public FavoriteRepositoryImpl(AppDatabase db) {
        dao = db.favoriteMealDao();
    }

    @Override
    public LiveData<List<FavoriteMeal>> getFavorites() {
        return dao.getAllFavorites();
    }

    @Override
    public LiveData<FavoriteMeal> getFavoriteById(String id) {
        return dao.getFavoriteById(id);
    }

    @Override
    public void addFavorite(FavoriteMeal meal) {
        AsyncTask.execute(() -> dao.insert(meal));
    }

    @Override
    public void removeFavorite(FavoriteMeal meal) {
        AsyncTask.execute(() -> dao.delete(meal));
    }

    @Override
    public void syncFavoritesFromFirestore(DataCallback<Void> callback) {

    }

    @Override
    public void syncFavoritesOnStartup(Context context) {

    }

    @Override
    public void clearFavoriteData() {

    }
}