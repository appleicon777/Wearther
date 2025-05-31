package com.example.wearther;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    private TextView textViewRecommendation;
    private RecyclerView recyclerView;
    private ClothingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        textViewRecommendation = findViewById(R.id.textViewRecommendation);
        recyclerView = findViewById(R.id.recyclerView);

        // RecyclerView 구성
        adapter = new ClothingAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // MainActivity로부터 전달받은 날씨 및 활동 정보
        int meanTemp = getIntent().getIntExtra("meanTemp", 15);
        int startHour = getIntent().getIntExtra("startHour", 9);
        int endHour = getIntent().getIntExtra("endHour", 18);
        boolean isRaining = getIntent().getBooleanExtra("isRaining", false);
        boolean isSnowing = getIntent().getBooleanExtra("isSnowing", false);
        boolean isOutdoor = getIntent().getBooleanExtra("isOutdoor", true);

        // DB 연결
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "wearther-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        List<ClothingItem> clothes = db.clothingItemDao().getAll();

        // 활동 및 날씨에 따른 목표 warmthLevel 설정
        int targetWarmth = isOutdoor ?
                (meanTemp <= 0 ? 9 : meanTemp <= 10 ? 7 : meanTemp <= 17 ? 5 : meanTemp <= 23 ? 3 : 1)
                : (meanTemp <= 0 ? 7 : meanTemp <= 10 ? 5 : meanTemp <= 17 ? 3 : meanTemp <= 23 ? 2 : 0);

        // 추천 리스트 생성
        List<ClothingItem> recommended = new ArrayList<>();
        List<Integer> recommendedIds = new ArrayList<>();

        for (ClothingItem item : clothes) {
            // AI 기반 등록 대비 방어 코드
            if (item.warmthLevel <= 0) continue;
            if (item.name == null || item.name.trim().isEmpty()) continue;

            boolean suitable = false;

            if ((isRaining && item.isWaterproof) || (isSnowing && item.isThick)) {
                suitable = true;
            } else if (Math.abs(item.warmthLevel - targetWarmth) <= 2) {
                suitable = true;
            }

            if (suitable) {
                recommended.add(item);
                recommendedIds.add(item.id);
            }
        }

        // 결과 출력 및 로그 기록
        if (recommended.isEmpty()) {
            textViewRecommendation.setText("적절한 옷이 없습니다. 옷장을 업데이트 해주세요.");
        } else {
            textViewRecommendation.setText("추천된 옷 리스트:");
            adapter.setItems(recommended);

            // 추천 로그 저장
            RecommendationLog log = new RecommendationLog();
            log.timestamp = System.currentTimeMillis();
            log.recommendedItemIds = joinIds(recommendedIds);
            log.feedbackId = null;
            db.recommendationLogDao().insert(log);
        }
    }

    private String joinIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1) sb.append(",");
        }
        return sb.toString();
    }
}
