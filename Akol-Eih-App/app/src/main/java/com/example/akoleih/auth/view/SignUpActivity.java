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

import com.example.akoleih.R;
import com.example.akoleih.auth.model.AuthRepositoryImpl;
import com.example.akoleih.auth.presenter.SignUpPresenterImpl;

public class SignUpActivity extends AppCompatActivity implements SignUpView {
    private static final int RC_GOOGLE_SIGN_IN = 123;

    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private SignUpPresenterImpl signUpPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);
        Button signUpButton = findViewById(R.id.btnSignUp);
        Button googleSignInButton = findViewById(R.id.btnGoogleSignIn);
        TextView loginText = findViewById(R.id.textViewLogin);

        signUpPresenter = new SignUpPresenterImpl(this, new AuthRepositoryImpl(this));

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            signUpPresenter.signUp(email, password);
        });

        googleSignInButton.setOnClickListener(v -> {
            signUpPresenter.signInWithGoogle(this);
        });

        loginText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSignUpError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}