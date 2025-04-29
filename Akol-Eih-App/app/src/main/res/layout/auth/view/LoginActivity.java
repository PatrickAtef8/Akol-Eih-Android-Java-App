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

import com.example.akoleih.home.view.HomeActivity;
import com.example.akoleih.R;
import com.example.akoleih.auth.contract.LoginContract;
import com.example.akoleih.auth.presenter.LoginPresenter;
import com.example.akoleih.auth.presenter.LoginPresenterImpl;


public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);
        Button loginButton = findViewById(R.id.btnLogin);
        TextView signupText = findViewById(R.id.btnSignUp); // <-- Link to sign up text

        loginPresenter = new LoginPresenterImpl(this,);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            loginPresenter.login(email, password);
        });

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
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
    public void onLoginSuccess() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onLoginError(String message) {
        Toast.makeText(this, "Login failed: " + message, Toast.LENGTH_LONG).show();
    }
}
