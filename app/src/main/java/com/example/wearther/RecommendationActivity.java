package com.example.wearther;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RecommendationActivity extends AppCompatActivity {

    private TextView resultText;
    private RecyclerView recyclerView;
    private ClothingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        resultText = findViewById(R.id.resultText);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ClothingAdapter();
        recyclerView.setAdapter(adapter);

        // Room DB 접근은 반드시 백그라운드 스레드에서!
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            // 최근 활동 정보 가져오기
            List<ActivityInfo> activityList = db.activityInfoDao().getAll();
            if (activityList.isEmpty()) {
                runOnUiThread(() -> resultText.setText("등록된 활동 정보가 없습니다."));
                return;
            }
            ActivityInfo activity = activityList.get(activityList.size() - 1);

            // 날씨 및 온도 데이터
            WeatherInfo latestWeather = db.weatherInfoDao().getLatest();
            List<houlyTemp> temps = db.houlyTempDao().getTempsForWeather(latestWeather.id);
            if (temps.isEmpty()) {
                runOnUiThread(() -> resultText.setText("날씨 데이터가 없습니다."));
                return;
            }

            // 평균 기온 계산
            int meanTemp = calculateMeanTemp(activity.startTime, activity.endTime, temps);

            // 필요한 warmthLevel 계산
            int requiredWarmth = calculateRequiredWarmth(
                    meanTemp,
                    activity.isOutdoors,
                    activity.isPhysicallyActive,
                    latestWeather.isRaining || latestWeather.isSnowing
            );

            // 옷장 전체에서 추천 옷 필터링
            List<ClothingItem> allItems = db.clothingItemDao().getAll();
            List<ClothingItem> recommended = allItems.stream()
                    .filter(item -> Math.abs(item.warmthLevel - requiredWarmth) <= 1)
                    .collect(Collectors.toList());

            // UI 업데이트는 메인스레드에서!
            runOnUiThread(() -> {
                if (recommended.isEmpty()) {
                    resultText.setText("추천할 옷이 없습니다. 옷장을 등록해주세요.");
                } else {
                    resultText.setText("추천된 옷 (" + recommended.size() + "개)");
                    adapter.setItems(recommended);
                }
            });
        });
        findViewById(R.id.buttonBack).setOnClickListener(v -> {
            finish(); // 현재 액티비티 종료 → MainActivity로 돌아감
        });

    }

    // 시간 범위 내 평균 기온 계산
    private int calculateMeanTemp(long startTime, long endTime, List<houlyTemp> temps) {
        int sum = 0;
        int count = 0;
        for (houlyTemp t : temps) {
            if (t.timestamp >= startTime && t.timestamp <= endTime) {
                sum += t.temp;
                count++;
            }
        }
        return (count > 0) ? Math.round((float) sum / count) : -1;
    }

    // 평균 기온 및 활동/날씨 조건으로 필요한 warmthLevel 계산
    private int calculateRequiredWarmth(int meanTemp, boolean isOutdoors, boolean isPhysicallyActive, boolean isBadWeather) {
        int base = 5;
        if (meanTemp <= 0) base = 9;
        else if (meanTemp <= 5) base = 8;
        else if (meanTemp <= 10) base = 7;
        else if (meanTemp <= 15) base = 6;
        else if (meanTemp <= 20) base = 5;
        else if (meanTemp <= 25) base = 4;
        else base = 3;

        if (isOutdoors) base += 1;
        if (!isPhysicallyActive) base += 1;
        if (isBadWeather) base += 1;

        return Math.min(base, 10);
    }
}
