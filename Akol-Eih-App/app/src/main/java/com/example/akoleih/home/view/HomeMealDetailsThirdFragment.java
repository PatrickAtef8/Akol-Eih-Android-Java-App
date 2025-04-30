// MealDetailsFragment.java
package com.example.akoleih.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.akoleih.R;
import com.example.akoleih.home.contract.HomeContract;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.presenter.HomePresenter;
import com.example.akoleih.home.network.RetrofitClient;
import com.example.akoleih.home.model.repository.HomeRepositoryImpl;
import com.example.akoleih.home.network.api.CategoriesRemoteDataSource;
import com.example.akoleih.home.network.api.MealRemoteDataSource;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class HomeMealDetailsThirdFragment extends Fragment implements HomeContract.View {
    private HomePresenter presenter;
    private String mealId;
    private ShapeableImageView mealImage;
    private TextView mealName, mealArea, mealInstructions, mealIngredients;

    public static HomeMealDetailsThirdFragment newInstance(String mealId) {
        HomeMealDetailsThirdFragment fragment = new HomeMealDetailsThirdFragment();
        Bundle args = new Bundle();
        args.putString("mealId", mealId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealId = getArguments().getString("mealId");
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
        View view = inflater.inflate(R.layout.fragment_meal_details, container, false);

        mealImage = view.findViewById(R.id.meal_image);
        mealName = view.findViewById(R.id.meal_name);
        mealArea = view.findViewById(R.id.meal_area);
        mealInstructions = view.findViewById(R.id.meal_instructions);
        mealIngredients = view.findViewById(R.id.meal_ingredients);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.getMealDetails(mealId);
    }

    @Override
    public void showMealDetails(Meal meal) {
        Glide.with(this)
                .load(meal.getThumbnail())
                .placeholder(R.drawable.foodloading)
                .error(R.drawable.foodloading)
                .into(mealImage);

        mealName.setText(meal.getName());
        mealArea.setText(meal.getArea());
        mealInstructions.setText(meal.getInstructions());
        mealIngredients.setText(meal.getIngredients());
    }

    // Implement other required methods from HomeContract.View
    @Override
    public void showCategories(List<Category> categories) {}
    @Override
    public void showRandomMeal(Meal meal) {}
    @Override
    public void showMealsByCategory(List<Meal> meals) {}
    @Override
    public void showError(String message) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}