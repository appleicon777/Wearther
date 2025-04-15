package com.example.wearther;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ClothingItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String category;

    public int warmthLevel;
    public boolean isLayerable;

    public String colorTag;
    public boolean isJean;

    public String imageUri;
}
