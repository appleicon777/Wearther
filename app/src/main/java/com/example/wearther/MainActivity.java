package com.example.wearther;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.slider.RangeSlider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WeatherTest";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView textViewResult;
    private TextView timeRangeText;
    private RangeSlider timeRangeSlider;
    private LocationManager locationManager;
    private List<WeatherResponse.ForecastItem> forecastItems;


    private final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private final String SERVICE_KEY = "akcVCcpt2hMlytifqM9VPdV0gTs08X0nS5j09JImhRmA8pvGjNExBs80aLUvJ26uk7n0XUVXltc52mlfgmONQw==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewResult = findViewById(R.id.textViewResult);
        timeRangeText = findViewById(R.id.textViewTimeRange);
        timeRangeSlider = findViewById(R.id.timeRangeSlider);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        timeRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            int startHour = Math.round(values.get(0));
            int endHour = Math.round(values.get(1));
            timeRangeText.setText("활동 시간: " + startHour + "시 ~ " + endHour + "시");

            if (forecastItems != null) {
                int meanTemp = tempMean.getMeanTemp(forecastItems, startHour, endHour);
                String current = textViewResult.getText().toString();
                String updated = current.replaceAll("예보 평균 기온: .*?℃", "예보 평균 기온: " + meanTemp + "℃");
                textViewResult.setText(updated);
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            } else {
                textViewResult.setText("위치 권한이 필요합니다.");
            }
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = 37.3789; //location.getLatitude();
                double longitude = 127.1159; //location.getLongitude();
                Log.d(TAG, "받아온 위경도: lat = " + latitude + ", lon = " + longitude);
                int[] grid = convertGPS(latitude, longitude);
                Log.d(TAG, "변환된 좌표: nx = " + grid[0] + ", ny = " + grid[1]);
                fetchWeather(grid[0], grid[1]);
                locationManager.removeUpdates(this);
            }
        });
    }

    private void fetchWeather(int nx, int ny) {
        String baseDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String baseTime = getBaseTime();
        List<Float> sliderValues = timeRangeSlider.getValues();
        int startHour = Math.round(sliderValues.get(0));
        int endHour = Math.round(sliderValues.get(1));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherResponse> call = service.getForecast(
                SERVICE_KEY,
                1,
                1000,
                "JSON",
                baseDate,
                baseTime,
                nx,
                ny
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    if (weather.response == null || weather.response.body == null || weather.response.body.items == null) {
                        textViewResult.setText("예보 데이터가 없습니다.");
                        return;
                    }

                    List<WeatherResponse.ForecastItem> items = weather.response.body.items.item;
                    forecastItems = items;
                    int meanTemp = tempMean.getMeanTemp(items, startHour, endHour);
                    String nowDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                    String nowTime = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
                    String closestTime = getClosestForecastTime(nowTime);
                    List<Float> sliderValues = timeRangeSlider.getValues();
                    int startHour = Math.round(sliderValues.get(0));
                    int endHour = Math.round(sliderValues.get(1));

                    // 평균 기온 계산
                    int meantemp = tempMean.getMeanTemp(items, startHour, endHour);

                    String condition = "맑음";
                    String rainStart = null;
                    String rainEnd = null;
                    String currentTime = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());

                    for (WeatherResponse.ForecastItem item : items) {
                        if ("PTY".equals(item.category) && nowDate.equals(item.fcstDate)) {
                            String pty = item.fcstValue;
                            if (pty.equals("1") || pty.equals("2") || pty.equals("3") || pty.equals("4")) {
                                if (item.fcstTime.compareTo(currentTime) <= 0) {
                                    // 비가 이미 오고 있는 경우
                                    condition = pty.equals("3") || pty.equals("4") ? "눈" : "비";
                                } else {
                                    // 비가 올 예정
                                    if (rainStart == null) {
                                        rainStart = item.fcstTime.substring(0, 2); // 시작 시각
                                    }
                                    rainEnd = item.fcstTime.substring(0, 2); // 마지막 시각 (계속 갱신)
                                }
                            }
                        }
                    }

                    String weatherString = "날씨: " + condition;
                    if ((condition.equals("맑음") || condition.equals("비") || condition.equals("눈"))
                            && rainStart != null && rainEnd != null) {

                        String phenomenon = "비";
                        for (WeatherResponse.ForecastItem item : items) {
                            if ("PTY".equals(item.category) && nowDate.equals(item.fcstDate)) {
                                if (item.fcstTime.substring(0, 2).equals(rainStart)) {
                                    if (item.fcstValue.equals("3") || item.fcstValue.equals("4")) {
                                        phenomenon = "눈";
                                    }
                                    break;
                                }
                            }
                        }
                        weatherString += " (" + phenomenon + ": " + rainStart + "시~" + rainEnd + "시)";
                    }


                    WeatherResponse.ForecastItem matchedTempItem = null;

                    for (WeatherResponse.ForecastItem item : items) {
                        if ("TMP".equals(item.category) && nowDate.equals(item.fcstDate)) {
                            matchedTempItem = item;
                            break; // 가장 빠른 TMP 예보 하나만 사용
                        }
                    }

                    if (matchedTempItem != null) {
                        String msg = "예보 기온: " + matchedTempItem.fcstValue + "℃ (" + matchedTempItem.fcstTime + ")\n"
                                + "예보 평균 기온: " + meanTemp + "℃ (" + startHour + "시 ~ " + endHour + "시)\n"
                                + weatherString;
                        textViewResult.setText(msg);
                        Log.d(TAG, msg); // 디버깅용 로그
                    } else {
                        textViewResult.setText("기온 정보 없음");
                    }

                } else {
                    textViewResult.setText("서버 오류");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewResult.setText("통신 오류: " + t.getMessage());
                Log.e(TAG, "오류 발생", t);
            }
        });
    }

    private String getBaseTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        int baseHour;

        if (minute < 45) {
            hour -= 1;
            if (hour < 0) hour = 23;
        }

        if (hour >= 23) baseHour = 23;
        else if (hour >= 20) baseHour = 20;
        else if (hour >= 17) baseHour = 17;
        else if (hour >= 14) baseHour = 14;
        else if (hour >= 11) baseHour = 11;
        else if (hour >= 8) baseHour = 8;
        else if (hour >= 5) baseHour = 5;
        else if (hour >= 2) baseHour = 2;
        else baseHour = 23;

        return String.format("%02d00", baseHour);
    }

    private String getClosestForecastTime(String nowTime) {
        int hour = Integer.parseInt(nowTime.substring(0, 2));
        return String.format(Locale.getDefault(), "%02d00", (hour / 3) * 3);
    }

    private int[] convertGPS(double lat, double lon) {
        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;
        double XO = 43;
        double YO = 136;

        double DEGRAD = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) /
                Math.log(Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5));
        double sf = Math.pow(Math.tan(Math.PI * 0.25 + slat1 * 0.5), sn) * Math.cos(slat1) / sn;
        double ro = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + olat * 0.5), sn);

        double ra = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5), sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) (ra * Math.sin(theta) + XO + 0.5);
        int y = (int) (ro - ra * Math.cos(theta) + YO + 0.5);
        return new int[]{x, y};
    }
}
