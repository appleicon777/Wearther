// RecommendationAdapter.java
package com.example.wearther;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private List<ClothingItem> items;

    public RecommendationAdapter(List<ClothingItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecommendationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationAdapter.ViewHolder holder, int position) {
        ClothingItem item = items.get(position);
        Glide.with(holder.imageRecommendation.getContext())
                .load(item.imageUri)
                .into(holder.imageRecommendation);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageRecommendation;

        public ViewHolder(View view) {
            super(view);
            imageRecommendation = view.findViewById(R.id.imageRecommendation);
        }
    }
}
