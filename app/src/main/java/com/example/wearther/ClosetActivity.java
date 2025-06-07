package com.example.wearther;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ClosetActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ClothingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClothingAdapter();
        recyclerView.setAdapter(adapter);

        // [수정] 현재 로그인 사용자의 UID로 필터링하여 옷 목록 불러오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("clothingItems")
          .whereEqualTo("userId", currentUserId)
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              ArrayList<ClothingItem> items = new ArrayList<>();
              for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                  ClothingItem item = new ClothingItem();
                  item.name = doc.getString("label");
                  item.imageUri = doc.getString("imageUrl");
                  // [수정] warmthLevel, category 등 추가 시 동일하게 처리
                  items.add(item);
              }
              adapter.setItems(items);
              if (items.isEmpty()) {
                  Toast.makeText(this, "등록된 옷이 없습니다.", Toast.LENGTH_SHORT).show();
              }
          })
          .addOnFailureListener(e -> {
              Toast.makeText(this, "옷 목록 불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
          });

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        ((android.widget.TextView)findViewById(R.id.resultText)).setText("내 옷장");
    }
}