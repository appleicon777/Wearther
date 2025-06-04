// AppDatabase.java
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
