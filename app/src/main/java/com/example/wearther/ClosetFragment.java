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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import android.app.AlertDialog;

public class ClosetFragment extends Fragment {
    private RecyclerView recyclerView;
    private ClothingAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    // 추가: 옷 삭제 버튼
    private View buttonDeleteClothing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closet, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ClothingAdapter();
        recyclerView.setAdapter(adapter);

        // 옷 등록 버튼은 기존 코드와 동일함
        view.findViewById(R.id.buttonAddClothing).setOnClickListener(v -> {
            if(getContext()!=null){
                new AlertDialog.Builder(getContext())
                    .setTitle("옷 등록 방법 선택")
                    .setItems(new CharSequence[]{"사진 촬영", "갤러리에서 선택"}, (dialog, which) -> {
                        Intent intent = new Intent(getContext(), ClothingRegisterActivity.class);
                        if (which == 0) {  // 사진 촬영
                            intent.putExtra("mode", "camera");
                        } else {           // 갤러리에서 선택
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

        // 옷 삭제 버튼 클릭 리스너 추가
        buttonDeleteClothing = view.findViewById(R.id.buttonDeleteClothing);
        buttonDeleteClothing.setOnClickListener(v -> {
            if (!adapter.isSelectionMode()) {
                // 선택 모드 활성화
                adapter.setSelectionMode(true);
                Toast.makeText(getContext(), "삭제할 옷을 선택한 후 다시 '옷 삭제' 버튼을 누르세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 이미 선택 모드인 경우, 선택된 옷 삭제 진행
                ArrayList<ClothingItem> selected = new ArrayList<>(adapter.getSelectedItems());
                if (selected.isEmpty()) {
                    // 선택이 없으면 선택 모드 해제
                    adapter.setSelectionMode(false);
                    Toast.makeText(getContext(), "선택된 옷이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 삭제 확인 다이얼로그
                    new AlertDialog.Builder(getContext())
                        .setTitle("삭제 확인")
                        .setMessage("선택한 옷을 삭제할까요?")
                        .setPositiveButton("삭제", (dialog, which) -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            for (ClothingItem item : selected) {
                                if (item.documentId != null) {
                                    db.collection("clothingItems").document(item.documentId)
                                      .delete();
                                }
                            }
                            Toast.makeText(getContext(), "선택한 옷이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            // 삭제 후 새로고침
                            refreshClothingData();
                        })
                        .setNegativeButton("취소", (dialog, which) -> {
                            // 선택 모드 해제
                            adapter.setSelectionMode(false);
                        })
                        .show();
                }
            }
        });

        // 초기 데이터 로드
        refreshClothingData();

        // 새로고침 리스너
        swipeRefreshLayout.setOnRefreshListener(() -> refreshClothingData());

        return view;
    }

    private void refreshClothingData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // [수정] 현재 로그인 사용자의 UID로 필터링
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
                  Long warmth = doc.getLong("warmthLevel");
                  item.warmthLevel = warmth != null ? warmth.intValue() : 0;
                  item.category = doc.getString("category");
                  // Firestore 문서 id 저장 (삭제 등 처리 시 필요)
                  item.documentId = doc.getId();
                  items.add(item);
              }
              adapter.setItems(items);
              if (items.isEmpty()) {
                  Toast.makeText(getContext(), "등록된 옷이 없습니다.", Toast.LENGTH_SHORT).show();
              }
              swipeRefreshLayout.setRefreshing(false);
          })
          .addOnFailureListener(e -> {
              Toast.makeText(getContext(), "옷 목록 불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
              swipeRefreshLayout.setRefreshing(false);
          });
    }
}
