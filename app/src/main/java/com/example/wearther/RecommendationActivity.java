package com.example.wearther;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    private TextView textViewRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        textViewRecommendation = findViewById(R.id.textViewRecommendation);

        int meanTemp = getIntent().getIntExtra("meanTemp", 15);
        int startHour = getIntent().getIntExtra("startHour", 9);
        int endHour = getIntent().getIntExtra("endHour", 18);
        boolean isRaining = getIntent().getBooleanExtra("isRaining", false);
        boolean isSnowing = getIntent().getBooleanExtra("isSnowing", false);
        boolean isOutdoor = getIntent().getBooleanExtra("isOutdoor", true);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "wearther-db").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        List<ClothingItem> clothes = db.clothingItemDao().getAll();

        // 옷 추천 알고리즘
        int targetWarmth = isOutdoor ?
                (meanTemp <= 0 ? 9 : meanTemp <= 10 ? 7 : meanTemp <= 17 ? 5 : meanTemp <= 23 ? 3 : 1)
                : (meanTemp <= 0 ? 7 : meanTemp <= 10 ? 5 : meanTemp <= 17 ? 3 : meanTemp <= 23 ? 2 : 0);

        StringBuilder result = new StringBuilder("추천 옷:\n");

        for (ClothingItem item : clothes) {
            if ((isRaining && item.isWaterproof) || (isSnowing && item.isThick)) {
                result.append("☔/❄ ").append(item.name).append("\n");
            } else if (Math.abs(item.warmthLevel - targetWarmth) <= 2) {
                result.append("✓ ").append(item.name).append("\n");
            }
        }

        if (result.toString().equals("추천 옷:\n")) {
            result.append("적절한 옷이 없습니다. 옷장을 업데이트 해주세요.");
        }

        textViewRecommendation.setText(result.toString());
    }
}
