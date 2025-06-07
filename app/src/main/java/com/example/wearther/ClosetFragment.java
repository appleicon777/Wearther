package com.example.wearther;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import android.app.AlertDialog;

public class ClosetFragment extends Fragment {
    private RecyclerView recyclerView;
    private ClothingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closet, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClothingAdapter();
        recyclerView.setAdapter(adapter);

        // Firestore에서 옷 목록 불러오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clothingItems")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                ArrayList<ClothingItem> items = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    ClothingItem item = new ClothingItem();
                    item.name = doc.getString("label");
                    item.imageUri = doc.getString("imageUrl");
                    // Firestore에 warmthLevel, category 등이 있다면 아래처럼 추가
                    // item.warmthLevel = doc.getLong("warmthLevel") != null ? doc.getLong("warmthLevel").intValue() : 0;
                    // item.category = doc.getString("category");
                    items.add(item);
                }
                adapter.setItems(items);
                if (items.isEmpty()) {
                    Toast.makeText(getContext(), "등록된 옷이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "옷 목록 불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        // 옷 등록 버튼 클릭 리스너 수정
        view.findViewById(R.id.buttonAddClothing).setOnClickListener(v -> {
            // Context가 null이 아닌지 확인
            if (getContext() != null) {
                new AlertDialog.Builder(getContext())
                    .setTitle("옷 등록 방법 선택")
                    .setItems(new CharSequence[]{"사진 촬영", "갤러리에서 선택"}, (dialog, which) -> {
                        Intent intent = new Intent(getContext(), ClothingRegisterActivity.class);
                        if (which == 0) {
                            // 사진 촬영
                            intent.putExtra("mode", "camera");
                        } else {
                            // 갤러리에서 선택
                            intent.putExtra("mode", "gallery");
                        }
                        startActivity(intent);
                    })
                    .setNegativeButton("취소", null)
                    .show();
            } else {
                Toast.makeText(getActivity(), "Fragment가 Activity에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
