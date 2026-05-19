package com.salle.grup17.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.salle.grup17.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField, confirmPasswordField;
    private Button registerBtn, backToLoginBtn;
    private TextView errorMessage;   // add to XML: android:id="@+id/registerErrorMessage"
    private TextView successMessage; // optional:   android:id="@+id/registerSuccessMessage"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailField           = findViewById(R.id.registerEmailInput);
        passwordField        = findViewById(R.id.registerPasswordInput);
        confirmPasswordField = findViewById(R.id.confirmPasswordInput);
        registerBtn          = findViewById(R.id.createAccountBtn);
        backToLoginBtn       = findViewById(R.id.backToLoginBtn);
        errorMessage         = findViewById(R.id.registerErrorMessage);
        successMessage       = findViewById(R.id.registerSuccessMessage);

        registerBtn.setOnClickListener(v -> registerUser());
        backToLoginBtn.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String email           = emailField.getText().toString().trim();
        String password        = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        hideError();

        if (TextUtils.isEmpty(email)) {
            showError("Please enter your email address.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("Please enter a password.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            showError("Please confirm your password.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        registerBtn.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    registerBtn.setEnabled(true);

                    if (task.isSuccessful()) {
                        showSuccess("Account created successfully!");
                        startActivity(new Intent(RegisterActivity.this, MainAppActivity.class));
                        finish();
                    } else {
                        showError(getRegisterErrorMessage(task.getException()));
                    }
                });
    }

    private String getRegisterErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException) {
            return "An account with this email already exists.";
        }
        if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Password is too weak. Use at least 6 characters.";
        }
        if (e != null && e.getMessage() != null &&
                e.getMessage().contains("badly formatted")) {
            return "Invalid email address format.";
        }
        if (e != null && e.getMessage() != null &&
                e.getMessage().contains("network")) {
            return "No internet connection. Please check your network.";
        }
        return "Registration failed. Please try again.";
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    private void showSuccess(String message) {
        if (successMessage != null) {
            successMessage.setText(message);
            successMessage.setVisibility(View.VISIBLE);
        }
    }
}