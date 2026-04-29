package com.salle.grup17.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.grup17.R;
import com.salle.grup17.api.RetrofitClient;
import com.salle.grup17.models.ApiResponse;
import com.salle.grup17.models.Character;
import com.salle.grup17.views.adapters.CharacterAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView charactersRecyclerView;
    private CharacterAdapter characterAdapter;
    private final ArrayList<Character> characterList = new ArrayList<>();

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        charactersRecyclerView = view.findViewById(R.id.charactersRecyclerView);
        charactersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        characterAdapter = new CharacterAdapter(characterList);
        charactersRecyclerView.setAdapter(characterAdapter);

        loadCharacters();

        return view;
    }

    private void loadCharacters() {
        RetrofitClient.getApi().getCharacters(1)
                .enqueue(new Callback<ApiResponse<Character>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<Character>> call,
                            @NonNull Response<ApiResponse<Character>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            characterList.clear();
                            characterList.addAll(response.body().getResults());
                            characterAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "API error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<Character>> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                requireContext(),
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}