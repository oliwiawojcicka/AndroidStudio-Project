package com.salle.grup17.controllers.fragments;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.salle.grup17.R;
import com.salle.grup17.api.RetrofitClient;
import com.salle.grup17.models.Character;
import com.salle.grup17.views.adapters.EpisodeCharacterAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EpisodeDetailActivity extends AppCompatActivity {

    private TextView epDetailName, epDetailCode, epDetailDate;
    private RecyclerView charactersHorizontalRecyclerView;
    private EpisodeCharacterAdapter characterAdapter;
    private List<Character> characterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        Button backBtn = findViewById(R.id.episodeBackBtn);
        epDetailName = findViewById(R.id.epDetailName);
        epDetailCode = findViewById(R.id.epDetailCode);
        epDetailDate = findViewById(R.id.epDetailDate);
        charactersHorizontalRecyclerView = findViewById(R.id.charactersHorizontalRecyclerView);

        backBtn.setOnClickListener(v -> finish());

        String name = getIntent().getStringExtra("episode_name");
        String code = getIntent().getStringExtra("episode_code");
        String date = getIntent().getStringExtra("episode_date");
        ArrayList<String> characterUrls = getIntent().getStringArrayListExtra("character_urls");

        epDetailName.setText(name);
        epDetailCode.setText(code);
        epDetailDate.setText(date);

        charactersHorizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        characterList = new ArrayList<>();
        characterAdapter = new EpisodeCharacterAdapter(characterList);
        charactersHorizontalRecyclerView.setAdapter(characterAdapter);

        loadEpisodeCharacters(characterUrls);
    }

    private void loadEpisodeCharacters(List<String> urls) {
        if (urls == null || urls.isEmpty()) return;

        for (String url : urls) {
            try {
                String[] parts = url.split("/");
                int characterId = Integer.parseInt(parts[parts.length - 1]);

                RetrofitClient.getApi().getCharacterById(characterId)
                        .enqueue(new Callback<Character>() {
                            @Override
                            public void onResponse(@NonNull Call<Character> call, @NonNull Response<Character> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    characterList.add(response.body());
                                    characterAdapter.notifyItemInserted(characterList.size() - 1);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Character> call, @NonNull Throwable t) {}
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}