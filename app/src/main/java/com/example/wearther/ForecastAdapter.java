package com.example.wearther;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<ForecastItemData> items;

    public ForecastAdapter(List<ForecastItemData> items) {
        this.items = items;
    }

    public void setItems(List<ForecastItemData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        ForecastItemData item = items.get(position);
        holder.textTime.setText(item.time.substring(0, 2) + "시");
        holder.textTemp.setText(item.temperature + "℃");

        String weatherText = "맑음";
        int icon = R.drawable.ic_sunny;

        if (!item.pty.equals("0")) {
            switch (item.pty) {
                case "1": weatherText = "비"; icon = R.drawable.ic_rain; break;
                case "2": weatherText = "비/눈"; icon = R.drawable.ic_rain_snow; break;
                case "3": weatherText = "눈"; icon = R.drawable.ic_snow; break;
                case "4": weatherText = "소나기"; icon = R.drawable.ic_shower; break;
            }
        } else {
            switch (item.sky) {
                case "3": weatherText = "구름많음"; icon = R.drawable.ic_cloudy; break;
                case "4": weatherText = "흐림"; icon = R.drawable.ic_cloud; break;
            }
        }

        holder.textSky.setText(weatherText);
        holder.weatherIcon.setImageResource(icon);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTime, textTemp, textSky;
        ImageView weatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.textTime);
            textTemp = itemView.findViewById(R.id.textTemp);
            textSky = itemView.findViewById(R.id.textSky);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }
}
