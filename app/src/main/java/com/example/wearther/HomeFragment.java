package com.example.wearther;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private final String SERVICE_KEY = "akcVCcpt2hMlytifqM9VPdV0gTs08X0nS5j09JImhRmA8pvGjNExBs80aLUvJ26uk7n0XUVXltc52mlfgmONQw==";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ForecastAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ForecastAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 위치 고정: 서울시청 격자 좌표
        int nx = 60;
        int ny = 127;
        fetchWeather(nx, ny);

        return view;
    }

    private void fetchWeather(int nx, int ny) {
        progressBar.setVisibility(View.VISIBLE);

        String baseDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String baseTime = getBaseTime();

        Log.d("WeatherDebug", "baseDate: " + baseDate + ", baseTime: " + baseTime);
        Log.d("WeatherDebug", "nx: " + nx + ", ny: " + ny);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherResponse> call = service.getForecast(
                SERVICE_KEY, 1, 1000, "JSON", baseDate, baseTime, nx, ny);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().response != null) {
                    WeatherResponse.Response res = response.body().response;
                    if (res.body != null && res.body.items != null) {
                        List<WeatherResponse.ForecastItem> items = res.body.items.item;
                        Map<String, ForecastItemData> forecastMap = new HashMap<>();

                        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

                        for (WeatherResponse.ForecastItem item : items) {
                            if (!today.equals(item.fcstDate)) continue;

                            ForecastItemData data = forecastMap.getOrDefault(item.fcstTime, new ForecastItemData(item.fcstTime));
                            switch (item.category) {
                                case "TMP": data.temperature = item.fcstValue; break;
                                case "SKY": data.sky = item.fcstValue; break;
                                case "PTY": data.pty = item.fcstValue; break;
                            }
                            forecastMap.put(item.fcstTime, data);
                        }

                        List<ForecastItemData> forecastList = new ArrayList<>(forecastMap.values());
                        forecastList.sort((a, b) -> a.time.compareTo(b.time));
                        adapter.setItems(forecastList);
                    } else {
                        Toast.makeText(requireContext(), "데이터를 불러오지 못했습니다 (body null)", Toast.LENGTH_SHORT).show();
                        Log.e("WeatherDebug", "items null or body null");
                    }
                } else {
                    Toast.makeText(requireContext(), "응답 실패 또는 데이터 없음", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherDebug", "응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WeatherDebug", "통신 오류", t);
            }
        });
    }

    private String getBaseTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        if (minute < 45) hour--;
        if (hour < 0) hour = 23;

        int[] baseHours = {2, 5, 8, 11, 14, 17, 20, 23};
        for (int i = baseHours.length - 1; i >= 0; i--) {
            if (hour >= baseHours[i]) return String.format("%02d00", baseHours[i]);
        }
        return "2300";
    }
}
