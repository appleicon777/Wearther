package com.example.wearther;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Feedback {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String result;         // "too_cold", "just_right", "too_hot"
    public long date;             // 저장 시간
    public boolean adjusted;      // 보정 여부
    public String conditionKey;   // 조건 기반 추천 키 (예: "15:25:맑음:오후")
}
