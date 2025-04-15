package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE id = :id")
    User getById(int id);

    @Delete
    void delete(User user);
}
