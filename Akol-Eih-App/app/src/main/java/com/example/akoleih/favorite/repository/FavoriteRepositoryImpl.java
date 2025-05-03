package com.example.akoleih.favorite.repository;

import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.akoleih.favorite.db.AppDatabase;
import com.example.akoleih.favorite.db.FavoriteMealDao;
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
    public void addFavorite(FavoriteMeal meal) {
        AsyncTask.execute(() -> dao.insert(meal));
    }

    @Override
    public void removeFavorite(FavoriteMeal meal) {
        AsyncTask.execute(() -> dao.delete(meal));
    }
}