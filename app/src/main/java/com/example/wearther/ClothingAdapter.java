package com.example.wearther;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private List<ClothingItem> clothingItems = new ArrayList<>();
    private boolean selectionMode = false;
    private Set<Integer> selectedPositions = new HashSet<>();

    public void setItems(List<ClothingItem> items) {
        this.clothingItems = items;
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    public void setSelectionMode(boolean mode) {
        this.selectionMode = mode;
        if (!mode) {
            selectedPositions.clear();
        }
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public List<ClothingItem> getSelectedItems() {
        List<ClothingItem> selected = new ArrayList<>();
        for (int pos : selectedPositions) {
            selected.add(clothingItems.get(pos));
        }
        return selected;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewClothing;
        public CheckBox checkBoxSelect;

        public ViewHolder(View view) {
            super(view);
            imageViewClothing = view.findViewById(R.id.imageViewClothing);
            checkBoxSelect = view.findViewById(R.id.checkBoxSelect);
        }
    }

    @NonNull
    @Override
    public ClothingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingAdapter.ViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);
        if (item.imageUri != null && !item.imageUri.isEmpty()) {
            Glide.with(holder.imageViewClothing.getContext())
                    .load(item.imageUri)
                    .into(holder.imageViewClothing);
        } else {
            holder.imageViewClothing.setImageResource(android.R.color.darker_gray);
        }
        // 선택 모드일 경우, 체크박스 보여주기
        if (selectionMode) {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
            holder.checkBoxSelect.setChecked(selectedPositions.contains(position));
        } else {
            holder.checkBoxSelect.setVisibility(View.GONE);
        }

        // 클릭 시 선택 모드이면 체크박스 상태 토글
        holder.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position);
                    holder.checkBoxSelect.setChecked(false);
                } else {
                    selectedPositions.add(position);
                    holder.checkBoxSelect.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return clothingItems.size();
    }
}
