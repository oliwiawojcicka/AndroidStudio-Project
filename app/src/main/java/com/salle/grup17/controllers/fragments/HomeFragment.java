package com.salle.grup17.controllers.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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

    private EditText searchEditText;
    private RecyclerView charactersRecyclerView;
    private CharacterAdapter characterAdapter;
    private LinearLayoutManager layoutManager;

    private TextView tvNoResults;

    private final ArrayList<Character> characterList = new ArrayList<>();

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private String currentSearch = "";

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

        searchEditText = view.findViewById(R.id.searchEditText);
        charactersRecyclerView = view.findViewById(R.id.charactersRecyclerView);

        // Podpinamy nasz napis z XML
        tvNoResults = view.findViewById(R.id.tvNoResults);

        layoutManager = new LinearLayoutManager(requireContext());
        charactersRecyclerView.setLayoutManager(layoutManager);

        characterAdapter = new CharacterAdapter(characterList);
        charactersRecyclerView.setAdapter(characterAdapter);

        setupSearch();
        setupPagination();

        resetAndLoadCharacters();

        return view;
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().trim();
                resetAndLoadCharacters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupPagination() {
        charactersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(
                    @NonNull RecyclerView recyclerView,
                    int dx,
                    int dy
            ) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy <= 0) {
                    return;
                }

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                boolean isAtEnd =
                        visibleItemCount + firstVisibleItemPosition >= totalItemCount - 3;

                if (!isLoading && hasMorePages && isAtEnd) {
                    currentPage++;
                    loadCharacters(currentPage);
                }
            }
        });
    }

    private void resetAndLoadCharacters() {
        currentPage = 1;
        hasMorePages = true;
        characterList.clear();
        characterAdapter.notifyDataSetChanged();

        showResults();

        loadCharacters(currentPage);
    }

    private void loadCharacters(int page) {
        isLoading = true;

        Call<ApiResponse<Character>> call;

        if (currentSearch.isEmpty()) {
            call = RetrofitClient.getApi().getCharacters(page);
        } else {
            call = RetrofitClient.getApi().searchCharacters(currentSearch, page);
        }

        call.enqueue(new Callback<ApiResponse<Character>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<Character>> call,
                    @NonNull Response<ApiResponse<Character>> response
            ) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Character> newCharacters =
                            new ArrayList<>(response.body().getResults());

                    int oldSize = characterList.size();
                    characterList.addAll(newCharacters);

                    characterAdapter.notifyItemRangeInserted(
                            oldSize,
                            newCharacters.size()
                    );

                    if (newCharacters.isEmpty()) {
                        hasMorePages = false;
                    }

                    if (characterList.isEmpty()) {
                        showEmptyState("No characters found");
                    } else {
                        showResults();
                    }

                } else {
                    hasMorePages = false;
                    if (characterList.isEmpty()) {
                        showEmptyState("No characters found");
                    }
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<Character>> call,
                    @NonNull Throwable t
            ) {
                isLoading = false;

                // Brak internetu - też zamieniamy dawny Toast na nasz napis!
                if (characterList.isEmpty()) {
                    showEmptyState("Connection error");
                }
            }
        });
    }



    private void showEmptyState(String message) {
        charactersRecyclerView.setVisibility(View.GONE);
        tvNoResults.setText(message);
        tvNoResults.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        tvNoResults.setVisibility(View.GONE);
        charactersRecyclerView.setVisibility(View.VISIBLE);
    }
}