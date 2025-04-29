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

import com.example.akoleih.home.view.NavigationActivity;
import com.example.akoleih.R;
import com.example.akoleih.auth.contract.LoginContract;
import com.example.akoleih.auth.presenter.LoginPresenter;
import com.example.akoleih.auth.presenter.LoginPresenterImpl;
import com.example.akoleih.auth.model.AuthRepositoryImpl;

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
        TextView signupText = findViewById(R.id.btnSignUp);

        loginPresenter = new LoginPresenterImpl(this, new AuthRepositoryImpl(this));



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginPresenter.login(email, password);
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
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
        startActivity(new Intent(this, NavigationActivity.class));
        finish();
    }



    @Override
    public void onLoginError(String message) {
        Toast.makeText(this, "Login failed: " + message, Toast.LENGTH_LONG).show();
    }
}