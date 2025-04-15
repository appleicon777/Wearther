package com.example.wearther;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeatherInfoDao {

    @Insert
    void insert(WeatherInfo weather);

    @Query("SELECT * FROM WeatherInfo ORDER BY timestamp DESC LIMIT 1")
    WeatherInfo getLatest();
}
