package com.example.akoleih.favorite.model.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.model.db.CalendarMealDao;
import com.example.akoleih.favorite.model.FavoriteMeal;

@Database(entities = {FavoriteMeal.class, CalendarMeal.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract FavoriteMealDao favoriteMealDao();
    public abstract CalendarMealDao calendarMealDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "products_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void clearInstance() {
        INSTANCE = null;
    }
}