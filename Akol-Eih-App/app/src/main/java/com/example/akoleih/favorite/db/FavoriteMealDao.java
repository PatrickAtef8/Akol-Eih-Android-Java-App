package com.example.akoleih.favorite.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.akoleih.favorite.model.FavoriteMeal;

import java.util.List;

@Dao
public interface FavoriteMealDao {
    @Insert
    void insert(FavoriteMeal meal);

    @Delete
    void delete(FavoriteMeal meal);

    @Query("SELECT * FROM favorites")
    LiveData<List<FavoriteMeal>> getAllFavorites();
}
