package com.example.wearther;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ClothingRegisterActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imageViewPreview;
    private Uri imageUri;
    private String mode; // "camera" 또는 "gallery"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_register);

        imageViewPreview = findViewById(R.id.imageViewPreview);

        // 다이얼로그에서 mode를 전달받았다면 처리
        mode = getIntent().getStringExtra("mode");
        if ("camera".equals(mode)) {
            openCamera();
        } else if ("gallery".equals(mode)) {
            openGallery();
        }

        // 필요하다면 이후 Vision API 호출, 메타데이터 확인/수정, 저장 버튼 등 추가
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageViewPreview.setImageBitmap(photo);
                VisionApiHelper.extractMetaData(this, photo, metaData -> {
                    runOnUiThread(() -> {
                        if (metaData != null) {
                            showMetaDataEditDialog(metaData.get("label"), photo);
                        } else {
                            Toast.makeText(this, "이미지 분석 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                imageViewPreview.setImageURI(imageUri);

                // 여기서 Bitmap으로 변환 후 Vision API 호출
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    VisionApiHelper.extractMetaData(this, bitmap, metaData -> {
                        runOnUiThread(() -> {
                            if (metaData != null) {
                                showMetaDataEditDialog(metaData.get("label"), bitmap);
                            } else {
                                Toast.makeText(this, "이미지 분석 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "이미지 처리 오류", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 다이얼로그 함수 추가
    private void showMetaDataEditDialog(String label, Bitmap photo) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_metadata, null);
        EditText editTextLabel = dialogView.findViewById(R.id.editTextLabel);
        editTextLabel.setText(label);

        new AlertDialog.Builder(this)
            .setTitle("옷 정보 확인")
            .setView(dialogView)
            .setPositiveButton("저장", (dialog, which) -> {
                String finalLabel = editTextLabel.getText().toString().trim();
                saveClothingToFirebase(finalLabel, photo);
            })
            .setNegativeButton("취소", null)
            .show();
    }

    private void saveClothingToFirebase(String label, Bitmap photo) {
        // 1. 이미지 Firebase Storage에 업로드
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("clothes/" + System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        storageRef.putBytes(data)
            .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // 2. Firestore에 메타데이터 저장
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> clothing = new HashMap<>();
                clothing.put("label", label);
                clothing.put("imageUrl", uri.toString());
                clothing.put("createdAt", System.currentTimeMillis());

                db.collection("clothingItems").add(clothing)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "옷이 등록되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "DB 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }))
            .addOnFailureListener(e -> Toast.makeText(this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}