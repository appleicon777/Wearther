package com.example.wearther;

import android.app.Dialog;
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

    private List<ClothingItem> items = new ArrayList<>();

    public void setItems(List<ClothingItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewClothing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewClothing = itemView.findViewById(R.id.imageViewClothing);
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

        Glide.with(holder.itemView.getContext())
                .load(item.imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageViewClothing);

        holder.imageViewClothing.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_image_preview, null);
            ImageView preview = dialogView.findViewById(R.id.imageViewPreview);
            Glide.with(v.getContext()).load(item.imageUri).into(preview);
            dialog.setContentView(dialogView);
            dialog.show();
            preview.setOnClickListener(view -> dialog.dismiss());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
