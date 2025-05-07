package com.example.akoleih.favorite.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.akoleih.R;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.view.listener.OnMealClickListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavVH> {
    public interface OnFavDeleteClickListener {
        void onDelete(FavoriteMeal meal, int position);
    }

    private ArrayList<FavoriteMeal> data = new ArrayList<>();
    private final OnFavDeleteClickListener deleteListener;
    private OnMealClickListener mealListener;

    public FavoriteAdapter(OnFavDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setMealClickListener(OnMealClickListener listener) {
        this.mealListener = listener;
    }

    public void setData(List<FavoriteMeal> newData) {
        data.clear();
        if (newData != null) {
            data.addAll(newData);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void restoreItem(FavoriteMeal meal, int position) {
        if (position >= 0 && position <= data.size()) {
            data.add(position, meal);
            notifyItemInserted(position);
        }
    }

    @NonNull
    @Override
    public FavVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new FavVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavVH holder, int position) {
        FavoriteMeal meal = data.get(position);
        holder.title.setText(meal.getStrMeal());

        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.foodloading)
                .error(R.drawable.foodloading)
                .into(holder.thumb);

        holder.deleteBtn.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && deleteListener != null) {
                deleteListener.onDelete(data.get(pos), pos);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (mealListener != null) {
                mealListener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<FavoriteMeal> getItems() {
        return data;
    }

    static class FavVH extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumb;
        ImageButton deleteBtn;

        FavVH(View item) {
            super(item);
            title = item.findViewById(R.id.tvFavName);
            thumb = item.findViewById(R.id.ivFavThumb);
            deleteBtn = item.findViewById(R.id.btnDeleteFav);
        }
    }
}