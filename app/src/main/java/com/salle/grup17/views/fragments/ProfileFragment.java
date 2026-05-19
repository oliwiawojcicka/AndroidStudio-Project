package com.salle.grup17.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.salle.grup17.R;
import com.salle.grup17.controllers.LoginActivity;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView emailText       = view.findViewById(R.id.profileEmail);
        Button logoutBtn         = view.findViewById(R.id.logoutBtn);
        Button changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        Button changeEmailBtn    = view.findViewById(R.id.changeEmailBtn);
        Button deleteAccountBtn  = view.findViewById(R.id.deleteAccountBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailText.setText(user.getEmail());
        }

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog());
        changeEmailBtn.setOnClickListener(v -> showChangeEmailDialog());
        deleteAccountBtn.setOnClickListener(v -> showDeleteAccountDialog());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void showError(TextView errorView, String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }

    private void hideError(TextView errorView) {
        errorView.setVisibility(View.GONE);
    }

    // ── Change Password ───────────────────────────────────────────────────────

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_two_fields, null);

        EditText field1      = dialogView.findViewById(R.id.dialogField1);
        EditText field2      = dialogView.findViewById(R.id.dialogField2);
        TextView errorView   = dialogView.findViewById(R.id.dialogErrorMessage); // add to dialog_two_fields.xml

        field1.setHint("Current password");
        field2.setHint("New password");
        field1.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        field2.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", null) // set null to override auto-dismiss
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(v -> {
                String currentPass = field1.getText().toString().trim();
                String newPass     = field2.getText().toString().trim();

                hideError(errorView);

                if (currentPass.isEmpty() && newPass.isEmpty()) {
                    showError(errorView, "Please fill in all fields.");
                    return;
                }
                if (currentPass.isEmpty()) {
                    showError(errorView, "Please enter your current password.");
                    return;
                }
                if (newPass.isEmpty()) {
                    showError(errorView, "Please enter a new password.");
                    return;
                }
                if (newPass.length() < 6) {
                    showError(errorView, "New password must be at least 6 characters.");
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null || user.getEmail() == null) return;

                positiveBtn.setEnabled(false);

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(t -> {
                            positiveBtn.setEnabled(true);
                            if (t.isSuccessful()) {
                                dialog.dismiss();
                            } else {
                                showError(errorView, getPasswordUpdateError(t.getException()));
                            }
                        });
                    } else {
                        positiveBtn.setEnabled(true);
                        showError(errorView, "Incorrect current password.");
                    }
                });
            });
        });

        dialog.show();
    }

    private String getPasswordUpdateError(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Password is too weak. Use at least 6 characters.";
        }
        if (e instanceof FirebaseAuthRecentLoginRequiredException) {
            return "Session expired. Please log in again.";
        }
        return "Failed to update password. Please try again.";
    }

    // ── Change Email ──────────────────────────────────────────────────────────

    private void showChangeEmailDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_two_fields, null);

        EditText field1    = dialogView.findViewById(R.id.dialogField1);
        EditText field2    = dialogView.findViewById(R.id.dialogField2);
        TextView errorView = dialogView.findViewById(R.id.dialogErrorMessage);

        field1.setHint("Current password");
        field2.setHint("New email");
        field1.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        field2.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Change Email")
                .setView(dialogView)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(v -> {
                String currentPass = field1.getText().toString().trim();
                String newEmail    = field2.getText().toString().trim();

                hideError(errorView);

                if (currentPass.isEmpty() && newEmail.isEmpty()) {
                    showError(errorView, "Please fill in all fields.");
                    return;
                }
                if (currentPass.isEmpty()) {
                    showError(errorView, "Please enter your current password.");
                    return;
                }
                if (newEmail.isEmpty()) {
                    showError(errorView, "Please enter a new email address.");
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null || user.getEmail() == null) return;

                positiveBtn.setEnabled(false);

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updateEmail(newEmail).addOnCompleteListener(t -> {
                            positiveBtn.setEnabled(true);
                            if (t.isSuccessful()) {
                                TextView emailText = requireView().findViewById(R.id.profileEmail);
                                emailText.setText(newEmail);
                                dialog.dismiss();
                            } else {
                                showError(errorView, getEmailUpdateError(t.getException()));
                            }
                        });
                    } else {
                        positiveBtn.setEnabled(true);
                        showError(errorView, "Incorrect password.");
                    }
                });
            });
        });

        dialog.show();
    }

    private String getEmailUpdateError(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException) {
            return "This email address is already in use.";
        }
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid email address format.";
        }
        if (e instanceof FirebaseAuthRecentLoginRequiredException) {
            return "Session expired. Please log in again.";
        }
        return "Failed to update email. Please try again.";
    }

    // ── Delete Account ────────────────────────────────────────────────────────

    private void showDeleteAccountDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_one_field, null);

        EditText field1    = dialogView.findViewById(R.id.dialogField1);
        TextView errorView = dialogView.findViewById(R.id.dialogErrorMessage); // add to dialog_one_field.xml

        field1.setHint("Enter password to confirm");
        field1.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("This action is irreversible. Are you sure?")
                .setView(dialogView)
                .setPositiveButton("Delete", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(v -> {
                String password = field1.getText().toString().trim();

                hideError(errorView);

                if (password.isEmpty()) {
                    showError(errorView, "Please enter your password.");
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null || user.getEmail() == null) return;

                positiveBtn.setEnabled(false);

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                dialog.dismiss();
                                Intent intent = new Intent(requireContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                positiveBtn.setEnabled(true);
                                showError(errorView, "Failed to delete account. Please try again.");
                            }
                        });
                    } else {
                        positiveBtn.setEnabled(true);
                        showError(errorView, "Incorrect password.");
                    }
                });
            });
        });

        dialog.show();
    }
}