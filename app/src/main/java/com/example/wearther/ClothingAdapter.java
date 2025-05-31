package com.example.wearther;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);
        holder.textViewName.setText(item.name);
        holder.textViewWarmth.setText("Warmth Level: " + item.warmthLevel);

        // 이미지 URI → Glide로 이미지 로딩
        if (item.imageUri != null && !item.imageUri.trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(new File(item.imageUri))
                    .placeholder(R.drawable.ic_launcher_background) // 이미지 없을 때 대체
                    .into(holder.imageViewClothing);
        } else {
            holder.imageViewClothing.setImageResource(R.drawable.ic_launcher_background); // 기본 이미지
        }
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
