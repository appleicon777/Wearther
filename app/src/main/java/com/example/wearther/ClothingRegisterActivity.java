package com.example.wearther;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import android.app.AlertDialog;

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

        mode = getIntent().getStringExtra("mode");
        if ("camera".equals(mode)) {
            openCamera();
        } else if ("gallery".equals(mode)) {
            openGallery();
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_IMAGE_PICK);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_PICK);
                return;
            }
        }
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageViewPreview.setImageBitmap(photo);
                // Vision API를 호출하고 결과에 따른 라벨을 받아 다이얼로그 표시
                VisionApiHelper.extractMetaData(this, photo, metaData -> runOnUiThread(() -> {
                    String label = metaData != null ? metaData.get("label") : "";
                    showMetaDataEditDialog(label, photo);
                }));
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                imageUri = data.getData();
                imageViewPreview.setImageURI(imageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    VisionApiHelper.extractMetaData(this, bitmap, metaData -> runOnUiThread(() -> {
                        String label = metaData != null ? metaData.get("label") : "";
                        showMetaDataEditDialog(label, bitmap);
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "이미지 처리 오류", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "이미지 선택이 취소되었거나 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMetaDataEditDialog(String label, Bitmap photo) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_metadata, null);
        EditText editTextLabel = dialogView.findViewById(R.id.editTextLabel);
        editTextLabel.setText(label);

        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.clothing_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        EditText editTextWarmth = dialogView.findViewById(R.id.editTextWarmth);

        new AlertDialog.Builder(this)
            .setTitle("옷 정보 확인")
            .setView(dialogView)
            .setPositiveButton("저장", (dialog, which) -> {
                String finalLabel = editTextLabel.getText().toString().trim();
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                String warmthStr = editTextWarmth.getText().toString().trim();
                int warmthLevel = warmthStr.isEmpty() ? 0 : Integer.parseInt(warmthStr);
                saveClothingToFirebase(finalLabel, selectedCategory, warmthLevel, photo);
            })
            .setNegativeButton("취소", null)
            .show();
    }

    private void saveClothingToFirebase(String label, String category, int warmthLevel, Bitmap photo) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("clothes/" + System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        storageRef.putBytes(data)
            .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> clothing = new HashMap<>();
                clothing.put("label", label);
                clothing.put("imageUrl", uri.toString());
                clothing.put("createdAt", System.currentTimeMillis());
                clothing.put("category", category);         // 상의/하의 정보
                clothing.put("warmthLevel", warmthLevel);     // 따뜻함 레벨

                db.collection("clothingItems").add(clothing)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "옷이 등록되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "DB 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }))
            .addOnFailureListener(e -> Toast.makeText(this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "저장소(사진) 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}