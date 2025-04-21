package com.example.akoleih.splash.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.akoleih.MainActivity;
import com.example.akoleih.R;
import com.example.akoleih.splash.contract.SplashContract;
import com.example.akoleih.splash.presenter.SplashPresenterImpl;
import com.example.akoleih.splash.presenter.SplashPresenterInterface;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    private SplashPresenterInterface presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        presenter = new SplashPresenterImpl(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.checkUserSession();
            }
            },2000);
        }

        @Override
    public void navigateToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void navigateToLogin() {
        //to be implemented
//        finish();
    }
}
