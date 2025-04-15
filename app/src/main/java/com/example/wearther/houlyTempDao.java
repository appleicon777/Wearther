package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface houlyTempDao {

    @Insert
    void insertAll(List<houlyTemp> temps);

    @Query("SELECT * FROM houlyTemp WHERE weatherInfoId = :weatherId")
    List<houlyTemp> getTempsForWeather(int weatherId);
}
