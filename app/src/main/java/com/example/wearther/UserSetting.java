package com.example.wearther;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
))

public class UserSetting {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int tempSensitivity;
    public String stylePreference;
}
