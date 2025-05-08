package com.example.akoleih.calendar.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.view.adapter.listener.OnDeleteClickListener;
import com.example.akoleih.calendar.view.adapter.listener.OnPlannedMealClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private final List<CalendarMeal> meals = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;
    private final OnPlannedMealClickListener mealClickListener;

    public CalendarAdapter(OnDeleteClickListener deleteClickListener, OnPlannedMealClickListener mealClickListener) {
        this.deleteClickListener = deleteClickListener;
        this.mealClickListener = mealClickListener;
    }

    public void setMeals(List<CalendarMeal> newMeals) {
        meals.clear();
        meals.addAll(newMeals);
        notifyDataSetChanged();
    }


    public void addItem(int position, CalendarMeal meal) {
        meals.add(position, meal);
        notifyItemInserted(position);


    }
/*******************************view holder*************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_meal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarMeal meal = meals.get(position);
        holder.mealName.setText(meal.getMealName());
        Picasso.get()
                .load(meal.getMealThumb())
                .placeholder(R.drawable.gradient_overlay)
                .error(R.drawable.ic_no_results)
                .into(holder.mealThumb);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClickListener.onDeleteClick(meal, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealClickListener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView mealThumb;
        TextView mealName;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            mealThumb = itemView.findViewById(R.id.mealThumb);
            mealName = itemView.findViewById(R.id.mealName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}