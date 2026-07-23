package com.example.noteapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.noteapp.fragments.CalendarFragment;
import com.example.noteapp.fragments.HomeFragment;
import com.example.noteapp.fragments.NewTaskDialogFragment;
import com.example.noteapp.fragments.ReminderFragment;
import com.example.noteapp.fragments.SettingsFragment;
import com.example.noteapp.fragments.TasksFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab_add);
        bottomNav = findViewById(R.id.bottom_navigation);

        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00D68F")));
        fab.setOnClickListener(v -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof HomeFragment || current instanceof TasksFragment) {
                NewTaskDialogFragment dialog = new NewTaskDialogFragment();
                dialog.show(getSupportFragmentManager(), "NewTaskDialog");
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                fab.setVisibility(View.VISIBLE);
            } else if (itemId == R.id.nav_calendar) {
                selectedFragment = new CalendarFragment();
                fab.setVisibility(View.GONE);
            } else if (itemId == R.id.nav_tasks) {
                selectedFragment = new TasksFragment();
                fab.setVisibility(View.VISIBLE);
            } else if (itemId == R.id.nav_reminder) {
                selectedFragment = new ReminderFragment();
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
            fab.setVisibility(View.VISIBLE);
        }
    }
}
