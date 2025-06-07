package com.example.wearther;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClothingRecommender {

    // 기존 메서드 (보정값 없이)
    public static List<ClothingItem> recommend(List<ClothingItem> allClothes, WeatherInfo weather, int startHour, int endHour) {
        return recommend(allClothes, weather, startHour, endHour, 0);
    }

    // 새로운 메서드: 사용자 피드백 보정값을 반영 (피드백은 이번 추천에만 적용)
    public static List<ClothingItem> recommend(List<ClothingItem> allClothes, WeatherInfo weather, int startHour, int endHour, int userFeedbackAdjustment) {
        List<ClothingItem> recommended = new ArrayList<>();

        double averageTemp = weather.temp;
        int requiredWarmth = calculateRequiredWarmth(averageTemp, weather.windSpeed, weather.isRaining, weather.isSnowing);
        requiredWarmth += userFeedbackAdjustment;
        if (requiredWarmth < 1) requiredWarmth = 1;
        if (requiredWarmth > 7) requiredWarmth = 7;

        List<ClothingItem> candidateTops = new ArrayList<>();
        List<ClothingItem> candidateBottoms = new ArrayList<>();

        for (ClothingItem item : allClothes) {
            if (item.warmthLevel >= requiredWarmth && item.warmthLevel <= requiredWarmth + 2 && item.category != null) {
                if ("상의".equals(item.category)) {
                    candidateTops.add(item);
                } else if ("하의".equals(item.category)) {
                    candidateBottoms.add(item);
                }
            }
        }

        if (!candidateTops.isEmpty()) {
            Collections.shuffle(candidateTops);
            recommended.add(candidateTops.get(0));
        }
        if (!candidateBottoms.isEmpty()) {
            Collections.shuffle(candidateBottoms);
            recommended.add(candidateBottoms.get(0));
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

        if (isRaining || isSnowing) {
            level += 1;
        }
        if (wind > 5.0) {
            level += 1;
        }
        if (level > 7) level = 7;
        return level;
    }
}
