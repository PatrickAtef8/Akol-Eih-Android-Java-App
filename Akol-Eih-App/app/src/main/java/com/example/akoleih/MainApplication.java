package com.example.akoleih;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.akoleih.calendar.model.repository.CalendarRepositoryImpl;
import com.example.akoleih.calendar.model.repository.FirebaseService;
import com.example.akoleih.calendar.model.repository.FirebaseServiceImpl;
import com.example.akoleih.favorite.model.repository.SyncFavoriteRepositoryImpl;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        initializeAppCheck();
        configureFirestore();
        performStartupSync();
    }

    private void initializeAppCheck() {
        try {
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance());
            Log.d(TAG, "Firebase App Check initialized with Play Integrity");
        } catch (Exception e) {
            Log.e(TAG, "Firebase App Check initialization failed", e);
        }
    }

    private void configureFirestore() {
        try {
            FirebaseFirestore.getInstance().setFirestoreSettings(
                    new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(true)
                            .build()
            );
            Log.d(TAG, "Firestore configured with offline persistence");
        } catch (Exception e) {
            Log.e(TAG, "Firestore configuration failed", e);
        }
    }

    private void performStartupSync() {
        SyncFavoriteRepositoryImpl favoriteRepo = new SyncFavoriteRepositoryImpl(this);
        FirebaseService firebaseService = new FirebaseServiceImpl();
        CalendarRepositoryImpl calendarRepo = new CalendarRepositoryImpl(this, firebaseService);

        favoriteRepo.syncFavoritesOnStartup(this);
        calendarRepo.syncCalendarOnStartup(this);
    }
}