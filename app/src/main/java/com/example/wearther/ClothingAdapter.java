package com.example.wearther;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private List<ClothingItem> items = new ArrayList<>();

    public void setItems(List<ClothingItem> newItems) {
        this.items = newItems;
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
    public ClothingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        return items.size();
    }
}
