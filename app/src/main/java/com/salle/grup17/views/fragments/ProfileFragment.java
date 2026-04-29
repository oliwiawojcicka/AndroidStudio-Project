package com.salle.grup17.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundColor(android.graphics.Color.rgb(21, 23, 40));

        TextView title = new TextView(requireContext());
        title.setText("Profile screen");
        title.setTextSize(24);
        title.setTextColor(android.graphics.Color.WHITE);
        title.setGravity(android.view.Gravity.CENTER);

        TextView subtitle = new TextView(requireContext());
        subtitle.setText("To be implemented");
        subtitle.setTextSize(16);
        subtitle.setTextColor(android.graphics.Color.LTGRAY);
        subtitle.setGravity(android.view.Gravity.CENTER);

        layout.addView(title);
        layout.addView(subtitle);

        return layout;
    }
}