package com.salle.grup17.controllers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.salle.grup17.R;
import com.salle.grup17.controllers.fragments.DiscoverFragment;
import com.salle.grup17.controllers.fragments.FavoritesFragment;
import com.salle.grup17.controllers.fragments.HomeFragment;
import com.salle.grup17.controllers.fragments.QuizFragment;
import com.salle.grup17.views.fragments.ProfileFragment;

public class MainAppActivity extends AppCompatActivity {

    private static final String TAG = "MainAppActivity";

    private BottomNavigationView bottomNavigationView;
    private TextView tvTabError;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tvTabError = findViewById(R.id.tvTabError);

        if (bottomNavigationView == null) {
            showTabError(getString(R.string.tab_error_navigation));
            Log.e(TAG, "BottomNavigationView not found in activity_main_app.xml");
            return;
        }

        bottomNavigationView.setLabelVisibilityMode(
                NavigationBarView.LABEL_VISIBILITY_LABELED
        );

        bottomNavigationView.setOnItemSelectedListener(item -> {
            clearTabError();

            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());

            if (selectedFragment == null) {
                showTabError(getString(R.string.tab_error_section_unavailable));
                Log.e(TAG, "No fragment found for menu item id: " + item.getItemId());
                return false;
            }

            return openFragment(selectedFragment);
        });

        if (savedInstanceState == null) {
            boolean homeLoaded = openFragment(new HomeFragment());

            if (homeLoaded) {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    private Fragment getFragmentForMenuItem(int itemId) {
        if (itemId == R.id.nav_home) {
            return new HomeFragment();
        } else if (itemId == R.id.nav_favorites) {
            return new FavoritesFragment();
        } else if (itemId == R.id.nav_quiz) {
            return new QuizFragment();
        } else if (itemId == R.id.nav_discover) {
            return new DiscoverFragment();
        } else if (itemId == R.id.nav_profile) {
            return new ProfileFragment();
        } else {
            return null;
        }
    }

    private boolean openFragment(Fragment fragment) {
        if (fragment == null) {
            showTabError(getString(R.string.tab_error_section_unavailable));
            Log.e(TAG, "Tried to open a null fragment");
            return false;
        }

        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error opening fragment: " + fragment.getClass().getSimpleName(), e);
            showTabError(getString(R.string.tab_error_opening_section));
            return false;
        }
    }

    private void showTabError(String message) {
        if (tvTabError == null) {
            Log.e(TAG, "Tab error: " + message);
            return;
        }

        tvTabError.setText(message);
        tvTabError.setVisibility(View.VISIBLE);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            if (tvTabError != null) {
                tvTabError.setText(getString(R.string.empty_text));
                tvTabError.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void clearTabError() {
        if (tvTabError != null) {
            tvTabError.setText(getString(R.string.empty_text));
            tvTabError.setVisibility(View.GONE);
        }
    }
}