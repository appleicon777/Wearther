package com.example.wearther;

import android.util.Log;

import java.util.List;

public class tempMean {

    public static int getMeanTemp(List<WeatherResponse.ForecastItem> items, int startHour, int endHour) {
        int sum = 0;
        int count = 0;

        for (WeatherResponse.ForecastItem item : items) {
            if (!"TMP".equals(item.category)) continue;

            try {
                int hour = Integer.parseInt(item.fcstTime.substring(0, 2));
                if (hour >= startHour && hour <= endHour) {
                    sum += Integer.parseInt(item.fcstValue);
                    count++;
                }
            } catch (Exception e) {
                Log.e("tempMean", "시간 혹은 기온 파싱 실패: " + e.getMessage());
            }
        }

        return count > 0 ? Math.round((float) sum / count) : -1;
    }

    public static boolean isRainExpected(List<WeatherResponse.ForecastItem> items) {
        for (WeatherResponse.ForecastItem item : items) {
            if ("PTY".equals(item.category) && !"0".equals(item.fcstValue)) {
                return true;
            }
        }
        return false;
    }
}
