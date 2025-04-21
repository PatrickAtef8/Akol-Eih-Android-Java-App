package com.example.akoleih.auth.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.akoleih.R;
import com.example.akoleih.auth.contract.SignUpContract;
import com.example.akoleih.auth.presenter.SignUpPresenter;
import com.example.akoleih.auth.presenter.SignUpPresenterImpl;

public class SignUpActivity extends AppCompatActivity implements SignUpContract.View {

    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private SignUpPresenter signupPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);
        Button signUpButton = findViewById(R.id.btnSignUp);

        signupPresenter = new SignUpPresenterImpl(this);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            signupPresenter.signUp(email, password);
        });
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
        Toast.makeText(this, "Sign up successful! Please Login", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSignUpError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}
