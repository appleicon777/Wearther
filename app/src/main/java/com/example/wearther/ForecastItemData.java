package com.example.wearther;

public class ForecastItemData {
    public String time;
    public String temperature = "";
    public String sky = "";
    public String pty = "";

    public ForecastItemData(String time) {
        this.time = time;
    }
}
