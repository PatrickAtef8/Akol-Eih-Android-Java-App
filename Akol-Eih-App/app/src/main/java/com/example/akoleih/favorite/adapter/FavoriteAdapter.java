package com.example.akoleih.favorite.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.akoleih.R;
import com.example.akoleih.favorite.model.FavoriteMeal;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavVH> {
    public interface OnFavDeleteClickListener {
        void onDelete(FavoriteMeal meal);
    }

    private List<FavoriteMeal> data;
    private final OnFavDeleteClickListener listener;

    public FavoriteAdapter(OnFavDeleteClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<FavoriteMeal> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public FavVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new FavVH(v);
    }

    @Override
    public void onBindViewHolder(FavVH holder, int pos) {
        FavoriteMeal m = data.get(pos);
        holder.title.setText(m.getStrMeal());
        Glide.with(holder.itemView.getContext())
                .load(m.getStrMealThumb()) // Ensure this URL is correct
                .placeholder(R.drawable.foodloading) // Your placeholder
                .error(R.drawable.foodloading) // Your error image
                .into(holder.thumb);        holder.deleteBtn.setOnClickListener(v -> listener.onDelete(m));
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }

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
