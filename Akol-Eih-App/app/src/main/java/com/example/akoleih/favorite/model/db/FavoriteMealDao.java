package com.example.akoleih.favorite.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.akoleih.favorite.model.FavoriteMeal;

import java.util.List;

@Dao
public interface FavoriteMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteMeal meal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FavoriteMeal> meals);

    @Delete
    void delete(FavoriteMeal meal);

    @Query("DELETE FROM favorites")
    void deleteAll();

    @Query("SELECT * FROM favorites")
    LiveData<List<FavoriteMeal>> getAllFavorites();

    @Query("SELECT * FROM favorites WHERE idMeal = :id")
    LiveData<FavoriteMeal> getFavoriteById(String id);
}