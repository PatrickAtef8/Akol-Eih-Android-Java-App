package com.example.akoleih;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.akoleih.auth.model.callbacks.DataCallback;
import com.example.akoleih.auth.model.repository.DataRepository;
import com.example.akoleih.auth.model.repository.DataRepositoryImpl;
import com.example.akoleih.calendar.model.repository.CalendarRepository;
import com.example.akoleih.calendar.model.repository.CalendarRepositoryImpl;
import com.example.akoleih.calendar.model.repository.FirebaseService;
import com.example.akoleih.calendar.model.repository.FirebaseServiceImpl;
import com.example.akoleih.calendar.presenter.CalendarPresenterImpl;
import com.example.akoleih.calendar.presenter.CalendarPresenter;
import com.example.akoleih.calendar.view.fragment.CalendarFragment;
import com.example.akoleih.favorite.view.fragment.FavoritesFragment;
import com.example.akoleih.home.view.fragments.firstfragment.HomeFirstFragment;
import com.example.akoleih.home.view.fragments.secondfragment.HomeMealsSecondFragment;
import com.example.akoleih.home.view.fragments.thirdfragment.HomeMealDetailsThirdFragment;
import com.example.akoleih.profile.view.ProfileActivity;
import com.example.akoleih.search.view.activity.SearchActivity;
import com.example.akoleih.utils.CustomLoginDialog;
import com.example.akoleih.utils.SharedPrefUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Collections;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int currentSelectedItem = R.id.nav_home;
    private Fragment currentHomeFragment;
    private DataRepository dataRepo;
    private Map<String, Object> userData = Collections.emptyMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        dataRepo = new DataRepositoryImpl();
        loadUserData();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);

            if (currentFragment instanceof CalendarFragment) {
                currentSelectedItem = R.id.nav_calendar;
            } else if (isHomeFragment(currentFragment)) {
                currentSelectedItem = R.id.nav_home;
            } else if (currentFragment instanceof FavoritesFragment) {
                currentSelectedItem = R.id.nav_favorites;
            }
            bottomNavigationView.setSelectedItemId(currentSelectedItem);
        });

        if (savedInstanceState == null) {
            currentHomeFragment = new HomeFirstFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentHomeFragment)
                    .commit();
        } else {
            currentSelectedItem = savedInstanceState.getInt("SELECTED_ITEM", R.id.nav_home);
            currentHomeFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, "CURRENT_HOME_FRAGMENT");
            restoreFragmentState();
        }
        bottomNavigationView.setSelectedItemId(currentSelectedItem);
    }

    private void restoreFragmentState() {
        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (current == null) {
            if (currentSelectedItem == R.id.nav_favorites) {
                replaceFragment(new FavoritesFragment(), false);
            } else if (currentSelectedItem == R.id.nav_calendar) {
                replaceFragment(new CalendarFragment(createCalendarPresenter()), false);
            } else {
                replaceFragment(currentHomeFragment != null ? currentHomeFragment : new HomeFirstFragment(), false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(currentSelectedItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SELECTED_ITEM", currentSelectedItem);
        if (currentHomeFragment != null && currentHomeFragment.isAdded()) {
            getSupportFragmentManager()
                    .putFragment(outState, "CURRENT_HOME_FRAGMENT", currentHomeFragment);
        }
    }

    private void loadUserData() {
        dataRepo.getUserData(new DataCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                userData = data;
                updateFragmentsWithData();
            }

            @Override
            public void onFailure(String message) {
                // Handle error
            }
        });
    }

    private void updateFragmentsWithData() {
        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (current instanceof DataListener) {
            ((DataListener) current).onDataLoaded(userData);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();

                if (itemId == currentSelectedItem) return true;

                if (itemId == R.id.nav_search) {
                    Fragment current = getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
                    if (isHomeFragment(current)) {
                        currentHomeFragment = current;
                    }
                    startActivity(new Intent(NavigationActivity.this, SearchActivity.class));
                    return true;
                }

                if (itemId == R.id.nav_favorites || itemId == R.id.nav_calendar || itemId == R.id.nav_profile) {
                    if (SharedPrefUtil.isGuestMode(this)) {
                        CustomLoginDialog.show(this);
                        return false;
                    }
                }

                if (itemId == R.id.nav_home) {
                    handleHomeNavigation();
                } else if (itemId == R.id.nav_favorites) {
                    handleFavoritesNavigation();
                } else if (itemId == R.id.nav_calendar) {
                    handleCalendarNavigation();
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(NavigationActivity.this, ProfileActivity.class));
                    return true;
                } else {
                    return false;
                }

                updateFragmentsWithData();
                bottomNavigationView.setSelectedItemId(currentSelectedItem);
                return true;
            };

    private void handleHomeNavigation() {
        if (currentHomeFragment == null) {
            currentHomeFragment = new HomeFirstFragment();
        }
        clearBackStack();
        replaceFragment(currentHomeFragment, false);
        currentSelectedItem = R.id.nav_home;
    }

    private void handleFavoritesNavigation() {
        replaceFragment(new FavoritesFragment(), true);
        currentSelectedItem = R.id.nav_favorites;
    }

    private void handleCalendarNavigation() {
        replaceFragment(new CalendarFragment(createCalendarPresenter()), true);
        currentSelectedItem = R.id.nav_calendar;
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(addToBackStack ? fragment.getClass().getSimpleName() : null)
                .commit();
    }

    private void clearBackStack() {
        getSupportFragmentManager().popBackStackImmediate(
                null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private boolean isHomeFragment(Fragment fragment) {
        return fragment instanceof HomeFirstFragment
                || fragment instanceof HomeMealsSecondFragment
                || fragment instanceof HomeMealDetailsThirdFragment;
    }

    private CalendarPresenter createCalendarPresenter() {
        FirebaseService firebaseService = new FirebaseServiceImpl();
        CalendarRepository repository = new CalendarRepositoryImpl(this, firebaseService);
        return new CalendarPresenterImpl(repository);
    }

    public interface DataListener {
        void onDataLoaded(Map<String, Object> data);
    }
}