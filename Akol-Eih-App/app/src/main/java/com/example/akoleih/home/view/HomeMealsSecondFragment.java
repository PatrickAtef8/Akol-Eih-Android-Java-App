
// HomeMealsSecondFragment.java
package com.example.akoleih.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.favorite.db.AppDatabase;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.FavoriteRepositoryImpl;
import com.example.akoleih.home.adapter.MealsAdapter;
import com.example.akoleih.home.adapter.MealsAdapter.OnFavClickListener;
import com.example.akoleih.home.adapter.MealsAdapter.OnMealClickListener;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.presenter.HomePresenterImpl;
import com.example.akoleih.home.model.repository.HomeRepositoryImpl;
import com.example.akoleih.home.network.MealRemoteDataSourceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeMealsSecondFragment extends Fragment implements HomeView, OnMealClickListener {
    private HomePresenterImpl homePresenter;
    private FavoriteRepositoryImpl favRepo;
    private MealsAdapter mealsAdapter;
    private final Set<String> favoriteIds = new HashSet<>();
    private String category;

    public static HomeMealsSecondFragment newInstance(String category) {
        HomeMealsSecondFragment fragment = new HomeMealsSecondFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }
        // Home presenter
        homePresenter = new HomePresenterImpl(
                this,
                new HomeRepositoryImpl(null, new MealRemoteDataSourceImpl())
        );
        // Favorites repository
        favRepo = new FavoriteRepositoryImpl(
                AppDatabase.getInstance(requireContext())
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meals, container, false);
        RecyclerView rv = view.findViewById(R.id.meals_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        mealsAdapter = new MealsAdapter(
                null,
                /* meal click */ this,
                /* fav click */ new OnFavClickListener() {
            @Override
            public void onFavClick(Meal meal) {
                FavoriteMeal fm = new FavoriteMeal(
                        meal.getIdMeal(),
                        meal.getName(),
                        meal.getThumbnail()
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
        rv.setAdapter(mealsAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Observe favorites LiveData
        favRepo.getFavorites().observe(getViewLifecycleOwner(), favList -> {
            favoriteIds.clear();
            for (FavoriteMeal fm : favList) {
                favoriteIds.add(fm.getIdMeal());
            }
            mealsAdapter.setFavoriteIds(favoriteIds);
        });
        // Load meals by category
        homePresenter.getMealsByCategory(category);
    }

    @Override
    public void showMealsByCategory(List<Meal> meals) {
        mealsAdapter.updateMeals(meals);
    }

    @Override
    public void onMealClick(Meal meal) {
        HomeMealDetailsThirdFragment fragment =
                HomeMealDetailsThirdFragment.newInstance(meal.getIdMeal());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override public void showCategories(List<Category> categories) { }
    @Override public void showRandomMeal(Meal meal) { }
    @Override public void showMealDetails(Meal meal) { }

    @Override
    public void showError(String message) {
        // show error
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homePresenter.onDestroy();
    }
}
