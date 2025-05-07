package com.example.akoleih.home.view.fragments.secondfragment;

import android.os.Bundle;
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
import com.example.akoleih.favorite.model.db.AppDatabase;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.FavoriteRepositoryImpl;
import com.example.akoleih.home.view.adapters.meals.MealsAdapter;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.model.repository.HomeViewModel;
import com.example.akoleih.home.view.fragments.thirdfragment.HomeMealDetailsThirdFragment;
import com.example.akoleih.utils.CustomLoginDialog;
import com.example.akoleih.utils.NoInternetDialog;
import com.example.akoleih.utils.SharedPrefUtil;

import java.util.HashSet;
import java.util.Set;

public class HomeMealsSecondFragment extends Fragment implements MealsAdapter.OnMealClickListener {
    private HomeViewModel viewModel;
    private FavoriteRepositoryImpl favRepo;
    private MealsAdapter mealsAdapter;
    private final Set<String> favoriteIds = new HashSet<>();
    private String filterValue;
    private String filterType;
    private TextView noDataText;
    private RecyclerView mealsRecyclerView;

    public static HomeMealsSecondFragment newInstance(String filterValue, String filterType) {
        HomeMealsSecondFragment fragment = new HomeMealsSecondFragment();
        Bundle args = new Bundle();
        args.putString("filter_value", filterValue);
        args.putString("filter_type", filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterValue = getArguments().getString("filter_value");
            filterType = getArguments().getString("filter_type");
        }
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(HomeViewModel.class);
        favRepo = new FavoriteRepositoryImpl(
                AppDatabase.getInstance(requireContext())
        );
        Log.d("HomeMealsSecondFragment", "onCreate: ViewModel and repo initialized, filterType: " + filterType + ", filterValue: " + filterValue);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meals, container, false);
        mealsRecyclerView = view.findViewById(R.id.meals_recycler_view);
        noDataText = view.findViewById(R.id.no_data_text);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mealsAdapter = new MealsAdapter(
                null, this, meal -> {
            if (SharedPrefUtil.isGuestMode(requireContext())) {
                CustomLoginDialog.show(requireContext());
            } else {
                FavoriteMeal fm = new FavoriteMeal(
                        meal.getIdMeal(), meal.getName(), meal.getThumbnail()
                );
                if (favoriteIds.contains(meal.getIdMeal())) {
                    favRepo.removeFavorite(fm);
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favRepo.addFavorite(fm);
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        mealsRecyclerView.setAdapter(mealsAdapter);
        Log.d("HomeMealsSecondFragment", "onCreateView: View initialized");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favRepo.getFavorites().observe(getViewLifecycleOwner(), favList -> {
            favoriteIds.clear();
            for (FavoriteMeal fm : favList) {
                favoriteIds.add(fm.getIdMeal());
            }
            mealsAdapter.setFavoriteIds(favoriteIds);
            Log.d("HomeMealsSecondFragment", "Favorites observed, size: " + favoriteIds.size());
        });

        viewModel.getMealsByCategoryLive().observe(getViewLifecycleOwner(), meals -> {
            Log.d("HomeMealsSecondFragment", "Meals observed, size: " + (meals != null ? meals.size() : 0));
            mealsAdapter.updateMeals(meals);
            boolean isEmpty = meals == null || meals.isEmpty();
            noDataText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            mealsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            Log.d("HomeMealsSecondFragment", "no_data_text visibility: " + (isEmpty ? "VISIBLE" : "GONE"));
        });

        viewModel.getErrorLive().observe(getViewLifecycleOwner(), message -> {
            Log.d("HomeMealsSecondFragment", "Error observed: " + message);
            handleError(message);
        });

        if ("category".equals(filterType)) {
            viewModel.loadMealsByCategory(filterValue);
        } else if ("country".equals(filterType)) {
            viewModel.loadMealsByCountry(filterValue);
        }
        Log.d("HomeMealsSecondFragment", "onViewCreated: Loading meals for " + filterType + ": " + filterValue);
    }

    @Override
    public void onMealClick(Meal meal) {
        HomeMealDetailsThirdFragment fragment = HomeMealDetailsThirdFragment.newInstance(meal.getIdMeal());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void handleError(String message) {
        if (message != null && (message.equals("NO_INTERNET") ||
                message.toLowerCase().contains("timeout") ||
                message.toLowerCase().contains("connectexception") ||
                message.toLowerCase().contains("connect to") ||
                message.toLowerCase().contains("network") ||
                message.toLowerCase().contains("api") ||
                message.toLowerCase().contains("can't connect to api"))) {
            Log.d("HomeMealsSecondFragment", "Showing NoInternetDialog for error: " + message);
            if (isAdded()) {
                NoInternetDialog.show(requireContext(), () -> {
                    if ("category".equals(filterType)) {
                        viewModel.loadMealsByCategory(filterValue);
                    } else if ("country".equals(filterType)) {
                        viewModel.loadMealsByCountry(filterValue);
                    }
                });
                noDataText.setVisibility(View.VISIBLE);
                mealsRecyclerView.setVisibility(View.GONE);
                Log.d("HomeMealsSecondFragment", "NoInternetDialog triggered, no_data_text set to VISIBLE");
            } else {
                Log.w("HomeMealsSecondFragment", "Cannot show dialog, fragment not attached");
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("HomeMealsSecondFragment", "Showing Toast for non-network error: " + (message != null ? message : "null"));
            Toast.makeText(getContext(), message != null ? message : "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }
}