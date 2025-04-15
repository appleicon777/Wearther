package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FeedbackDao {

    @Insert
    void insert(Feedback feedback);

    @Query("SELECT * FROM Feedback")
    List<Feedback> getAll();

    @Query("SELECT * FROM Feedback WHERE id = :id")
    Feedback getById(int id);
}
