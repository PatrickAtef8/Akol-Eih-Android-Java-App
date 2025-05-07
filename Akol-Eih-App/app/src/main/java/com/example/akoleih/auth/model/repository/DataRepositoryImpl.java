package com.example.akoleih.auth.model.repository;

import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepositoryImpl implements DataRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public DataRepositoryImpl() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void saveUserData(Map<String, Object> data, DataCallback<Void> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }
        db.collection("users").document(user.getUid())
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void getUserData(DataCallback<Map<String, Object>> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.getData());
                    } else {
                        callback.onFailure("No data found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void saveProfileData(String name, String email, String profileImageUrl, DataCallback<Void> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", name);
        profileData.put("email", email);
        profileData.put("profileImageUrl", profileImageUrl);
        db.collection("users").document(user.getUid())
                .update(profileData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void saveFavorite(FavoriteMeal meal, DataCallback<Void> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }
        db.collection("users").document(user.getUid())
                .collection("favorites")
                .document(meal.getIdMeal())
                .set(meal)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void getFavorites(DataCallback<List<FavoriteMeal>> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }
        db.collection("users").document(user.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<FavoriteMeal> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        FavoriteMeal meal = doc.toObject(FavoriteMeal.class);
                        if (meal != null) {
                            meal.setIdMeal(doc.getId());
                            favorites.add(meal);
                        }
                    }
                    callback.onSuccess(favorites);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void deleteFavorite(String mealId, DataCallback<Void> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }
        db.collection("users").document(user.getUid())
                .collection("favorites")
                .document(mealId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}