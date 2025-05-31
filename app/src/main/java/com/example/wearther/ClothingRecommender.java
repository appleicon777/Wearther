package com.example.wearther;

import java.util.ArrayList;
import java.util.List;

public class ClothingRecommender {

    // 추천 메서드: 전체 옷 리스트, 날씨, 활동 시작/끝 시간 입력
    public static List<ClothingItem> recommend(List<ClothingItem> allClothes, WeatherInfo weather, int startHour, int endHour) {
        List<ClothingItem> recommended = new ArrayList<>();

        // 🌡️ 평균 기온 기준으로 필요한 warmthLevel 결정
        double averageTemp = weather.temp;
        int requiredWarmth = calculateRequiredWarmth(averageTemp, weather.windSpeed, weather.isRaining, weather.isSnowing);

        // 옷 추천 기준: warmthLevel이 비슷하거나 약간 높은 옷들 선택
        for (ClothingItem item : allClothes) {
            if (item.warmthLevel >= requiredWarmth && item.warmthLevel <= requiredWarmth + 2) {
                recommended.add(item);
            }
        }

        return recommended;
    }

    // 날씨 조건에 따른 warmthLevel 계산 로직
    private static int calculateRequiredWarmth(double temp, double wind, boolean isRaining, boolean isSnowing) {
        int level;

        if (temp >= 25) {
            level = 1;  // 매우 더움
        } else if (temp >= 20) {
            level = 2;
        } else if (temp >= 15) {
            level = 3;
        } else if (temp >= 10) {
            level = 4;
        } else if (temp >= 5) {
            level = 5;
        } else {
            level = 6;  // 매우 추움
        }

        //  비/눈이 오면 1단계 더 따뜻하게
        if (isRaining || isSnowing) {
            level += 1;
        }

        // 바람이 많이 불면 1단계 더 따뜻하게
        if (wind > 5.0) {
            level += 1;
        }

        // 최대치 제한
        if (level > 7) level = 7;
        return level;
    }
}
