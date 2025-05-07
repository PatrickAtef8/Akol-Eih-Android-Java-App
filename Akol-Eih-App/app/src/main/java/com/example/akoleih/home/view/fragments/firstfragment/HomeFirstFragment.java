package com.example.akoleih.home.view.fragments.firstfragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.home.view.adapters.categories.CategoriesAdapter;
import com.example.akoleih.home.view.adapters.countries.CountryFlagsAdapter;
import com.example.akoleih.home.view.adapters.categories.OnCategoryClickListener;
import com.example.akoleih.home.view.adapters.countries.OnCountryClickListener;
import com.example.akoleih.home.view.adapters.randommeal.RandomMealAdapter;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.repository.HomeViewModel;
import com.example.akoleih.home.view.fragments.thirdfragment.HomeMealDetailsThirdFragment;
import com.example.akoleih.home.view.fragments.secondfragment.HomeMealsSecondFragment;
import com.example.akoleih.utils.NetworkUtil;
import com.example.akoleih.utils.NoInternetDialog;
import java.util.ArrayList;

public class HomeFirstFragment extends Fragment implements OnCategoryClickListener, OnCountryClickListener {

    private HomeViewModel viewModel;
    private CategoriesAdapter categoriesAdapter;
    private RandomMealAdapter randomMealAdapter;
    private CountryFlagsAdapter flagsAdapter;
    private TextView noDataText;
    private RecyclerView categoriesRecyclerView, randomMealRecyclerView, flagsRecyclerView;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long lastErrorTime = 0;
    private static final long ERROR_DEBOUNCE_MS = 1000; // 1 second debounce

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(HomeViewModel.class);
        Log.d("HomeFirstFragment", "onCreate: ViewModel initialized");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noDataText = view.findViewById(R.id.no_data_text);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        randomMealRecyclerView = view.findViewById(R.id.random_meal_recycler_view);
        flagsRecyclerView = view.findViewById(R.id.flags_recycler_view);

        setupCategoriesRecycler();
        setupRandomMealRecycler();
        setupFlagsRecycler();

        Log.d("HomeFirstFragment", "onCreateView: View initialized");
        return view;
    }

    private void setupCategoriesRecycler() {
        categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        categoriesAdapter = new CategoriesAdapter(new ArrayList<>(), this);
        categoriesRecyclerView.setAdapter(categoriesAdapter);
    }

    private void setupRandomMealRecycler() {
        randomMealRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        randomMealAdapter = new RandomMealAdapter(meal ->
                navigateToDetails(meal.getIdMeal())
        );
        randomMealRecyclerView.setAdapter(randomMealAdapter);
    }

    private void setupFlagsRecycler() {
        flagsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        flagsAdapter = new CountryFlagsAdapter(new ArrayList<>(), this);
        flagsRecyclerView.setAdapter(flagsAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();
        loadData();
        Log.d("HomeFirstFragment", "onViewCreated: Observers set up, data loading");
    }

    private void setupObservers() {
        viewModel.getCategoriesLive().observe(getViewLifecycleOwner(), categories -> {
            Log.d("HomeFirstFragment", "Categories observed, size: " + (categories != null ? categories.size() : 0));
            categoriesAdapter.updateCategories(categories);
            boolean isEmpty = categories == null || categories.isEmpty();
            noDataText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            categoriesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            randomMealRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            flagsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            Log.d("HomeFirstFragment", "no_data_text visibility: " + (isEmpty ? "VISIBLE" : "GONE"));
        });

        viewModel.getRandomMealLive().observe(getViewLifecycleOwner(), meal -> {
            Log.d("HomeFirstFragment", "Random meal observed: " + (meal != null ? meal.getName() : "null"));
            randomMealAdapter.setMeal(meal);
        });

        viewModel.getCountriesLive().observe(getViewLifecycleOwner(), countries -> {
            Log.d("HomeFirstFragment", "Countries observed, size: " + (countries != null ? countries.size() : 0));
            flagsAdapter.updateCountries(countries);
        });

        viewModel.getErrorLive().observe(getViewLifecycleOwner(), message -> {
            Log.d("HomeFirstFragment", "Error observed: " + message);
            handleError(message);
        });
    }

    private void loadData() {
        viewModel.loadCategories();
        viewModel.loadRandomMeal();
        viewModel.loadCountries();
        Log.d("HomeFirstFragment", "loadData: Initiated data loading");
    }

    private void navigateToDetails(String mealId) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeMealDetailsThirdFragment.newInstance(mealId))
                .addToBackStack(null)
                .commit();
    }

    private void handleError(String message) {
        if (message == null) {
            Log.d("HomeFirstFragment", "Null error message, ignoring");
            return;
        }

        // Debounce error handling
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastErrorTime < ERROR_DEBOUNCE_MS) {
            Log.d("HomeFirstFragment", "Error debounced, ignoring: " + message);
            return;
        }
        lastErrorTime = currentTime;

        boolean isNetworkError = message.equals("NO_INTERNET") ||
                message.toLowerCase().contains("timeout") ||
                message.toLowerCase().contains("connectexception") ||
                message.toLowerCase().contains("connect to") ||
                message.toLowerCase().contains("network") ||
                message.toLowerCase().contains("api") ||
                message.toLowerCase().contains("can't connect to api");

        if (isNetworkError) {
            Log.d("HomeFirstFragment", "Showing NoInternetDialog for error: " + message);
            mainHandler.post(() -> {
                if (isAdded() && getContext() != null) {
                    NoInternetDialog.show(getContext(), () -> {
                        if (NetworkUtil.isConnected(getContext())) {
                            loadData();
                        } else {
                            // Reshow dialog if still no internet
                            handleError("NO_INTERNET");
                        }
                    });
                    noDataText.setVisibility(View.VISIBLE);
                    categoriesRecyclerView.setVisibility(View.GONE);
                    randomMealRecyclerView.setVisibility(View.GONE);
                    flagsRecyclerView.setVisibility(View.GONE);
                    Log.d("HomeFirstFragment", "NoInternetDialog triggered, no_data_text set to VISIBLE");
                } else {
                    Log.w("HomeFirstFragment", "Cannot show dialog, fragment not attached");
                    try {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("HomeFirstFragment", "Failed to show Toast: " + e.getMessage());
                    }
                }
            });
        } else {
            Log.d("HomeFirstFragment", "Showing Toast for non-network error: " + message);
            mainHandler.post(() -> {
                try {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("HomeFirstFragment", "Failed to show Toast: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeMealsSecondFragment.newInstance(category.getName(), "category"))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCountryClick(String countryName) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeMealsSecondFragment.newInstance(countryName, "country"))
                .addToBackStack(null)
                .commit();
    }
}