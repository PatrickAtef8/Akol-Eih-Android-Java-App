

/*
 * SearchActivity.java
 */
package com.example.akoleih.search.view;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.home.network.RetrofitClient;
import com.example.akoleih.home.view.HomeMealDetailsThirdFragment;
import com.example.akoleih.search.adapter.SearchMealsAdapter;
import com.example.akoleih.search.model.SearchMeal;
import com.example.akoleih.search.model.repository.SearchRepository;
import com.example.akoleih.search.model.repository.SearchRepositoryImpl;
import com.example.akoleih.search.model.repository.SearchType;
import com.example.akoleih.search.network.api.SearchApiService;
import com.example.akoleih.search.network.api.SearchRemoteDataSource;
import com.example.akoleih.search.network.api.SearchRemoteDataSourceImpl;
import com.example.akoleih.search.presenter.SearchPresenterImpl;
import com.example.akoleih.search.view.SearchView;
import com.example.akoleih.utils.SearchValidator;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements SearchView, SearchMealsAdapter.OnMealClickListener {

    private SearchPresenterImpl presenter;
    private SearchMealsAdapter adapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 300;

    private TextInputEditText searchEditText;
    private ChipGroup searchChipGroup;
    private AutoCompleteTextView areaAutoComplete;
    private AutoCompleteTextView categoryAutoComplete;
    private View emptyStateView;
    private ImageView emptyStateIcon;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchEditText = findViewById(R.id.search_edit_text);
        searchChipGroup = findViewById(R.id.search_chip_group);
        areaAutoComplete = findViewById(R.id.area_autocomplete);
        categoryAutoComplete = findViewById(R.id.category_autocomplete);
        emptyStateView = findViewById(R.id.empty_state);
        emptyStateIcon = findViewById(R.id.empty_state_icon);
        emptyStateText = findViewById(R.id.empty_state_text);

        RecyclerView rv = findViewById(R.id.results_recycler_view);
        adapter = new SearchMealsAdapter(new ArrayList<>(), this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Presenter setup
        SearchApiService apiService = RetrofitClient.getInstance().create(SearchApiService.class);
        SearchRemoteDataSource remote = new SearchRemoteDataSourceImpl(apiService);
        SearchRepository repo = new SearchRepositoryImpl(remote);
        presenter = new SearchPresenterImpl(this, repo);

        setupFilterUI();

        // Default filter to "All"
        findViewById(R.id.chip_all).performClick();
        performSearch("");

        setupSearchListeners();
    }

    @Override
    public void onMealClick(SearchMeal meal) {
        FrameLayout container = findViewById(R.id.search_fragment_container);
        if (container.getVisibility() == View.GONE) {
            container.setVisibility(View.VISIBLE);
        }

        HomeMealDetailsThirdFragment detailFrag =
                HomeMealDetailsThirdFragment.newInstance(meal.getIdMeal());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.search_fragment_container, detailFrag)
                .addToBackStack(null)
                .commit();
    }

    private void setupFilterUI() {
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                SearchValidator.getValidAreas()
        );
        areaAutoComplete.setAdapter(areaAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                SearchValidator.getValidCategories()
        );
        categoryAutoComplete.setAdapter(catAdapter);

        searchChipGroup.setOnCheckedChangeListener((group, id) -> {
            adapter.updateMeals(new ArrayList<>());
            searchEditText.setText("");
            areaAutoComplete.setText("");
            categoryAutoComplete.setText("");
            showEmptyState(true, R.drawable.ic_search, "Start typing to search");

            areaAutoComplete.setVisibility(id == R.id.chip_area ? View.VISIBLE : View.GONE);
            categoryAutoComplete.setVisibility(id == R.id.chip_category ? View.VISIBLE : View.GONE);
        });

        areaAutoComplete.setOnItemClickListener((p, v, pos, id) ->
                performSearch((String) p.getItemAtPosition(pos))
        );
        categoryAutoComplete.setOnItemClickListener((p, v, pos, id) ->
                performSearch((String) p.getItemAtPosition(pos))
        );
    }

    private void setupSearchListeners() {
        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                // If details fragment is visible, hide it and go back to list
                FrameLayout fragmentContainer = findViewById(R.id.search_fragment_container);
                if (fragmentContainer.getVisibility() == View.VISIBLE) {
                    getSupportFragmentManager().popBackStack();
                    fragmentContainer.setVisibility(View.GONE);
                }

                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(s.toString());
                handler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        };
        searchEditText.addTextChangedListener(tw);
        areaAutoComplete.addTextChangedListener(tw);
        categoryAutoComplete.addTextChangedListener(tw);
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            adapter.updateMeals(new ArrayList<>());
            showEmptyState(true, R.drawable.ic_search, "Start typing to search");
            return;
        }

        int checked = searchChipGroup.getCheckedChipId();
        SearchType type = SearchType.ALL;
        if (checked == R.id.chip_area)       type = SearchType.AREA;
        else if (checked == R.id.chip_category) type = SearchType.CATEGORY;
        else if (checked == R.id.chip_ingredient) type = SearchType.INGREDIENT;

        presenter.search(query, type);
    }

    @Override
    public void showResults(List<SearchMeal> meals) {
        runOnUiThread(() -> {
            adapter.updateMeals(meals);
            showEmptyState(meals.isEmpty(), R.drawable.ic_no_results, "No results found");
        });
    }

    private void showEmptyState(boolean show, int iconRes, String msg) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            emptyStateIcon.setImageResource(iconRes);
            emptyStateText.setText(msg);
        }
    }

    @Override
    public void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            showEmptyState(true, R.drawable.ic_error, "Error loading results");
        });
    }

    @Override public void showLoading() {}
    @Override public void hideLoading() {}

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        presenter.onDestroy();
        super.onDestroy();
    }
}
