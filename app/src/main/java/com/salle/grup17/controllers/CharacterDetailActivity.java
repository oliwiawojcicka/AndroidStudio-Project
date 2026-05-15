package com.salle.grup17.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.salle.grup17.R;
import com.salle.grup17.api.RetrofitClient;
import com.salle.grup17.models.Character;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterDetailActivity extends AppCompatActivity {

    private ImageView detailImage;
    private TextView detailName;
    private TextView detailInfo;
    private Button backBtn;
    private Button favoriteBtn;
    private TextView tvStatusMessage;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Character currentCharacter;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailInfo = findViewById(R.id.detailInfo);
        backBtn = findViewById(R.id.backBtn);
        favoriteBtn = findViewById(R.id.favoriteBtn);

        tvStatusMessage = findViewById(R.id.tvStatusMessage);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(v -> finish());

        favoriteBtn.setOnClickListener(v -> {
            if (currentCharacter == null) {
                showStatusMessage("Character not loaded yet");
                return;
            }

            if (isFavorite) {
                removeFromFavorites();
            } else {
                addToFavorites();
            }
        });

        int characterId = getIntent().getIntExtra("character_id", -1);

        if (characterId == -1) {
            showStatusMessage("Character ID error");
            tvStatusMessage.postDelayed(this::finish, 1500);
            return;
        }

        loadCharacter(characterId);
    }

    private void loadCharacter(int id) {
        RetrofitClient.getApi().getCharacterById(id)
                .enqueue(new Callback<Character>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Character> call,
                            @NonNull Response<Character> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentCharacter = response.body();

                            detailName.setText(currentCharacter.getName());

                            String type = currentCharacter.getType();
                            if (type == null || type.isEmpty()) {
                                type = "Unknown";
                            }

                            String originName = "Unknown";
                            if (currentCharacter.getOrigin() != null && currentCharacter.getOrigin().getName() != null) {
                                originName = currentCharacter.getOrigin().getName();
                            }

                            String locationName = "Unknown";
                            if (currentCharacter.getLocation() != null && currentCharacter.getLocation().getName() != null) {
                                locationName = currentCharacter.getLocation().getName();
                            }

                            String info =
                                    "Status: " + currentCharacter.getStatus() + "\n" +
                                            "Species: " + currentCharacter.getSpecies() + "\n" +
                                            "Type: " + type + "\n" +
                                            "Gender: " + currentCharacter.getGender() + "\n" +
                                            "Origin: " + originName + "\n" +
                                            "Location: " + locationName;

                            detailInfo.setText(info);

                            Glide.with(CharacterDetailActivity.this)
                                    .load(currentCharacter.getImage())
                                    .into(detailImage);

                            checkIfFavorite();

                        } else {
                            showStatusMessage("API error");
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Character> call,
                            @NonNull Throwable t
                    ) {
                        showStatusMessage("Connection error: " + t.getMessage());
                    }
                });
    }

    private String getUserId() {
        if (auth.getCurrentUser() == null) {
            return null;
        }
        return auth.getCurrentUser().getUid();
    }

    private void checkIfFavorite() {
        String userId = getUserId();

        if (userId == null || currentCharacter == null) {
            favoriteBtn.setEnabled(false);
            return;
        }

        favoriteBtn.setEnabled(false);

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(String.valueOf(currentCharacter.getId()))
                .get()
                .addOnCompleteListener(task -> {
                    favoriteBtn.setEnabled(true);

                    if (task.isSuccessful() && task.getResult() != null) {
                        isFavorite = task.getResult().exists();
                        updateFavoriteButton();
                    }
                });
    }

    private void addToFavorites() {
        String userId = getUserId();

        if (userId == null) {
            showStatusMessage("User not logged in");
            return;
        }

        favoriteBtn.setEnabled(false);

        Map<String, Object> favorite = new HashMap<>();
        favorite.put("id", currentCharacter.getId());
        favorite.put("name", currentCharacter.getName());
        favorite.put("status", currentCharacter.getStatus());
        favorite.put("species", currentCharacter.getSpecies());
        favorite.put("image", currentCharacter.getImage());

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(String.valueOf(currentCharacter.getId()))
                .set(favorite)
                .addOnSuccessListener(unused -> {
                    isFavorite = true;
                    updateFavoriteButton();
                    favoriteBtn.setEnabled(true);
                    showStatusMessage("Added"); // <--- Zmiana na "Added"
                })
                .addOnFailureListener(e -> {
                    favoriteBtn.setEnabled(true);
                    showStatusMessage("Error adding favorite");
                });
    }

    private void removeFromFavorites() {
        String userId = getUserId();

        if (userId == null) {
            showStatusMessage("User not logged in");
            return;
        }

        favoriteBtn.setEnabled(false);

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(String.valueOf(currentCharacter.getId()))
                .delete()
                .addOnSuccessListener(unused -> {
                    isFavorite = false;
                    updateFavoriteButton();
                    favoriteBtn.setEnabled(true);
                    showStatusMessage("Deleted"); // <--- Zmiana na "Deleted"
                })
                .addOnFailureListener(e -> {
                    favoriteBtn.setEnabled(true);
                    showStatusMessage("Error removing favorite");
                });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            favoriteBtn.setText("Remove from favorites");
        } else {
            favoriteBtn.setText("Add to favorites");
        }
    }


    private void showStatusMessage(String message) {
        tvStatusMessage.setText(message);
        tvStatusMessage.setVisibility(View.VISIBLE);
        tvStatusMessage.removeCallbacks(null);

        tvStatusMessage.postDelayed(() -> tvStatusMessage.setVisibility(View.GONE), 2000);
    }
}