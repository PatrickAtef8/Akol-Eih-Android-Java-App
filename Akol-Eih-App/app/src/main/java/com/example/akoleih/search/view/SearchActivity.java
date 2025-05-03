package com.example.akoleih.search.view;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.home.network.RetrofitClient;
import com.example.akoleih.search.adapter.SearchMealsAdapter;
import com.example.akoleih.search.model.SearchMeal;
import com.example.akoleih.search.model.repository.SearchRepository;
import com.example.akoleih.search.model.repository.SearchRepositoryImpl;
import com.example.akoleih.search.model.repository.SearchType;
import com.example.akoleih.search.network.api.SearchApiService;
import com.example.akoleih.search.network.api.SearchRemoteDataSource;
import com.example.akoleih.search.network.api.SearchRemoteDataSourceImpl;
import com.example.akoleih.search.presenter.SearchPresenterImpl;
import com.example.akoleih.utils.SearchValidator;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView {
    private SearchPresenterImpl presenter;
    private SearchMealsAdapter adapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 300;
    private AutoCompleteTextView areaAutoComplete;
    private AutoCompleteTextView categoryAutoComplete;
    private TextInputEditText searchEditText;
    private ChipGroup searchChipGroup;
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
        RecyclerView resultsRecyclerView = findViewById(R.id.results_recycler_view);
        areaAutoComplete = findViewById(R.id.area_autocomplete);
        categoryAutoComplete = findViewById(R.id.category_autocomplete);
        emptyStateView = findViewById(R.id.empty_state);
        emptyStateIcon = findViewById(R.id.empty_state_icon);
        emptyStateText = findViewById(R.id.empty_state_text);

        // Setup RecyclerView with empty state
        adapter = new SearchMealsAdapter(new ArrayList<>());
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(adapter);

        // Initialize presenter
        SearchApiService apiService = RetrofitClient.getInstance().create(SearchApiService.class);
        SearchRemoteDataSource remoteDataSource = new SearchRemoteDataSourceImpl(apiService);
        SearchRepository repository = new SearchRepositoryImpl(remoteDataSource);
        presenter = new SearchPresenterImpl(this, repository);

        setupFilterUI();

        // Set default chip selection to ALL
        Chip defaultChip = findViewById(R.id.chip_all);
        defaultChip.setChecked(true);
        performSearch(""); // Initialize with empty results

        setupSearchListeners();
    }

    private void setupFilterUI() {
        // Setup area autocomplete
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                SearchValidator.getValidAreas()
        );
        areaAutoComplete.setAdapter(areaAdapter);

        // Setup category autocomplete
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                SearchValidator.getValidCategories()
        );
        categoryAutoComplete.setAdapter(categoryAdapter);

        searchChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.updateMeals(new ArrayList<>());
            searchEditText.setText("");
            areaAutoComplete.setText("");
            categoryAutoComplete.setText("");
            showEmptyState(true, R.drawable.ic_search, "Start typing to search");

            areaAutoComplete.setVisibility(View.GONE);
            categoryAutoComplete.setVisibility(View.GONE);

            if (checkedId == R.id.chip_area) {
                areaAutoComplete.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.chip_category) {
                categoryAutoComplete.setVisibility(View.VISIBLE);
            }
        });

        areaAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedArea = (String) parent.getItemAtPosition(position);
            performSearch(selectedArea);
        });

        categoryAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            performSearch(selectedCategory);
        });
    }

    private void setupSearchListeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(s.toString());
                handler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        areaAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchChipGroup.getCheckedChipId() == R.id.chip_area) {
                    handler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> performSearch(s.toString());
                    handler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
            }
        });

        categoryAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchChipGroup.getCheckedChipId() == R.id.chip_category) {
                    handler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> performSearch(s.toString());
                    handler.postDelayed(searchRunnable, SEARCH_DELAY);
                }
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            adapter.updateMeals(new ArrayList<>());
            showEmptyState(true, R.drawable.ic_search, "Start typing to search");
            return;
        }

        int selectedId = searchChipGroup.getCheckedChipId();
        SearchType type = SearchType.ALL;

        if (selectedId == R.id.chip_area) {
            type = SearchType.AREA;
        } else if (selectedId == R.id.chip_category) {
            type = SearchType.CATEGORY;
        } else if (selectedId == R.id.chip_ingredient) {
            type = SearchType.INGREDIENT;
        }

        presenter.search(query, type);
    }

    @Override
    public void showResults(List<SearchMeal> meals) {
        runOnUiThread(() -> {
            adapter.updateMeals(meals);
            showEmptyState(meals.isEmpty(), R.drawable.ic_no_results, "No results found");
        });
    }

    private void showEmptyState(boolean show, int iconRes, String message) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            emptyStateIcon.setImageResource(iconRes);
            emptyStateText.setText(message);
        }
    }

    @Override
    public void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            showEmptyState(true, R.drawable.ic_error, "Error loading results");
        });
    }

    @Override
    public void showLoading() {
        runOnUiThread(() -> {
            emptyStateView.setVisibility(View.GONE);
            // Show loading indicator if needed
        });
    }

    @Override
    public void hideLoading() {
        runOnUiThread(() -> {
            // Hide loading indicator if needed
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        presenter.onDestroy();
        super.onDestroy();
    }
}