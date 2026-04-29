package com.salle.grup17.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        TextView textView = new TextView(requireContext());
        textView.setText("Favorites screen\nTo be implemented");
        textView.setTextSize(24);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextColor(android.graphics.Color.WHITE);
        textView.setBackgroundColor(android.graphics.Color.rgb(21, 23, 40));

        return textView;
    }
}