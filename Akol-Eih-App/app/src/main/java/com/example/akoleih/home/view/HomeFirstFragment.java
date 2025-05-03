package com.example.akoleih.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.home.adapter.CategoriesAdapter;
import com.example.akoleih.home.adapter.OnCategoryClickListener;
import com.example.akoleih.home.adapter.RandomMealAdapter;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.presenter.HomePresenterImpl;
import com.example.akoleih.home.model.repository.HomeRepositoryImpl;
import com.example.akoleih.home.network.CategoryRemoteDataSourceImpl;
import com.example.akoleih.home.network.MealRemoteDataSourceImpl;

import java.util.List;

public class HomeFirstFragment extends Fragment implements HomeView, OnCategoryClickListener {
    private HomePresenterImpl presenter;
    private CategoriesAdapter categoriesAdapter;
    private RandomMealAdapter randomMealAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new HomePresenterImpl(this,
                new HomeRepositoryImpl(new CategoryRemoteDataSourceImpl(),new MealRemoteDataSourceImpl()
                )
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesAdapter = new CategoriesAdapter(null, this);
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        RecyclerView randomMealRecyclerView = view.findViewById(R.id.random_meal_recycler_view);
        randomMealRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        randomMealAdapter = new RandomMealAdapter(meal -> {
            HomeMealDetailsThirdFragment fragment = HomeMealDetailsThirdFragment.newInstance(meal.getIdMeal());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        randomMealRecyclerView.setAdapter(randomMealAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.getCategories();
        presenter.getRandomMeal();
    }

    @Override
    public void showCategories(List<Category> categories) {
        if (categoriesAdapter != null) {
            categoriesAdapter.updateCategories(categories);
        }
    }

    @Override
    public void showRandomMeal(Meal meal) {
        if (randomMealAdapter != null) {
            randomMealAdapter.setMeal(meal);
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        if (getActivity() != null) {
            HomeMealsSecondFragment fragment = HomeMealsSecondFragment.newInstance(category.getName());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void showMealsByCategory(List<Meal> meals) {}

    @Override
    public void showMealDetails(Meal meal) {}

    @Override
    public void showError(String message) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}