/*
 * SearchMealsAdapter.java
 */
package com.example.akoleih.search.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.search.model.SearchMeal;
import com.squareup.picasso.Picasso;
import java.util.List;

public class SearchMealsAdapter extends RecyclerView.Adapter<SearchMealsAdapter.MealViewHolder> {
    private List<SearchMeal> meals;
    private OnMealClickListener listener;

    /**
     * Interface for click callbacks on meals.
     */
    public interface OnMealClickListener {
        void onMealClick(SearchMeal meal);
    }

    public SearchMealsAdapter(List<SearchMeal> meals, OnMealClickListener listener) {
        this.meals = meals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        SearchMeal meal = meals.get(position);
        holder.mealName.setText(meal.getName());
        Picasso.get()
                .load(meal.getThumbnail())
                .placeholder(R.drawable.foodloading)
                .error(R.drawable.foodloading)
                .into(holder.mealImage);
        holder.iv_fav.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    public void updateMeals(List<SearchMeal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;
        ImageView iv_fav;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            iv_fav = itemView.findViewById(R.id.iv_favorite);
        }
    }
}

