package com.example.wearther;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wearther.ActivityInfo;
import com.example.wearther.ActivityInfoDao;
import com.example.wearther.AppDatabase;
import com.example.wearther.ClothingItem;
import com.example.wearther.ClothingItemDao;
import com.example.wearther.Feedback;
import com.example.wearther.FeedbackDao;
import com.example.wearther.houlyTemp;
import com.example.wearther.houlyTempDao;
import com.example.wearther.RecommendationLog;
import com.example.wearther.RecommendationLogDao;
import com.example.wearther.User;
import com.example.wearther.UserDao;
import com.example.wearther.UserSetting;
import com.example.wearther.UserSettingDao;
import com.example.wearther.WeatherInfo;
import com.example.wearther.WeatherInfoDao;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WeatherTest"; // yuwon branch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDatabase db = AppDatabase.getInstance(this);
        ClothingItemDao dao = db.clothingItemDao();

        ClothingItem hoodie = new ClothingItem();
        hoodie.name = "회색 후드";
        hoodie.category = "middle";
        hoodie.warmthLevel = 3;
        hoodie.isLayerable = true;
        hoodie.colorTag = "gray";
        hoodie.isJean = false;
        hoodie.imageUri = null;

        new Thread(() -> {
            dao.insert(hoodie);

            List<ClothingItem> clothes = dao.getAll();
            for (ClothingItem c : clothes) {
                Log.d(TAG, "옷 이름: " + c.name + ", 카테고리: " + c.category);
            }
        }).start();

    }

}