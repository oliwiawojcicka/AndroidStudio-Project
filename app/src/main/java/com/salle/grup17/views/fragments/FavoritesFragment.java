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
import com.salle.grup17.models.Character;
import com.salle.grup17.views.adapters.CharacterAdapter;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private CharacterAdapter characterAdapter;
    private final ArrayList<Character> favoriteList = new ArrayList<>();

    public FavoritesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        characterAdapter = new CharacterAdapter(favoriteList);
        favoritesRecyclerView.setAdapter(characterAdapter);

        Toast.makeText(requireContext(), "Favorites layout loaded", Toast.LENGTH_SHORT).show();

        return view;
    }
}