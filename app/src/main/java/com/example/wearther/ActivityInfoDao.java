package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ActivityInfoDao {

    @Insert
    void insert(ActivityInfo activity);

    @Query("SELECT * FROM ActivityInfo")
    List<ActivityInfo> getAll();

    @Query("SELECT * FROM ActivityInfo WHERE id = :id")
    ActivityInfo getById(int id);

    @Delete
    void delete(ActivityInfo activity);
}
