package com.example.wearther;

import java.util.List;

public class RecommendUtils {

    public static int adjustWarmthLevelByFeedback(AppDatabase db, String conditionHash, int originalWarmthLevel) {
        List<Feedback> recent = db.feedbackDao().getRecentFeedbacks(conditionHash, 5);
        int tooCold = 0, tooHot = 0;

        for (Feedback f : recent) {
            if ("too_cold".equals(f.result)) tooCold++;
            else if ("too_hot".equals(f.result)) tooHot++;
        }

        if (tooCold >= 3) {
            return originalWarmthLevel + 1;
        } else if (tooHot >= 3) {
            return originalWarmthLevel - 1;
        } else {
            return originalWarmthLevel;
        }
    }
}
