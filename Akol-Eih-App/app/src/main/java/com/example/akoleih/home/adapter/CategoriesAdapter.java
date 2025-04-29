// CategoriesAdapter.java
package com.example.akoleih.home.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.akoleih.R;
import com.example.akoleih.home.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Category> categories;
    private OnCategoryClickListener listener;

    public CategoriesAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories != null ? categories : new ArrayList<>();
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories != null ? newCategories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.name.setText(category.getName());
        Glide.with(holder.itemView.getContext())
                .load(category.getThumbnail())
                .placeholder(R.drawable.foodloading)
                .error(R.drawable.foodloading)
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.category_image);
            name = itemView.findViewById(R.id.category_name);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}