package com.example.wearther;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    private TextView recommendationText;
    private RecyclerView recommendationRecyclerView;
    private RecommendationAdapter recommendationAdapter;
    private String conditionKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        recommendationText = findViewById(R.id.recommendationText);
        recommendationRecyclerView = findViewById(R.id.recommendationRecyclerView);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 레이아웃 매니저 먼저 설정

        // 옷 추천 정보 표시
        List<ClothingItem> recommendedItems = getIntent().getParcelableArrayListExtra("recommendedItems");
        int minTemp = getIntent().getIntExtra("minTemp", 0);
        int maxTemp = getIntent().getIntExtra("maxTemp", 0);
        String weather = getIntent().getStringExtra("weather");
        String activityTime = getIntent().getStringExtra("activityTime");
        conditionKey = minTemp + ":" + maxTemp + ":" + weather + ":" + activityTime;

        if (recommendedItems != null && !recommendedItems.isEmpty()) {
            recommendationText.setText("추천된 옷 조합입니다:");
            recommendationAdapter = new RecommendationAdapter(recommendedItems);
            recommendationRecyclerView.setAdapter(recommendationAdapter); // 어댑터 설정
        } else {
            recommendationText.setText("추천 가능한 옷이 없습니다.");
        }

        // 피드백 버튼 설정
        Button buttonTooCold = findViewById(R.id.buttonTooCold);
        Button buttonJustRight = findViewById(R.id.buttonJustRight);
        Button buttonTooHot = findViewById(R.id.buttonTooHot);

        buttonTooCold.setOnClickListener(v -> saveFeedback("too_cold"));
        buttonJustRight.setOnClickListener(v -> saveFeedback("just_right"));
        buttonTooHot.setOnClickListener(v -> saveFeedback("too_hot"));
    }

    private void saveFeedback(String result) {
        new Thread(() -> {
            Feedback feedback = new Feedback();
            feedback.result = result;
            feedback.date = System.currentTimeMillis();
            feedback.adjusted = false;
            feedback.conditionKey = conditionKey;

            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            db.feedbackDao().insert(feedback);

            runOnUiThread(() ->
                    Toast.makeText(RecommendationActivity.this, "피드백이 저장되었습니다.", Toast.LENGTH_SHORT).show());
        }).start();
    }
}
