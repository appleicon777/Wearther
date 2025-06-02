package com.example.wearther;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
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
import com.example.wearther.WeatherResponse;

public class HomeFragment extends Fragment {

    private TextView textViewResult;
    private List<WeatherResponse.ForecastItem> forecastItems;

    private final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private final String SERVICE_KEY = "akcVCcpt2hMlytifqM9VPdV0gTs08X0nS5j09JImhRmA8pvGjNExBs80aLUvJ26uk7n0XUVXltc52mlfgmONQw==";
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textViewResult = view.findViewById(R.id.textViewResult);

        locationManager = (LocationManager) requireActivity().getSystemService(requireContext().LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            requestLocation();
        }

        return view;
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = 37.3789; // location.getLatitude();
                double longitude = 127.1159; // location.getLongitude();
                int[] grid = convertGPS(latitude, longitude);
                fetchWeather(grid[0], grid[1]);
                locationManager.removeUpdates(this);
            }
        });
    }

    private void fetchWeather(int nx, int ny) {
        String baseDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String baseTime = "0500";

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
                SERVICE_KEY, 1, 1000, "JSON", baseDate, baseTime, nx, ny);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    forecastItems = response.body().response.body.items.item;

                    // 날씨 데이터 추출 및 표시
                    String nowDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

                    String temperature = "";
                    String sky = "";
                    String precipitation = "";

                    for (WeatherResponse.ForecastItem item : forecastItems) {
                        if (!nowDate.equals(item.fcstDate)) continue;

                        switch (item.category) {
                            case "TMP":
                                if (temperature.isEmpty()) temperature = item.fcstValue + "℃ (" + item.fcstTime.substring(0, 2) + "시)";
                                break;
                            case "SKY":
                                if (sky.isEmpty()) {
                                    switch (item.fcstValue) {
                                        case "1": sky = "맑음"; break;
                                        case "3": sky = "구름 많음"; break;
                                        case "4": sky = "흐림"; break;
                                    }
                                }
                                break;
                            case "PTY":
                                if (precipitation.isEmpty()) {
                                    switch (item.fcstValue) {
                                        case "0": precipitation = "강수 없음"; break;
                                        case "1": precipitation = "비"; break;
                                        case "2": precipitation = "비/눈"; break;
                                        case "3": precipitation = "눈"; break;
                                        case "4": precipitation = "소나기"; break;
                                    }
                                }
                                break;
                        }

                        if (!temperature.isEmpty() && !sky.isEmpty() && !precipitation.isEmpty()) break;
                    }

                    StringBuilder weatherText = new StringBuilder();
                    weatherText.append("현재 기온: ").append(temperature).append("\n");
                    weatherText.append("하늘 상태: ").append(sky).append("\n");
                    weatherText.append("강수 상태: ").append(precipitation);

                    textViewResult.setText(weatherText.toString());
                } else {
                    textViewResult.setText("서버 오류로 날씨 정보를 불러올 수 없습니다.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewResult.setText("날씨 정보를 불러오지 못했습니다: " + t.getMessage());
            }
        });
    }

    private int[] convertGPS(double lat, double lon) {
        double RE = 6371.00877, GRID = 5.0, SLAT1 = 30.0, SLAT2 = 60.0, OLON = 126.0, OLAT = 38.0, XO = 43, YO = 136;
        double DEGRAD = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD, slat2 = SLAT2 * DEGRAD, olon = OLON * DEGRAD, olat = OLAT * DEGRAD;

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
