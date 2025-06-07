package com.example.wearther;

import java.util.ArrayList;
import java.util.List;

public class ClothingRecommender {

    // 전체 옷 리스트, 날씨 정보, 활동 시작/끝 시간 입력
    // 추천 결과: 상의 1벌과 하의 1벌 (해당 조건이 없으면 해당 카테고리 항목은 추천하지 않음)
    public static List<ClothingItem> recommend(List<ClothingItem> allClothes, WeatherInfo weather, int startHour, int endHour) {
        List<ClothingItem> recommended = new ArrayList<>();

        // 평균 기온과 기타 조건에 따른 필요 warmthLevel 계산
        double averageTemp = weather.temp;
        int requiredWarmth = calculateRequiredWarmth(averageTemp, weather.windSpeed, weather.isRaining, weather.isSnowing);

        ClothingItem recommendedTop = null;
        ClothingItem recommendedBottom = null;

        // 충분히 따뜻함 조건에 맞고, 카테고리를 고려하여 첫 상의와 첫 하의를 선택
        for (ClothingItem item : allClothes) {
            if (item.warmthLevel >= requiredWarmth && item.warmthLevel <= requiredWarmth + 2 && item.category != null) {
                if ("상의".equals(item.category) && recommendedTop == null) {
                    recommendedTop = item;
                } else if ("하의".equals(item.category) && recommendedBottom == null) {
                    recommendedBottom = item;
                }
            }
        }

        if (recommendedTop != null) {
            recommended.add(recommendedTop);
        }
        if (recommendedBottom != null) {
            recommended.add(recommendedBottom);
        }

        return recommended;
    }

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
