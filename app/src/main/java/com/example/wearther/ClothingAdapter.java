package com.example.wearther;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private List<ClothingItem> clothingItems = new ArrayList<>();

    public void setItems(List<ClothingItem> items) {
        this.clothingItems = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewClothing;

        public ViewHolder(View view) {
            super(view);
            imageViewClothing = view.findViewById(R.id.imageViewClothing);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);
        if (item.imageUri != null && !item.imageUri.isEmpty()) {
            Glide.with(holder.imageViewClothing.getContext())
                    .load(item.imageUri)
                    .into(holder.imageViewClothing);
        } else {
            holder.imageViewClothing.setImageResource(android.R.color.darker_gray);
        }
    }

    @Override
    public int getItemCount() {
        return clothingItems.size();
    }
}
