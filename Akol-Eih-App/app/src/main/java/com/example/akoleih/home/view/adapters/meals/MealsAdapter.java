// MealsAdapter.java
package com.example.akoleih.home.view.adapters.meals;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.home.model.Meal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.ViewHolder> {
    public interface OnMealClickListener { void onMealClick(Meal meal); }
    public interface OnFavClickListener { void onFavClick(Meal meal); }

    private List<Meal> meals;
    private final OnMealClickListener mealListener;
    private final OnFavClickListener favListener;
    private Set<String> favoriteIds = new HashSet<>();

    public MealsAdapter(List<Meal> meals,
                        OnMealClickListener mealListener,
                        OnFavClickListener favListener) {
        this.meals = meals != null ? meals : new ArrayList<>();
        this.mealListener = mealListener;
        this.favListener = favListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateMeals(List<Meal> newMeals) {
        this.meals = newMeals != null ? newMeals : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Update the set of favorite IDs and refresh icons
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setFavoriteIds(Set<String> favoriteIds) {
        this.favoriteIds = favoriteIds != null
                ? new HashSet<>(favoriteIds)
                : new HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Meal meal = meals.get(pos);
        holder.name.setText(meal.getName());
        Picasso.get()
                .load(meal.getThumbnail())
                .placeholder(R.drawable.foodloading)
                .error(R.drawable.foodloading)
                .into(holder.image);

        // Regular item click (navigate to details)
        holder.itemView.setOnClickListener(v -> mealListener.onMealClick(meal));

        // Favorite icon toggle
        boolean isFav = favoriteIds.contains(meal.getIdMeal());
        holder.favIcon.setImageResource(
                isFav
                        ? R.drawable.clcikedfav
                        : R.drawable.notclickedfav
        );
        holder.favIcon.setOnClickListener(v -> favListener.onFavClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        ImageView favIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image   = itemView.findViewById(R.id.meal_image);
            name    = itemView.findViewById(R.id.meal_name);
            favIcon = itemView.findViewById(R.id.iv_favorite);
        }
    }
}
