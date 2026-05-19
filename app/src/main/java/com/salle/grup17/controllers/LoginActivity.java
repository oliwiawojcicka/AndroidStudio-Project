package com.salle.grup17.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.salle.grup17.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button loginBtn, registerBtn;
    private TextView errorMessage;  // add to XML: android:id="@+id/loginErrorMessage"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ── Persistent login: if user is already logged in, skip LoginActivity ──
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            goToMainApp();
            return;
        }

        setContentView(R.layout.activity_login);

        emailField    = findViewById(R.id.emailInput);
        passwordField = findViewById(R.id.passwordInput);
        loginBtn      = findViewById(R.id.loginBtn);
        registerBtn   = findViewById(R.id.registerBtn);
        errorMessage  = findViewById(R.id.loginErrorMessage);

        loginBtn.setOnClickListener(v -> loginUser());
        registerBtn.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email    = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        hideError();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            showError("Please enter your email and password.");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            showError("Please enter your email address.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("Please enter your password.");
            return;
        }

        loginBtn.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginBtn.setEnabled(true);

                    if (task.isSuccessful()) {
                        goToMainApp();
                    } else {
                        showError(getLoginErrorMessage(task.getException()));
                    }
                });
    }

    private String getLoginErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException) {
            return "No account found for this email address.";
        }
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Incorrect password. Please try again.";
        }
        if (e != null && e.getMessage() != null &&
                e.getMessage().contains("network")) {
            return "No internet connection. Please check your network.";
        }
        return "Login failed. Please try again.";
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    private void goToMainApp() {
        startActivity(new Intent(LoginActivity.this, MainAppActivity.class));
        finish();
    }
}