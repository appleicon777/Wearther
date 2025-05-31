package com.example.wearther;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ActivityInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int startHour;
    public int endHour;
    public boolean isOutdoors;
    public boolean isPhysicallyActive;
}
