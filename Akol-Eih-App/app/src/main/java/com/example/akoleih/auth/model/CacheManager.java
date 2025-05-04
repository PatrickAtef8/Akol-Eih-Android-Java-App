package com.example.akoleih.auth.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CacheManager {
    private static final String CACHE_PREFS = "user_cache";
    private static final String KEY_USER = "current_user";

    public static void saveUserLocally(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply();
    }

    public static User getLocalUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return null;

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<User>(){}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearLocalUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER).apply();
    }
}