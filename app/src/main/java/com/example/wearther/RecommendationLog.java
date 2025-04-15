package com.example.wearther;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Feedback.class,
                parentColumns = "id",
                childColumns = "feedbackId",
                onDelete = ForeignKey.SET_NULL)
})
public class RecommendationLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long timestamp;
    public String recommendedItemIds; // "1,4,7"
    public Integer feedbackId;
}
