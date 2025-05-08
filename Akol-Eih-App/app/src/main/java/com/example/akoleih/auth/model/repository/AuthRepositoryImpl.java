package com.example.akoleih.auth.model.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.akoleih.R;
import com.example.akoleih.auth.model.callbacks.AuthCallback;
import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.calendar.model.repository.CalendarRepositoryImpl;
import com.example.akoleih.calendar.model.repository.FirebaseService;
import com.example.akoleih.calendar.model.repository.FirebaseServiceImpl;
import com.example.akoleih.favorite.model.db.AppDatabase;
import com.example.akoleih.favorite.model.repository.SyncFavoriteRepositoryImpl;
import com.example.akoleih.home.model.network.model.PersistentHomeLocalDataSource;
import com.example.akoleih.utils.SharedPrefUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthRepositoryImpl implements AuthRepository {
    private static final String TAG = "AuthRepository";
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private final FirebaseAuth auth;
    private final Context context;
    private final PersistentHomeLocalDataSource localDataSource;
    private final CalendarRepositoryImpl calendarRepository;
    private final SyncFavoriteRepositoryImpl favoriteRepository;
    private final Handler mainHandler;
    private final GoogleSignInClient googleSignInClient;

    public AuthRepositoryImpl(Context context) {
        this.context = context.getApplicationContext();
        this.auth = FirebaseAuth.getInstance();
        this.localDataSource = new PersistentHomeLocalDataSource(context);
        FirebaseService firebaseService = new FirebaseServiceImpl();
        this.calendarRepository = new CalendarRepositoryImpl(context, firebaseService);
        this.favoriteRepository = new SyncFavoriteRepositoryImpl(context);
        this.mainHandler = new Handler(Looper.getMainLooper());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    @Override
    public void signUp(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String uid = authResult.getUser().getUid();
                        Log.d(TAG, "Sign-up successful for UID: " + uid);
                        SharedPrefUtil.saveUid(context, uid);
                        SharedPrefUtil.saveGuestMode(context, false);
                        localDataSource.clearGuestData();
                        mainHandler.post(() -> callback.onSuccess());
                    } else {
                        Log.w(TAG, "Sign-up succeeded but user is null");
                        mainHandler.post(() -> callback.onFailure("User data is null"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Sign-up failed: " + e.getMessage());
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                });
    }

    @Override
    public void signInWithGoogle(Activity activity, AuthCallback callback) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void handleGoogleSignInResult(Intent data, AuthCallback callback) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException.class);
            if (account != null) {
                String idToken = account.getIdToken();
                firebaseAuthWithGoogle(idToken, callback);
            } else {
                Log.w(TAG, "Google Sign-In account is null");
                mainHandler.post(() -> callback.onFailure("Google Sign-In failed: No account"));
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google Sign-In failed: " + e.getMessage(), e);
            mainHandler.post(() -> callback.onFailure("Google Sign-In failed: " + e.getMessage()));
        }
    }

    private void firebaseAuthWithGoogle(String idToken, AuthCallback callback) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String uid = authResult.getUser().getUid();
                        Log.d(TAG, "Google Sign-In successful for UID: " + uid);
                        SharedPrefUtil.saveUid(context, uid);
                        SharedPrefUtil.saveGuestMode(context, false);
                        localDataSource.clearGuestData();
                        favoriteRepository.syncFavoritesFromFirestore(new DataCallback<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Favorites sync completed");
                                calendarRepository.syncCalendarFromFirestore(new DataCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Calendar sync completed");
                                        mainHandler.post(() -> callback.onSuccess());
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Log.e(TAG, "Calendar sync failed: " + error);
                                        mainHandler.post(() -> callback.onSuccess());
                                    }
                                });
                            }
                            @Override
                            public void onFailure(String error) {
                                Log.e(TAG, "Favorites sync failed: " + error);
                                mainHandler.post(() -> callback.onSuccess());
                            }
                        });
                    } else {
                        Log.w(TAG, "Google Sign-In succeeded but user is null");
                        mainHandler.post(() -> callback.onSuccess());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase auth with Google failed: " + e.getMessage());
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                });
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String uid = authResult.getUser().getUid();
                        Log.d(TAG, "Login successful for UID: " + uid);
                        SharedPrefUtil.saveUid(context, uid);
                        SharedPrefUtil.saveGuestMode(context, false);
                        localDataSource.clearGuestData();
                        favoriteRepository.syncFavoritesFromFirestore(new DataCallback<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Favorites sync completed");
                                calendarRepository.syncCalendarFromFirestore(new DataCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Calendar sync completed");
                                        mainHandler.post(() -> callback.onSuccess());
                                    }
                                    @Override
                                    public void onFailure(String error) {
                                        Log.e(TAG, "Calendar sync failed: " + error);
                                        mainHandler.post(() -> callback.onSuccess());
                                    }
                                });
                            }
                            @Override
                            public void onFailure(String error) {
                                Log.e(TAG, "Favorites sync failed: " + error);
                                mainHandler.post(() -> callback.onSuccess());
                            }
                        });
                    } else {
                        Log.w(TAG, "Login succeeded but user is null");
                        mainHandler.post(() -> callback.onSuccess());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login failed: " + e.getMessage());
                    mainHandler.post(() -> callback.onFailure(e.getMessage()));
                });
    }

    @Override
    public void setGuestMode(final AuthCallback callback) {
        Log.d(TAG, "Setting guest mode");
        auth.signOut();
        SharedPrefUtil.saveUid(context, null);
        SharedPrefUtil.saveGuestMode(context, true);
        localDataSource.clearGuestData();
        calendarRepository.clearCalendarData();
        favoriteRepository.clearFavoriteData();
        AppDatabase.clearInstance();
        // Clear app_prefs SharedPreferences
        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        appPrefs.edit().clear().apply();
        Log.d(TAG, "Guest mode set successfully");
        mainHandler.post(() -> callback.onSuccess());
    }

    @Override
    public void logout() {
        Log.d(TAG, "Logging out");
        // Sign out from Firebase
        auth.signOut();
        // Sign out from Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Log.d(TAG, "Google Sign-In signed out");
        });
        // Clear all SharedPreferences
        SharedPrefUtil.clearAll(context);
        // Clear app_prefs SharedPreferences
        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        appPrefs.edit().clear().apply();
        localDataSource.clearGuestData();
        calendarRepository.clearCalendarData();
        favoriteRepository.clearFavoriteData();
        AppDatabase.clearInstance();
        Log.d(TAG, "Logout completed");
    }
}