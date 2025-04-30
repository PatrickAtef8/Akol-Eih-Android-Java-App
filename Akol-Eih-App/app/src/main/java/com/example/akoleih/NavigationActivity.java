package com.example.akoleih;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.akoleih.home.view.HomeFirstFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFirstFragment())
                    .commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFirstFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_search) {
                   // startActivity(new Intent(NavigationActivity.this, SearchActivity.class));
                    return true;
                } else if (itemId == R.id.nav_favorites) {
                    // getSupportFragmentManager().beginTransaction()
                    //         .replace(R.id.fragment_container, new FavoritesFragment())
                    //         .commit();
                    return true;
                }
                return false;
            };
}