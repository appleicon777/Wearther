package com.example.wearther;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.Executors;

public class ClosetActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ClothingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation); // 재사용

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClothingAdapter();
        recyclerView.setAdapter(adapter);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<ClothingItem> allItems = db.clothingItemDao().getAll();
            runOnUiThread(() -> {
                if (allItems.isEmpty()) {
                    Toast.makeText(this, "등록된 옷이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                adapter.setItems(allItems);
            });
        });

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        ((android.widget.TextView)findViewById(R.id.resultText)).setText("내 옷장");
    }
}