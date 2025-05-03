package com.example.akoleih;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.akoleih.favorite.view.FavoritesFragment;
import com.example.akoleih.home.view.HomeFirstFragment;
import com.example.akoleih.search.view.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int currentSelectedItem = R.id.nav_home;
    private Fragment currentHomeFragment; // Track current home fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            currentHomeFragment = new HomeFirstFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentHomeFragment)
                    .commit();
        } else {
            currentSelectedItem = savedInstanceState.getInt("SELECTED_ITEM", R.id.nav_home);
            currentHomeFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, "CURRENT_FRAGMENT");
        }

        bottomNavigationView.setSelectedItemId(currentSelectedItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SELECTED_ITEM", currentSelectedItem);
        if (currentHomeFragment != null && currentHomeFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "CURRENT_FRAGMENT", currentHomeFragment);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_search) {
                    // Save current fragment before going to search
                    Fragment current = getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
                    if (current != null && currentSelectedItem == R.id.nav_home) {
                        currentHomeFragment = current;
                    }
                    startActivity(new Intent(NavigationActivity.this, SearchActivity.class));
                    return true;
                }

                currentSelectedItem = itemId;
                if (itemId == R.id.nav_home) {
                    if (currentHomeFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, currentHomeFragment)
                                .commit();
                    }
                } else if (itemId == R.id.nav_favorites) {
                     getSupportFragmentManager().beginTransaction()
                             .replace(R.id.fragment_container, new FavoritesFragment())
                             .commit();
                }
                return true;
            };

    @Override
    protected void onResume() {
        super.onResume();
        // Restore selection when returning from SearchActivity
        bottomNavigationView.setSelectedItemId(currentSelectedItem);
    }
}