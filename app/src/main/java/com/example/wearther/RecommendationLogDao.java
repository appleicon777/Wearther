package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecommendationLogDao {

    @Insert
    void insert(RecommendationLog log);

    @Query("SELECT * FROM RecommendationLog ORDER BY timestamp DESC")
    List<RecommendationLog> getAll();
}
