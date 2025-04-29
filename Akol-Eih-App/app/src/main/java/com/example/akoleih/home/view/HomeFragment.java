// HomeFragment.java
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
import com.example.akoleih.home.adapter.RandomMealAdapter;
import com.example.akoleih.home.contract.HomeContract;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.network.RetrofitClient;
import com.example.akoleih.home.presenter.HomePresenter;
import com.example.akoleih.home.model.repository.HomeRepositoryImpl;
import com.example.akoleih.home.network.api.CategoriesRemoteDataSource;
import com.example.akoleih.home.network.api.MealRemoteDataSource;
import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View, CategoriesAdapter.OnCategoryClickListener {
    private HomePresenter presenter;
    private CategoriesAdapter categoriesAdapter;
    private RandomMealAdapter randomMealAdapter;
    private RecyclerView categoriesRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CategoriesRemoteDataSource categoriesRemoteDataSource = RetrofitClient.getInstance()
                .create(CategoriesRemoteDataSource.class);
        MealRemoteDataSource mealRemoteDataSource = RetrofitClient.getInstance()
                .create(MealRemoteDataSource.class);

        presenter = new HomePresenter(this, new HomeRepositoryImpl(categoriesRemoteDataSource, mealRemoteDataSource));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesAdapter = new CategoriesAdapter(null, this);
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        RecyclerView randomMealRecyclerView = view.findViewById(R.id.random_meal_recycler_view);
        randomMealRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        randomMealAdapter = new RandomMealAdapter();
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
        categoriesAdapter.updateCategories(categories);
    }

    @Override
    public void showRandomMeal(Meal meal) {
        randomMealAdapter.setMeal(meal);
    }

    @Override
    public void onCategoryClick(Category category) {
        MealsFragment fragment = MealsFragment.newInstance(category.getName());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showMealsByCategory(List<Meal> meals) {}
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