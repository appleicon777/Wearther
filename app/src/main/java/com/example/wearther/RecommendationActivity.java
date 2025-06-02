package com.example.wearther;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import java.util.ArrayList;
import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    private TextView recommendationText;
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        recommendationText = findViewById(R.id.recommendationText);

        // Intent 값 받기
        int startHour = getIntent().getIntExtra("startHour", 9);
        int endHour = getIntent().getIntExtra("endHour", 18);
        boolean isOutdoor = getIntent().getBooleanExtra("isOutdoor", false);
        boolean isRaining = getIntent().getBooleanExtra("isRaining", false);
        boolean isSnowing = getIntent().getBooleanExtra("isSnowing", false);
        int meanTemp = getIntent().getIntExtra("meanTemp", 15);

        // 필요한 warmthLevel 계산
        int targetWarmth = calculateTargetWarmth(meanTemp, startHour, endHour, isOutdoor);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").allowMainThreadQueries().build();

        List<ClothingItem> allItems = db.clothingItemDao().getAllItems();
        List<ClothingItem> filtered = new ArrayList<>();

        for (ClothingItem item : allItems) {
            if ((isRaining || isSnowing) && item.material != null && item.material.contains("cotton")) continue;
            if (item.warmthLevel <= targetWarmth) filtered.add(item);
        }

        if (filtered.isEmpty()) {
            recommendationText.setText("추천 가능한 옷이 없습니다 \uD83D\uDE25");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\uD83D\uDCA1 추천 옷 리스트 (필요 warmth: ").append(targetWarmth).append(")\n\n");
            for (ClothingItem item : filtered) {
                sb.append("- ").append(item.name)
                        .append(" (warmth: ").append(item.warmthLevel).append(")\n");
            }
            recommendationText.setText(sb.toString());
        }
    }

    private int calculateTargetWarmth(int temp, int startHour, int endHour, boolean isOutdoor) {
        int warmth = 0;
        if (temp <= 0) warmth = 9;
        else if (temp <= 5) warmth = 8;
        else if (temp <= 10) warmth = 7;
        else if (temp <= 15) warmth = 5;
        else if (temp <= 20) warmth = 3;
        else warmth = 1;

        if (isOutdoor) warmth += 1;
        if (endHour - startHour > 6) warmth += 1;

        return Math.min(warmth, 10);
    }
}
