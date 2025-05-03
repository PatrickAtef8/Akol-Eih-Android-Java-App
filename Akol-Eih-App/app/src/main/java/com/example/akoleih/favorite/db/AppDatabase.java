package com.example.akoleih.favorite.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.akoleih.favorite.model.FavoriteMeal;

@Database(entities = {FavoriteMeal.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract FavoriteMealDao favoriteMealDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"products_db"
            ).build();
        }
        return instance;
    }
}