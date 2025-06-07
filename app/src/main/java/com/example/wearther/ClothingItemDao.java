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

    // 전체 치환 -> 사용자 아이디로 필터링
    @Query("SELECT * FROM ClothingItem WHERE userId = :userId")
    List<ClothingItem> getItemsByUserId(String userId);

    @Query("SELECT * FROM ClothingItem WHERE id = :id")
    ClothingItem getById(int id);
}
