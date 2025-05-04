// HomeMealDetailsThirdFragment.java
package com.example.akoleih.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.akoleih.home.adapter.IngredientAdapter;
import com.example.akoleih.home.model.Category;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.presenter.HomePresenterImpl;
import com.example.akoleih.home.model.repository.HomeRepositoryImpl;
import com.example.akoleih.home.network.MealRemoteDataSourceImpl;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeMealDetailsThirdFragment extends Fragment implements HomeView {
    private HomePresenterImpl presenter;
    private FavoriteRepositoryImpl favRepo;
    private String mealId;
    private ShapeableImageView mealImage;
    private TextView mealName, mealArea, mealInstructions;
    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter ingredientsAdapter;
    private MaterialButton watchButton;
    private ImageView favIcon;
    private String youtubeUrl;
    private boolean isFavorite = false;
    private final Set<String> favoriteIds = new HashSet<>();

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
        presenter = new HomePresenterImpl(
                this,
                new HomeRepositoryImpl(null, new MealRemoteDataSourceImpl())
        );
        // Initialize favorites repository
        favRepo = new FavoriteRepositoryImpl(
                AppDatabase.getInstance(requireContext())
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_details, container, false);

        mealImage = view.findViewById(R.id.meal_image);
        mealName = view.findViewById(R.id.meal_name);
        mealArea = view.findViewById(R.id.meal_area);
        mealInstructions = view.findViewById(R.id.meal_instructions);
        watchButton = view.findViewById(R.id.watch);
        favIcon = view.findViewById(R.id.iv_favorite);

        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        ingredientsAdapter = new IngredientAdapter();
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Observe favorites to update icon state
        favRepo.getFavorites().observe(getViewLifecycleOwner(), favList -> {
            favoriteIds.clear();
            for (FavoriteMeal fm : favList) {
                favoriteIds.add(fm.getIdMeal());
            }
            isFavorite = favoriteIds.contains(mealId);
            updateFavIcon();
        });

        // Load meal details
        presenter.getMealDetails(mealId);
    }

    @Override
    public void showMealDetails(Meal meal) {
        if (getContext() != null) {
            Picasso.get()
                    .load(meal.getThumbnail())
                    .placeholder(R.drawable.foodloading)
                    .error(R.drawable.foodloading)
                    .into(mealImage);

            mealName.setText(meal.getName());
            mealArea.setText(meal.getArea());
            mealInstructions.setText(meal.getInstructions());
            youtubeUrl = meal.getYoutubeUrl();

            ingredientsAdapter.updateIngredients(meal.getIngredientList());

            // Setup watch button
            watchButton.setOnClickListener(v -> {
                if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
                    VideoFragment videoFragment = VideoFragment.newInstance(youtubeUrl);
                    videoFragment.show(getParentFragmentManager(), "video_dialog");
                } else {
                    showError("No video available for this meal");
                }
            });

            // Setup favorite toggle
            favIcon.setOnClickListener(v -> {
                FavoriteMeal fm = new FavoriteMeal(
                        meal.getIdMeal(),
                        meal.getName(),
                        meal.getThumbnail()
                );
                if (isFavorite) {
                    favRepo.removeFavorite(fm);
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favRepo.addFavorite(fm);
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateFavIcon() {
        favIcon.setImageResource(
                isFavorite
                        ? R.drawable.remove
                        : R.drawable.wishlist
        );
    }

    @Override public void showCategories(List<Category> categories) { }
    @Override public void showRandomMeal(Meal meal) { }
    @Override public void showMealsByCategory(List<Meal> meals) { }

    @Override
    public void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
