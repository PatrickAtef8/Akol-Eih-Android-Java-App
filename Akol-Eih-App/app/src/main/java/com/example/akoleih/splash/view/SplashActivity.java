package com.example.akoleih.splash.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.example.akoleih.NavigationActivity;
import com.example.akoleih.R;
import com.example.akoleih.auth.view.LoginActivity;
import com.example.akoleih.splash.presenter.SplashPresenterImpl;

public class SplashActivity extends AppCompatActivity implements SplashView {

    private static final String TAG = "SplashActivity";
    private SplashPresenterImpl presenter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView splashImageView;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImageView = findViewById(R.id.splashImageView);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        presenter = new SplashPresenterImpl();
        presenter.setView(this);

        LottieCompositionFactory.fromRawRes(this, R.raw.lottiesplash)
                .addListener(composition -> {
                    if (composition != null) {
                        lottieAnimationView.setComposition(composition);
                    } else {
                        Log.e(TAG, "Lottie composition is null");
                        proceedToSessionCheck();
                    }
                })
                .addFailureListener(throwable -> {
                    Log.e(TAG, "Failed to load Lottie animation", throwable);
                    proceedToSessionCheck();
                });

        handler.postDelayed(() -> {
            splashImageView.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
        }, 1500);

        handler.postDelayed(this::proceedToSessionCheck, 4200); // Total: 1000ms (image) + 3200ms (animation)
    }

    private void proceedToSessionCheck() {
        presenter.checkUserSession();
        finishAfterNavigation();
    }

    private void finishAfterNavigation() {
        handler.postDelayed(this::finish, 1000);
    }

    @Override
    public void navigateToHome() {
        startActivity(new Intent(this, NavigationActivity.class));
    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        presenter.onDestroy();
    }
}