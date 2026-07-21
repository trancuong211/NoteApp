package com.example.noteapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.noteapp.fragments.HomeFragment;
import com.example.noteapp.fragments.NotesFragment;
import com.example.noteapp.fragments.TasksFragment;
import com.example.noteapp.fragments.StatsFragment;
import com.example.noteapp.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                fab.setVisibility(View.GONE);
            } else if (itemId == R.id.nav_notes) {
                selectedFragment = new NotesFragment();
                fab.setVisibility(View.VISIBLE);
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7C4DFF")));
            } else if (itemId == R.id.nav_tasks) {
                selectedFragment = new TasksFragment();
                fab.setVisibility(View.VISIBLE);
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00E676")));
            } else if (itemId == R.id.nav_stats) {
                selectedFragment = new StatsFragment();
                fab.setVisibility(View.GONE);
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
                fab.setVisibility(View.GONE);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            fab.setVisibility(View.GONE);
        }
    }
}
