package com.example.wearther;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private List<ClothingItem> items = new ArrayList<>();

    public void setItems(List<ClothingItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewClothing;
        TextView textViewName, levelView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewClothing = itemView.findViewById(R.id.imageViewClothing);
            textViewName = itemView.findViewById(R.id.textViewName);
            levelView = itemView.findViewById(R.id.clothingWarmth);
        }
    }

    @Override
    public ClothingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClothingItem item = items.get(position);
        holder.textViewName.setText(item.name + " (" + item.category + ")");
        holder.levelView.setText("warmthLevel: " + item.warmthLevel);

        // 이미지 표시
        Glide.with(holder.itemView.getContext())
                .load(item.imageUri)
                .placeholder(R.drawable.ic_launcher_background) // 기본 이미지
                .into(holder.imageViewClothing);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
