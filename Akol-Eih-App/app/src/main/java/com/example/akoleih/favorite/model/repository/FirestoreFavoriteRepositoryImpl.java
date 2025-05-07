package com.example.akoleih.favorite.model.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirestoreFavoriteRepositoryImpl implements FavoriteRepository {
    private static final String TAG = "FirestoreFavoriteRepo";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final MutableLiveData<List<FavoriteMeal>> favoritesLiveData = new MutableLiveData<>();
    private ListenerRegistration listener;

    private CollectionReference favRef() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return db.collection("users").document(uid).collection("favorites");
    }

    @Override
    public LiveData<List<FavoriteMeal>> getFavorites() {
        if (listener == null) {
            listener = favRef().addSnapshotListener((snap, e) -> {
                if (e != null) {
                    Log.e(TAG, "Firestore favorites listener failed", e);
                    favoritesLiveData.postValue(new ArrayList<>());
                    return;
                }
                List<FavoriteMeal> list = new ArrayList<>();
                Set<String> uniqueIds = new HashSet<>();
                if (snap != null) {
                    for (var doc : snap.getDocuments()) {
                        String mealId = doc.getId();
                        if (uniqueIds.add(mealId)) {
                            Map<String, Object> data = doc.getData();
                            if (data != null) {
                                String strMeal = (String) data.get("strMeal");
                                String strMealThumb = (String) data.get("strMealThumb");
                                if (strMeal != null && strMealThumb != null) {
                                    FavoriteMeal m = new FavoriteMeal(mealId, strMeal, strMealThumb);
                                    list.add(m);
                                } else {
                                    Log.w(TAG, "Deleting invalid favorite document: " + mealId);
                                    doc.getReference().delete();
                                }
                            }
                        } else {
                            Log.w(TAG, "Deleting duplicate favorite document: " + mealId);
                            doc.getReference().delete();
                        }
                    }
                }
                Log.d(TAG, "Fetched " + list.size() + " unique favorites via LiveData");
                favoritesLiveData.postValue(list);
            });
        }
        return favoritesLiveData;
    }

    @Override
    public LiveData<FavoriteMeal> getFavoriteById(String id) {
        MutableLiveData<FavoriteMeal> live = new MutableLiveData<>();
        if (id == null) {
            Log.e(TAG, "Invalid meal ID for getFavoriteById");
            live.postValue(null);
            return live;
        }
        favRef().document(id).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> data = doc.getData();
                        if (data != null) {
                            String strMeal = (String) data.get("strMeal");
                            String strMealThumb = (String) data.get("strMealThumb");
                            if (strMeal != null && strMealThumb != null) {
                                FavoriteMeal m = new FavoriteMeal(doc.getId(), strMeal, strMealThumb);
                                live.postValue(m);
                            } else {
                                Log.w(TAG, "Deleting invalid favorite document: " + id);
                                doc.getReference().delete();
                                live.postValue(null);
                            }
                        } else {
                            live.postValue(null);
                        }
                    } else {
                        live.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch favorite by ID: " + id, e);
                    live.postValue(null);
                });
        return live;
    }

    @Override
    public void addFavorite(FavoriteMeal meal) {
        if (meal == null || meal.getIdMeal() == null || meal.getStrMeal() == null || meal.getStrMealThumb() == null) {
            Log.e(TAG, "Invalid FavoriteMeal, cannot add to Firestore");
            return;
        }
        favRef()
                .document(meal.getIdMeal())
                .set(Map.of(
                        "strMeal", meal.getStrMeal(),
                        "strMealThumb", meal.getStrMealThumb()
                ))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite added to Firestore: " + meal.getIdMeal()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add favorite to Firestore: " + meal.getIdMeal(), e));
    }

    @Override
    public void removeFavorite(FavoriteMeal meal) {
        if (meal == null || meal.getIdMeal() == null) {
            Log.e(TAG, "Invalid FavoriteMeal, cannot remove from Firestore");
            return;
        }
        favRef()
                .document(meal.getIdMeal())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite removed from Firestore: " + meal.getIdMeal()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to remove favorite from Firestore: " + meal.getIdMeal(), e));
    }

    @Override
    public void syncFavoritesFromFirestore(DataCallback<Void> callback) {
        // Not needed, as SyncFavoriteRepositoryImpl handles Firestore-to-Room sync
        callback.onSuccess(null);
    }

    @Override
    public void syncFavoritesOnStartup(Context context) {
        // Not needed, as SyncFavoriteRepositoryImpl handles startup sync
    }

    @Override
    public void clearFavoriteData() {
        favRef().get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                    Log.d(TAG, "Cleared all favorite data from Firestore");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to clear Firestore favorites", e));
    }

    public void cleanup() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }
}