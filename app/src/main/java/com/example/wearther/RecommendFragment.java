package com.example.wearther;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wearther.WeatherResponse;
import com.example.wearther.WeatherService;
import com.example.wearther.tempMean;
import com.google.android.material.slider.RangeSlider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecommendFragment extends Fragment {

    private Spinner dateSpinner;
    private TextView timeRangeText;
    private RangeSlider timeRangeSlider;
    private CheckBox checkBoxOutdoor;
    private Button buttonRecommend;
    private List<WeatherResponse.ForecastItem> forecastItems;

    public void setForecastItems(List<WeatherResponse.ForecastItem> items) {
        this.forecastItems = items;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        dateSpinner = view.findViewById(R.id.dateSpinner);
        timeRangeText = view.findViewById(R.id.textViewTimeRange);
        timeRangeSlider = view.findViewById(R.id.timeRangeSlider);
        checkBoxOutdoor = view.findViewById(R.id.checkbox_outdoor);
        buttonRecommend = view.findViewById(R.id.buttonRecommend);

        // 활동시간 슬라이더 변경 시 텍스트 업데이트
        timeRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            int startHour = Math.round(values.get(0));
            int endHour = Math.round(values.get(1));
            timeRangeText.setText("활동 시간: " + startHour + "시 ~ " + endHour + "시");
        });

        buttonRecommend.setOnClickListener(v -> {
            if (forecastItems == null || forecastItems.isEmpty()) {
                Log.e("Recommend", "예보 정보 없음");
                return;
            }

            // 날짜 선택 처리
            String selectedDate = getSelectedDate(dateSpinner.getSelectedItemPosition());

            // 시간 범위 처리
            int startHour = Math.round(timeRangeSlider.getValues().get(0));
            int endHour = Math.round(timeRangeSlider.getValues().get(1));

            // 해당 날짜와 시간에 맞는 평균 기온 계산
            int meanTemp = tempMean.getMeanTempByDate(forecastItems, selectedDate, startHour, endHour);

            // 강수 여부 파악
            boolean isRaining = false, isSnowing = false;
            for (WeatherResponse.ForecastItem item : forecastItems) {
                if (!item.fcstDate.equals(selectedDate)) continue;
                if ("PTY".equals(item.category)) {
                    if ("1".equals(item.fcstValue) || "4".equals(item.fcstValue)) isRaining = true;
                    if ("3".equals(item.fcstValue)) isSnowing = true;
                    if ("2".equals(item.fcstValue)) {
                        int temp = getTempAtTime(selectedDate, item.fcstTime);
                        if (temp < 4) isSnowing = true;
                        else isRaining = true;
                    }
                }
            }

            boolean isOutdoor = checkBoxOutdoor.isChecked();

            // 추천 화면으로 이동
            Intent intent = new Intent(getActivity(), RecommendationActivity.class);
            intent.putExtra("meanTemp", meanTemp);
            intent.putExtra("startHour", startHour);
            intent.putExtra("endHour", endHour);
            intent.putExtra("isRaining", isRaining);
            intent.putExtra("isSnowing", isSnowing);
            intent.putExtra("isOutdoor", isOutdoor);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });

        return view;
    }

    private String getSelectedDate(int position) {
        Calendar calendar = Calendar.getInstance();
        if (position == 1) {  // 내일
            calendar.add(Calendar.DATE, 1);
        }
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());
    }

    private int getTempAtTime(String targetDate, String targetTime) {
        for (WeatherResponse.ForecastItem item : forecastItems) {
            if ("TMP".equals(item.category)
                    && item.fcstDate.equals(targetDate)
                    && item.fcstTime.equals(targetTime)) {
                try {
                    return Integer.parseInt(item.fcstValue);
                } catch (NumberFormatException e) {
                    Log.e("Recommend", "기온 파싱 실패: " + item.fcstValue);
                }
            }
        }
        return 5;
    }
}
