package com.example.wearther;

import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "response", strict = false)
public class WeatherResponse {
    @SerializedName("response")
    public ResponseData response;

    public class ResponseData {
        @SerializedName("body")
        public Body body;
    }

    public class Body {
        @SerializedName("items")
        public Items items;
    }

    public class Items {
        @SerializedName("item")
        public List<ForecastItem> item;
    }

    public class ForecastItem {
        @SerializedName("category")
        public String category;

        @SerializedName("fcstDate")
        public String fcstDate;

        @SerializedName("fcstTime")
        public String fcstTime;

        @SerializedName("fcstValue")
        public String fcstValue;
    }
}