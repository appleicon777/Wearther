package com.example.wearther;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface WeatherService {
    @GET("getVilageFcst")
    Call<WeatherResponse> getForecast(
            @Query("serviceKey") String serviceKey,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("dataType") String dataType,
            @Query("base_date") String baseDate,
            @Query("base_time") String baseTime,
            @Query("nx") int nx,
            @Query("ny") int ny
    );
}