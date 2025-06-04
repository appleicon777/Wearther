package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FeedbackDao {
    @Insert
    void insert(Feedback feedback);

    @Query("SELECT * FROM Feedback WHERE conditionKey = :hash LIMIT 1")
    Feedback getByConditionHash(String hash);

    @Query("SELECT * FROM Feedback WHERE conditionKey = :hash ORDER BY date DESC LIMIT :limit")
    List<Feedback> getRecentFeedbacks(String hash, int limit);
}


