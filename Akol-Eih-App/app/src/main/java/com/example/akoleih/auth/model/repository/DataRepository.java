package com.example.akoleih.auth.model.repository;

import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.favorite.model.FavoriteMeal;
import java.util.List;
import java.util.Map;

public interface DataRepository {
    void saveUserData(Map<String, Object> data, DataCallback<Void> callback);
    void getUserData(DataCallback<Map<String, Object>> callback);
    void saveProfileData(String name, String email, String profileImageUrl, DataCallback<Void> callback);
    void saveFavorite(FavoriteMeal meal, DataCallback<Void> callback);
    void getFavorites(DataCallback<List<FavoriteMeal>> callback);
    void deleteFavorite(String mealId, DataCallback<Void> callback);
}