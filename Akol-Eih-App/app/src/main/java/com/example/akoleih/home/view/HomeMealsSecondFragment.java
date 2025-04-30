// MealsFragment.java
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
import com.example.akoleih.home.adapter.MealsAdapter;
import com.example.akoleih.home.contract.HomeContract;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.presenter.HomePresenter;
import com.example.akoleih.home.network.RetrofitClient;
import com.example.akoleih.home.model.repository.HomeRepositoryImpl;
import com.example.akoleih.home.network.api.CategoriesRemoteDataSource;
import com.example.akoleih.home.network.api.MealRemoteDataSource;
import java.util.List;

public class HomeMealsSecondFragment extends Fragment implements HomeContract.View, MealsAdapter.OnMealClickListener {
    private HomePresenter presenter;
    private MealsAdapter mealsAdapter;
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

        CategoriesRemoteDataSource categoriesRemoteDataSource = RetrofitClient.getInstance()
                .create(CategoriesRemoteDataSource.class);
        MealRemoteDataSource mealRemoteDataSource = RetrofitClient.getInstance()
                .create(MealRemoteDataSource.class);

        presenter = new HomePresenter(this, new HomeRepositoryImpl(categoriesRemoteDataSource, mealRemoteDataSource));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meals, container, false);

        RecyclerView mealsRecyclerView = view.findViewById(R.id.meals_recycler_view);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mealsAdapter = new MealsAdapter(null, this);
        mealsRecyclerView.setAdapter(mealsAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.getMealsByCategory(category);
    }

    @Override
    public void showMealsByCategory(List<Meal> meals) {
        mealsAdapter.updateMeals(meals);
    }

    @Override
    public void onMealClick(Meal meal) {
        HomeMealDetailsThirdFragment fragment = HomeMealDetailsThirdFragment.newInstance(meal.getIdMeal());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // Implement other required methods from HomeContract.View
    @Override
    public void showCategories(List<Category> categories) {}
    @Override
    public void showRandomMeal(Meal meal) {}
    @Override
    public void showMealDetails(Meal meal) {}
    @Override
    public void showError(String message) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}