package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClothingItemDao {

    @Insert
    void insert(ClothingItem item);

    @Delete
    void delete(ClothingItem item);

    @Query("SELECT * FROM ClothingItem")
    List<ClothingItem> getAll();

    @Query("SELECT * FROM ClothingItem WHERE id = :id")
    ClothingItem getById(int id);
}
