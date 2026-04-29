package com.salle.grup17.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.salle.grup17.R;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.salle.grup17.fragments.HomeFragment;
import com.salle.grup17.fragments.FavoritesFragment;
import com.salle.grup17.fragments.QuizFragment;
import com.salle.grup17.fragments.ProfileFragment;
public class MainAppActivity extends AppCompatActivity {

    Button homeBtn, favBtn, quizBtn, profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        homeBtn = findViewById(R.id.homeBtn);
        favBtn = findViewById(R.id.favBtn);
        quizBtn = findViewById(R.id.quizBtn);
        profileBtn = findViewById(R.id.profileBtn);


        loadFragment(new HomeFragment());

        homeBtn.setOnClickListener(v -> loadFragment(new HomeFragment()));
        favBtn.setOnClickListener(v -> loadFragment(new FavoritesFragment()));
        quizBtn.setOnClickListener(v -> loadFragment(new QuizFragment()));
        profileBtn.setOnClickListener(v -> loadFragment(new ProfileFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}