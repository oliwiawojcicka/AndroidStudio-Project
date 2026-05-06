package com.salle.grup17.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
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

        TextView emailText = view.findViewById(R.id.profileEmail);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        Button changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        Button changeEmailBtn = view.findViewById(R.id.changeEmailBtn);
        Button deleteAccountBtn = view.findViewById(R.id.deleteAccountBtn);

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

    // password change
    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_two_fields, null);

        EditText field1 = dialogView.findViewById(R.id.dialogField1);
        EditText field2 = dialogView.findViewById(R.id.dialogField2);
        field1.setHint("Current password");
        field2.setHint("New password");
        field1.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        field2.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String currentPass = field1.getText().toString().trim();
                    String newPass = field2.getText().toString().trim();

                    if (currentPass.isEmpty() || newPass.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 6) {
                        Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null || user.getEmail() == null) return;

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Password changed!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Error: " + t.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), "Wrong current password", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // email change
    private void showChangeEmailDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_two_fields, null);

        EditText field1 = dialogView.findViewById(R.id.dialogField1);
        EditText field2 = dialogView.findViewById(R.id.dialogField2);
        field1.setHint("Current password");
        field2.setHint("New email");
        field1.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        field2.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Email")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String currentPass = field1.getText().toString().trim();
                    String newEmail = field2.getText().toString().trim();

                    if (currentPass.isEmpty() || newEmail.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null || user.getEmail() == null) return;

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updateEmail(newEmail).addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    TextView emailText = requireView().findViewById(R.id.profileEmail);
                                    emailText.setText(newEmail);
                                    Toast.makeText(requireContext(), "Email changed!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Error: " + t.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // account delete
    private void showDeleteAccountDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_one_field, null);

        EditText field1 = dialogView.findViewById(R.id.dialogField1);
        field1.setHint("Enter password to confirm");
        field1.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("This action is irreversible. Are you sure?")
                .setView(dialogView)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String password = field1.getText().toString().trim();

                    if (password.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null || user.getEmail() == null) return;

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.delete().addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(requireContext(), "Error: " + t.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}