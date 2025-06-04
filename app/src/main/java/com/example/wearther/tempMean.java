package com.example.wearther;

import com.example.wearther.WeatherResponse;
import java.util.List;

public class tempMean {

    public static int getMeanTemp(List<WeatherResponse.ForecastItem> items, int startHour, int endHour) {
        // 기존 오늘 날짜용 구현
        return startHour;
    }

    public static int getMeanTempByDate(List<WeatherResponse.ForecastItem> items, String date, int startHour, int endHour) {
        if (items == null || items.isEmpty()) return 5;

        int sum = 0, count = 0;
        for (WeatherResponse.ForecastItem item : items) {
            if (!"TMP".equals(item.category)) continue;
            if (!item.fcstDate.equals(date)) continue;

            try {
                int hour = Integer.parseInt(item.fcstTime.substring(0, 2));
                if (hour >= startHour && hour <= endHour) {
                    sum += Integer.parseInt(item.fcstValue);
                    count++;
                }
            } catch (NumberFormatException e) {
                // 무시
            }
        }
        return count > 0 ? Math.round((float) sum / count) : 5;
    }
}
