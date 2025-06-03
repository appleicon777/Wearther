package com.example.wearther;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                ClothingItem.class,
                ActivityInfo.class,
                Feedback.class,
                houlyTemp.class,
                RecommendationLog.class,
                User.class,
                UserSetting.class,
                WeatherInfo.class
        },
        version = 2
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract UserSettingDao userSettingsDao();
    public abstract ClothingItemDao clothingItemDao();
    public abstract FeedbackDao feedbackDao();
    public abstract houlyTempDao houlyTempDao();
    public abstract RecommendationLogDao recommendationLogDao();
    public abstract WeatherInfoDao weatherInfoDao();
    public abstract ActivityInfoDao activityInfoDao();

    // tempMean이 Entity가 아니라 계산 전용 클래스라면 Dao는 존재하지 않음
    // 만약 tempMean이 DB에 저장되거나 Dao를 통해 조작된다면 아래 줄을 유지
    // public abstract tempMeanDao tempMeanDao(); ← 필요 없다면 제

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "wearther-db"
            ).fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
