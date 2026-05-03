package com.salle.grup17.controllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salle.grup17.R;
import com.salle.grup17.views.fragments.DiscoverFragment;
import com.salle.grup17.views.fragments.FavoritesFragment;
import com.salle.grup17.views.fragments.HomeFragment;
import com.salle.grup17.views.fragments.ProfileFragment;
import com.salle.grup17.views.fragments.QuizFragment;


public class MainAppActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        loadFragment(new HomeFragment());
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_favorites) {
                loadFragment(new FavoritesFragment());
                return true;
            } else if (id == R.id.nav_quiz) {
                loadFragment(new QuizFragment());
                return true;
            } else if (id == R.id.nav_discover) {
                loadFragment(new DiscoverFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}