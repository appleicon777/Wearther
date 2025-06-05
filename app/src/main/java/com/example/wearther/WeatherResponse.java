package com.example.wearther;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("response")
    public Response response;

    public static class Response {
        @SerializedName("body")
        public Body body;
    }

    public static class Body {
        @SerializedName("items")
        public Items items;
    }

    public static class Items {
        @SerializedName("item")
        public List<ForecastItem> item;
    }

    public static class ForecastItem {
        @SerializedName("category")
        public String category;

        @SerializedName("fcstTime")
        public String fcstTime;

        @SerializedName("fcstDate")
        public String fcstDate;

        @SerializedName("fcstValue")
        public String fcstValue;
    }
}
