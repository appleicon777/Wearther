package com.example.wearther;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ActivityInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long startTime;
    public long endTime;

    public boolean isOutdoors;
    public boolean isPhysicallyActive;
}
