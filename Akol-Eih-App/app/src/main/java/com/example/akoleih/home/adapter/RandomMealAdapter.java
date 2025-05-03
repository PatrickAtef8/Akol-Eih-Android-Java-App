package com.example.akoleih.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.akoleih.R;
import com.example.akoleih.home.model.Meal;

public class RandomMealAdapter extends RecyclerView.Adapter<RandomMealAdapter.ViewHolder> {
    private Meal meal;
    private OnRandomMealClickListener listener;

    public RandomMealAdapter(OnRandomMealClickListener listener) {
        this.listener = listener;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_random_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (meal != null) {
            holder.name.setText(meal.getName());
            Glide.with(holder.itemView.getContext())
                    .load(meal.getThumbnail())
                    .placeholder(R.drawable.food_placeholder)
                    .error(R.drawable.food_placeholder)
                    .into(holder.image);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRandomMealClick(meal);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return meal != null ? 1 : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.meal_image);
            name = itemView.findViewById(R.id.meal_name);
        }
    }
}