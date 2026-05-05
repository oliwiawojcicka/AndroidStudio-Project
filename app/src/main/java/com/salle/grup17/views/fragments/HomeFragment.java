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
    private LinearLayoutManager layoutManager;

    private final ArrayList<Character> characterList = new ArrayList<>();

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

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

        layoutManager = new LinearLayoutManager(requireContext());
        charactersRecyclerView.setLayoutManager(layoutManager);

        characterAdapter = new CharacterAdapter(characterList);
        charactersRecyclerView.setAdapter(characterAdapter);

        setupPagination();

        loadCharacters(currentPage);

        return view;
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

    private void loadCharacters(int page) {
        isLoading = true;

        RetrofitClient.getApi().getCharacters(page)
                .enqueue(new Callback<ApiResponse<Character>>() {
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

                        } else {
                            hasMorePages = false;
                            Toast.makeText(
                                    requireContext(),
                                    "No more characters or API error",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<Character>> call,
                            @NonNull Throwable t
                    ) {
                        isLoading = false;

                        Toast.makeText(
                                requireContext(),
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}