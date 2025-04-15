package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserSettingDao {

    @Insert
    void insert(UserSetting settings);

    @Query("SELECT * FROM UserSetting WHERE userId = :userId")
    UserSetting getByUserId(int userId);

    @Update
    void update(UserSetting settings);
}

