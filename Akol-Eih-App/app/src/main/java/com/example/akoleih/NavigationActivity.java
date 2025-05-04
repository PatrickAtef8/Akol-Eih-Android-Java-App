package com.example.akoleih;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.akoleih.favorite.view.FavoritesFragment;
import com.example.akoleih.home.view.HomeFirstFragment;
import com.example.akoleih.home.view.HomeMealsSecondFragment;
import com.example.akoleih.home.view.HomeMealDetailsThirdFragment;
import com.example.akoleih.profile.view.ProfileActivity;
import com.example.akoleih.search.view.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int currentSelectedItem = R.id.nav_home;
    private Fragment currentHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (isHomeFragment(currentFragment)) {
                updateSelectedItem(R.id.nav_home);
            } else if (currentFragment instanceof FavoritesFragment) {
                updateSelectedItem(R.id.nav_favorites);
            }
        });

        if (savedInstanceState == null) {
            currentHomeFragment = new HomeFirstFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentHomeFragment)
                    .commit();
        } else {
            currentSelectedItem = savedInstanceState.getInt("SELECTED_ITEM", R.id.nav_home);
            currentHomeFragment = getSupportFragmentManager().getFragment(savedInstanceState, "CURRENT_HOME_FRAGMENT");
            if (currentSelectedItem == R.id.nav_favorites) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FavoritesFragment())
                        .commit();
            } else {
                if (currentHomeFragment == null) {
                    currentHomeFragment = new HomeFirstFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, currentHomeFragment)
                        .commit();
            }
        }
        bottomNavigationView.setSelectedItemId(currentSelectedItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(currentSelectedItem);
    }

    private void updateSelectedItem(int itemId) {
        if (currentSelectedItem != itemId) {
            currentSelectedItem = itemId;
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SELECTED_ITEM", currentSelectedItem);
        if (currentHomeFragment != null && currentHomeFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "CURRENT_HOME_FRAGMENT", currentHomeFragment);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();

                // Handle Search separately
                if (itemId == R.id.nav_search) {
                    Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (current != null && isHomeFragment(current)) {
                        currentHomeFragment = current;
                    }
                    startActivity(new Intent(NavigationActivity.this, SearchActivity.class));
                    return true;
                }

                // Prevent redundant selection
                if (itemId == currentSelectedItem) {
                    return true;
                }

                // Home button
                if (itemId == R.id.nav_home) {
                    if (currentHomeFragment == null) {
                        currentHomeFragment = new HomeFirstFragment();
                    }
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, currentHomeFragment)
                            .commit();
                    currentSelectedItem = R.id.nav_home;

                    // Favorites button
                } else if (itemId == R.id.nav_favorites) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new FavoritesFragment())
                            .addToBackStack("favorites")
                            .commit();
                    currentSelectedItem = R.id.nav_favorites;

                }
                    else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(NavigationActivity.this, ProfileActivity.class));
                    return true;
                    } else {
                    return false;
                }

                bottomNavigationView.setSelectedItemId(currentSelectedItem);
                return true;
            };

    private boolean isHomeFragment(Fragment fragment) {
        return fragment instanceof HomeFirstFragment
                || fragment instanceof HomeMealsSecondFragment
                || fragment instanceof HomeMealDetailsThirdFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
