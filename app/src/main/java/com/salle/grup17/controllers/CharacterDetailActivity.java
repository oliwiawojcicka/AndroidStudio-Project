package com.salle.grup17.controllers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.salle.grup17.R;
import com.salle.grup17.api.RetrofitClient;
import com.salle.grup17.models.Character;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterDetailActivity extends AppCompatActivity {

    private ImageView detailImage;
    private TextView detailName;
    private TextView detailInfo;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailInfo = findViewById(R.id.detailInfo);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        int characterId = getIntent().getIntExtra("character_id", -1);

        if (characterId == -1) {
            Toast.makeText(this, "Character ID error", Toast.LENGTH_SHORT).show();
            finish();
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
                            Character character = response.body();

                            detailName.setText(character.getName());

                            String type = character.getType();
                            if (type == null || type.isEmpty()) {
                                type = "Unknown";
                            }

                            String originName = "Unknown";
                            if (character.getOrigin() != null && character.getOrigin().getName() != null) {
                                originName = character.getOrigin().getName();
                            }

                            String locationName = "Unknown";
                            if (character.getLocation() != null && character.getLocation().getName() != null) {
                                locationName = character.getLocation().getName();
                            }

                            String info =
                                    "Status: " + character.getStatus() + "\n" +
                                            "Species: " + character.getSpecies() + "\n" +
                                            "Type: " + type + "\n" +
                                            "Gender: " + character.getGender() + "\n" +
                                            "Origin: " + originName + "\n" +
                                            "Location: " + locationName;

                            detailInfo.setText(info);

                            Glide.with(CharacterDetailActivity.this)
                                    .load(character.getImage())
                                    .into(detailImage);
                        } else {
                            Toast.makeText(
                                    CharacterDetailActivity.this,
                                    "API error",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Character> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                CharacterDetailActivity.this,
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
