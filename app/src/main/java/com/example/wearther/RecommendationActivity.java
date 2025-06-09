package com.example.wearther;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 원래 전달받은 추천 결과 표시
        List<ClothingItem> recommendedItems = getIntent().getParcelableArrayListExtra("recommendedItems");
        int minTemp = getIntent().getIntExtra("minTemp", 0);
        int maxTemp = getIntent().getIntExtra("maxTemp", 0);
        String weather = getIntent().getStringExtra("weather");
        String activityTime = getIntent().getStringExtra("activityTime");
        conditionKey = minTemp + ":" + maxTemp + ":" + weather + ":" + activityTime;

        if (recommendedItems != null && !recommendedItems.isEmpty()) {
            recommendationText.setText("Wearther Recommendation");
            recommendationAdapter = new RecommendationAdapter(recommendedItems);
            recommendationRecyclerView.setAdapter(recommendationAdapter);
        } else {
            recommendationText.setText("추천 가능한 옷이 없습니다.");
        }

        Button buttonTooCold = findViewById(R.id.buttonTooCold);
        Button buttonJustRight = findViewById(R.id.buttonJustRight);
        Button buttonTooHot = findViewById(R.id.buttonTooHot);

        buttonTooCold.setText("Warmer");
        buttonJustRight.setText("Perfect");
        buttonTooHot.setText("Cooler");

        buttonTooCold.setOnClickListener(v -> {
            saveFeedback("too_cold");
            reloadRecommendation(1); // warmth 보정 +1
        });

        buttonJustRight.setOnClickListener(v -> {
            reloadRecommendation(0);
        });

        buttonTooHot.setOnClickListener(v -> {
            saveFeedback("too_hot");
            reloadRecommendation(-1); // warmth 보정 -1
        });
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

    // 새 추천을 위해 Firestore에서 옷 데이터를 다시 불러와 ClothingRecommender에 보정값을 반영
    private void reloadRecommendation(int feedbackAdjustment) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();  // 현재 userId 획득
        db.collection("clothingItems")
          .whereEqualTo("userId", currentUserId)  // userId 필터 추가
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              ArrayList<ClothingItem> allClothes = new ArrayList<>();
              for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                  ClothingItem item = new ClothingItem();
                  item.name = doc.getString("label");
                  item.imageUri = doc.getString("imageUrl");
                  Long warmth = doc.getLong("warmthLevel");
                  item.warmthLevel = warmth != null ? warmth.intValue() : 0;
                  item.category = doc.getString("category");
                  allClothes.add(item);
              }
              // 재추천용 WeatherInfo 생성 (여기서는 간단히 minTemp를 온도로 사용)
              WeatherInfo weatherInfo = new WeatherInfo();
              weatherInfo.temp = getIntent().getIntExtra("minTemp", 0);
              weatherInfo.windSpeed = 0;
              String weatherStr = getIntent().getStringExtra("weather");
              weatherInfo.isRaining = "비".equals(weatherStr);
              weatherInfo.isSnowing = "눈".equals(weatherStr);
              // activityTime은 "start시 ~ end시" 형식이므로 여기서는 기본값 사용
              int startHour = 9;
              int endHour = 18;
  
              List<ClothingItem> newRecommended = ClothingRecommender.recommend(allClothes, weatherInfo, startHour, endHour, feedbackAdjustment);
  
              runOnUiThread(() -> {
                  if (newRecommended != null && !newRecommended.isEmpty()) {
                      recommendationText.setText("추천된 옷 조합입니다:");
                      recommendationAdapter = new RecommendationAdapter(newRecommended);
                      recommendationRecyclerView.setAdapter(recommendationAdapter);
                  } else {
                      recommendationText.setText("추천 가능한 옷이 없습니다.");
                  }
              });
          })
          .addOnFailureListener(e ->
              runOnUiThread(() ->
                      Toast.makeText(RecommendationActivity.this, "재추천 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show())
          );
    }
}
