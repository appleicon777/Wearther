package com.example.wearther;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private List<ClothingItem> clothingItems = new ArrayList<>();

    public void setItems(List<ClothingItem> items) {
        this.clothingItems = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView, levelView;

        public ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.clothingName);
            levelView = view.findViewById(R.id.clothingWarmth);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClothingItem item = items.get(position);
        holder.nameView.setText(item.name + " (" + item.category + ")");
        holder.levelView.setText("warmthLevel: " + item.warmthLevel);
    }

    @Override
    public int getItemCount() {
        return clothingItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewWarmth;
        ImageView imageViewClothing;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewWarmth = itemView.findViewById(R.id.textViewWarmth);
            imageViewClothing = itemView.findViewById(R.id.imageViewClothing);
        }
    }
}
