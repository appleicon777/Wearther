package com.example.wearther;

import java.util.List;

public class TemperatureUtil {
    public static int[] getMaxMinTemp(List<WeatherResponse.ForecastItem> items, String targetDate) {
        if (items == null || items.isEmpty()) return new int[]{0, 0}; // 안전 처리

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (WeatherResponse.ForecastItem item : items) {
            if (!"TMP".equals(item.category)) continue;
            if (!item.fcstDate.equals(targetDate)) continue;

            try {
                int temp = Integer.parseInt(item.fcstValue);
                max = Math.max(max, temp);
                min = Math.min(min, temp);
            } catch (NumberFormatException ignored) {}
        }

        return new int[]{max, min};
    }
}

