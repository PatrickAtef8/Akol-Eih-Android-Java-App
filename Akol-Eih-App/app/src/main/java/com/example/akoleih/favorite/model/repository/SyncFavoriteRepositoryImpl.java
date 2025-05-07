package com.example.akoleih.favorite.model.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.favorite.model.db.AppDatabase;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.db.FavoriteMealDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncFavoriteRepositoryImpl implements FavoriteRepository {
    private static final String TAG = "SyncFavoriteRepo";
    private final FavoriteMealDao dao;
    private final FirestoreFavoriteRepositoryImpl firestoreRepo;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler;

    public SyncFavoriteRepositoryImpl(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        dao = db.favoriteMealDao();
        firestoreRepo = new FirestoreFavoriteRepositoryImpl();
        mainHandler = new Handler(Looper.getMainLooper());
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
        executor.execute(() -> {
            dao.insert(meal);
            firestoreRepo.addFavorite(meal);
        });
    }

    @Override
    public void removeFavorite(FavoriteMeal meal) {
        executor.execute(() -> {
            dao.delete(meal);
            firestoreRepo.removeFavorite(meal);
        });
    }

    @Override
    public void syncFavoritesFromFirestore(DataCallback<Void> callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (uid == null) {
            Log.e(TAG, "User not authenticated for Firestore sync");
            mainHandler.post(() -> callback.onFailure("User not authenticated"));
            return;
        }
        Log.d(TAG, "Starting Firestore favorites sync for UID: " + uid);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<FavoriteMeal> list = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        FavoriteMeal m = new FavoriteMeal(
                                doc.getId(),
                                doc.getString("strMeal"),
                                doc.getString("strMealThumb")
                        );
                        list.add(m);
                    }
                    Log.d(TAG, "Fetched " + list.size() + " favorites from Firestore");
                    executor.execute(() -> {
                        dao.deleteAll();
                        if (!list.isEmpty()) {
                            dao.insertAll(list);
                            Log.d(TAG, "Inserted " + list.size() + " favorites into Room");
                        } else {
                            Log.d(TAG, "No favorites in Firestore, cleared Room");
                        }
                        mainHandler.post(() -> callback.onSuccess(null));
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch favorites from Firestore", e);
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                });
    }

    @Override
    public void syncFavoritesOnStartup(Context context) {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (uid != null) {
            syncFavoritesFromFirestore(new DataCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "Favorites synced on startup for UID: " + uid);
                }
                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Startup sync failed: " + error);
                }
            });
        } else {
            Log.d(TAG, "No user logged in during startup sync");
        }
    }

    @Override
    public void clearFavoriteData() {
        executor.execute(() -> {
            dao.deleteAll();
            Log.d(TAG, "Cleared all favorite data from Room");
            String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : null;
            if (uid != null) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .collection("favorites")
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            for (var doc : querySnapshot.getDocuments()) {
                                doc.getReference().delete();
                            }
                            Log.d(TAG, "Cleared all favorite data from Firestore");
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to clear Firestore favorites", e));
            }
        });
    }
}