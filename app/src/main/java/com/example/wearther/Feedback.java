package com.example.wearther;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Feedback {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String result; // "too cold", "just right", "too hot"
    public long date;
    public boolean adjusted;
}
