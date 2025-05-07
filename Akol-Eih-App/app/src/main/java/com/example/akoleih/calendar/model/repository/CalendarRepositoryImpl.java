package com.example.akoleih.calendar.model.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.model.db.CalendarMealDao;
import com.example.akoleih.favorite.model.db.AppDatabase;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarRepositoryImpl implements CalendarRepository {
    private static final String TAG = "CalendarRepo";
    private final CalendarMealDao dao;
    private final FirebaseService firebaseService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler;

    public CalendarRepositoryImpl(Context context, FirebaseService firebaseService) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.dao = db.calendarMealDao();
        this.firebaseService = firebaseService;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public LiveData<List<CalendarMeal>> getMealsForDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        long end = cal.getTimeInMillis();

        return dao.getMealsByDateRange(start, end);
    }

    @Override
    public LiveData<Set<Long>> getPlannedDates() {
        return Transformations.map(dao.getAllPlannedDates(), dates -> new HashSet<>(dates));
    }

    @Override
    public void addMeal(CalendarMeal meal) {
        executor.execute(() -> {
            dao.insert(meal);
            String uid = firebaseService.getCurrentUserId();
            if (uid != null) {
                firebaseService.saveMeal(uid, meal.getMealId() + "_" + meal.getDate(), meal)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Meal added to Firestore: " + meal.getMealId()))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to add meal to Firestore: " + meal.getMealId(), e));
            } else {
                Log.w(TAG, "No user logged in, meal not synced to Firestore");
            }
        });
    }

    @Override
    public void removeMeal(CalendarMeal meal) {
        executor.execute(() -> {
            dao.delete(meal);
            String uid = firebaseService.getCurrentUserId();
            if (uid != null) {
                firebaseService.deleteMeal(uid, meal.getMealId() + "_" + meal.getDate())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Meal removed from Firestore: " + meal.getMealId()))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to remove meal from Firestore: " + meal.getMealId(), e));
            } else {
                Log.w(TAG, "No user logged in, meal removal not synced to Firestore");
            }
        });
    }

    @Override
    public void syncCalendarFromFirestore(DataCallback<Void> callback) {
        String uid = firebaseService.getCurrentUserId();
        if (uid == null) {
            Log.e(TAG, "User not authenticated for Firestore sync");
            mainHandler.post(() -> callback.onFailure("User not authenticated"));
            return;
        }
        Log.d(TAG, "Starting Firestore calendar sync for UID: " + uid);
        firebaseService.getCalendarMeals(uid)
                .addOnSuccessListener(querySnapshot -> {
                    List<CalendarMeal> meals = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        CalendarMeal meal = doc.toObject(CalendarMeal.class);
                        if (meal != null) {
                            meals.add(meal);
                        }
                    }
                    Log.d(TAG, "Fetched " + meals.size() + " calendar meals from Firestore");
                    executor.execute(() -> {
                        dao.deleteAll();
                        if (!meals.isEmpty()) {
                            dao.insertAll(meals);
                            Log.d(TAG, "Inserted " + meals.size() + " calendar meals into Room");
                        } else {
                            Log.d(TAG, "No calendar meals in Firestore, cleared Room");
                        }
                        mainHandler.post(() -> callback.onSuccess(null));
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch calendar meals from Firestore", e);
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                });
    }

    @Override
    public void syncCalendarOnStartup(Context context) {
        String uid = firebaseService.getCurrentUserId();
        if (uid != null) {
            syncCalendarFromFirestore(new DataCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "Calendar synced on startup for UID: " + uid);
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
    public void clearCalendarData() {
        executor.execute(() -> {
            dao.deleteAll();
            Log.d(TAG, "Cleared all calendar data from Room");
            String uid = firebaseService.getCurrentUserId();
            if (uid != null) {
                firebaseService.getCalendarMeals(uid)
                        .addOnSuccessListener(querySnapshot -> {
                            for (var doc : querySnapshot) {
                                doc.getReference().delete();
                            }
                            Log.d(TAG, "Cleared all calendar data from Firestore");
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to clear Firestore calendar", e));
            }
        });
    }
}