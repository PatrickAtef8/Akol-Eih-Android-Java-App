package com.example.akoleih.home.view.adapters.countries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.utils.CountryFlagUtil;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class CountryFlagsAdapter extends RecyclerView.Adapter<CountryFlagsAdapter.ViewHolder> {

    private final List<String> countries;
    private final OnCountryClickListener listener;

    public CountryFlagsAdapter(List<String> countries, OnCountryClickListener listener) {
        this.countries = countries != null ? countries : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String country = countries.get(position);
        String countryCode = CountryFlagUtil.getCountryCode(country);

        holder.countryName.setText(country);
        if (countryCode != null) {
            String flagUrl = "https://flagcdn.com/w640/" + countryCode.toLowerCase() + ".png";
            Picasso.get()
                    .load(flagUrl)
                    .placeholder(R.drawable.gradient_overlay)
                    .error(R.drawable.ic_no_results)
                    .into(holder.flagImage);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCountryClick(country);
            }
        });
    }

    public void updateCountries(List<String> newCountries) {
        countries.clear();
        if (newCountries != null) {
            countries.addAll(newCountries);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView flagImage;
        TextView countryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flagImage = itemView.findViewById(R.id.flag_image);
            countryName = itemView.findViewById(R.id.country_name);
        }
    }
}