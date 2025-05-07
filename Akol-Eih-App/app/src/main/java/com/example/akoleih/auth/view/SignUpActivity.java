// SignUpActivity.java
package com.example.akoleih.auth.view;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.akoleih.auth.model.repository.AuthRepositoryImpl;
import com.example.akoleih.auth.presenter.SignUpPresenterImpl;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements SignUpView {
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LOGGED_IN = "loggedIn";

    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private SignUpPresenterImpl signUpPresenter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) shared‑prefs check first
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_LOGGED_IN, false)) {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
            return;
        }

        // 2) also check FirebaseAuth if desired
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply();
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_signup);

        emailEditText        = findViewById(R.id.editTextEmail);
        passwordEditText     = findViewById(R.id.editTextPassword);
        progressBar          = findViewById(R.id.progressBar);
        Button signUpButton       = findViewById(R.id.btnSignUp);
        Button googleSignInButton = findViewById(R.id.btnGoogleSignIn);
        TextView loginText        = findViewById(R.id.textViewLogin);

        signUpPresenter = new SignUpPresenterImpl(this, new AuthRepositoryImpl(this));

        signUpButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String email    = emailEditText.getText().toString().trim();
                                                String password = passwordEditText.getText().toString().trim();
                                                signUpPresenter.signUp(email, password);
                                            }
                                        });

                googleSignInButton.setOnClickListener(v ->
                        signUpPresenter.signInWithGoogle(this)
                );

        loginText.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            signUpPresenter.handleGoogleSignInResult(data);
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(android.view.View.GONE);
    }

    @Override
    public void onSignUpSuccess() {
        // 3) mark loggedIn
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply();

        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, NavigationActivity.class));
        finish();
    }

    @Override
    public void onSignUpError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}
