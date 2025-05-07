package com.example.akoleih.auth.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.akoleih.NavigationActivity;
import com.example.akoleih.R;
import com.example.akoleih.auth.presenter.LoginPresenter;
import com.example.akoleih.auth.presenter.LoginPresenterImpl;
import com.example.akoleih.auth.model.repository.AuthRepositoryImpl;
import com.example.akoleih.utils.SharedPrefUtil;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText    = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        progressBar      = findViewById(R.id.progressBar);
        Button loginButton = findViewById(R.id.btnLogin);
        TextView signupText = findViewById(R.id.btnSignUp);
        Button guestButton = findViewById(R.id.btnGuest);

        loginPresenter = new LoginPresenterImpl(this, new AuthRepositoryImpl(this));

        loginButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               showLoading();
                                               String email    = emailEditText.getText().toString().trim();
                                               String password = passwordEditText.getText().toString().trim();
                                               loginPresenter.login(email, password);
                                           }
                                       });

                signupText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

                    }
                });

                guestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginPresenter.loginAsGuest();

                    }
                });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onLoginSuccess() {
        hideLoading();
        String message = SharedPrefUtil.isGuestMode(this) ? "Logged in as guest" : "Login successful!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, NavigationActivity.class));
        finish();
    }

    @Override
    public void onLoginError(String message) {
        hideLoading();
        Toast.makeText(this, "Login failed: " + message, Toast.LENGTH_LONG).show();
    }
}