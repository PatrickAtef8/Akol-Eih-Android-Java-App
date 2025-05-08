package com.example.akoleih.search.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.example.akoleih.R;
import com.example.akoleih.home.view.fragments.thirdfragment.HomeMealDetailsThirdFragment;
import com.example.akoleih.search.view.adapter.SearchMealsAdapter;
import com.example.akoleih.search.model.SearchMeal;
import com.example.akoleih.search.model.repository.SearchType;
import com.example.akoleih.search.model.repository.SearchViewModel;
import com.example.akoleih.utils.NetworkUtil;
import com.example.akoleih.utils.NoInternetDialog;
import com.example.akoleih.utils.SearchValidator;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchMealsAdapter.OnMealClickListener {
    private static final String TAG = "SearchActivity";
    private SearchViewModel viewModel;
    private SearchMealsAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 300;
    private String lastQuery = "";
    private SearchType lastSearchType = SearchType.ALL;

    private TextInputEditText searchEditText;
    private ChipGroup searchChipGroup;
    private AutoCompleteTextView areaAutoComplete;
    private AutoCompleteTextView categoryAutoComplete;
    private View emptyStateView;
    private LottieAnimationView emptyStateAnimation;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(SearchViewModel.class);

        searchEditText = findViewById(R.id.search_edit_text);
        searchChipGroup = findViewById(R.id.search_chip_group);
        areaAutoComplete = findViewById(R.id.area_autocomplete);
        categoryAutoComplete = findViewById(R.id.category_autocomplete);
        emptyStateView = findViewById(R.id.empty_state);
        emptyStateAnimation = findViewById(R.id.empty_state_animation);
        emptyStateText = findViewById(R.id.empty_state_text);

        RecyclerView rv = findViewById(R.id.results_recycler_view);
        adapter = new SearchMealsAdapter(new ArrayList<>(), this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Check initial network state
        if (!NetworkUtil.isConnected(getApplication())) {
            Log.e(TAG, "Initial network check: No internet, showing dialog");
            NoInternetDialog.show(this, () -> viewModel.search(lastQuery, lastSearchType));
        } else {
            Log.d(TAG, "Initial network check: Connected");
        }

        setupFilterUI();
        setupSearchListeners();
        setupObservers();

        findViewById(R.id.chip_all).performClick();
        performSearch("");
    }

    private void setupObservers() {
        viewModel.getSearchResultsLive().observe(this, meals -> {
            adapter.updateMeals(meals);
            showEmptyState(meals.isEmpty(), R.drawable.ic_no_results, "No results found");
            Log.d(TAG, "Search results updated, count: " + (meals != null ? meals.size() : 0));
        });

        viewModel.getErrorLive().observe(this, message -> {
            Log.d(TAG, "Error received: " + message);
            if ("NO_INTERNET".equals(message)) {
                NoInternetDialog.show(this, () -> viewModel.search(lastQuery, lastSearchType));
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                showEmptyState(true, R.drawable.ic_error, "Error loading results");
            }
        });
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
            lastQuery = "";
            lastSearchType = SearchType.ALL;
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
            lastQuery = "";
            lastSearchType = SearchType.ALL;
            return;
        }

        int checked = searchChipGroup.getCheckedChipId();
        SearchType type = SearchType.ALL;
        if (checked == R.id.chip_area) type = SearchType.AREA;
        else if (checked == R.id.chip_category) type = SearchType.CATEGORY;
        else if (checked == R.id.chip_ingredient) type = SearchType.INGREDIENT;

        lastQuery = query;
        lastSearchType = type;
        viewModel.search(query, type);
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

    private void showEmptyState(boolean show, int iconRes, String msg) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            int animationRes;
            if (iconRes == R.drawable.ic_search) {
                animationRes = R.raw.searching;
            } else if (iconRes == R.drawable.ic_no_results) {
                animationRes = R.raw.noresult;
            } else if (iconRes == R.drawable.ic_error) {
                animationRes = R.raw.noresult;
            } else {
                animationRes = R.raw.typetosearch;
            }

            emptyStateAnimation.setAnimation(animationRes);
            emptyStateAnimation.playAnimation();

            emptyStateText.setText(msg);
        } else {
            emptyStateAnimation.cancelAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}