package com.example.perbana.adapter;

import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.perbana.R;
import com.example.perbana.model.Weather;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private ArrayList<Weather> list;

    public WeatherAdapter(ArrayList<Weather> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        holder.tvWeatherTemp.setText(list.get(position).getTemperatur());
        holder.ivWeather.setImageResource(list.get(position).getImage());
        holder.tvWeatherDesc.setText(list.get(position).getWeatherDesc());
        holder.tvWeatherTime.setText(list.get(position).getLocalDateTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardWeather;
        private final LinearLayout llWeather;
        private final TextView tvWeatherTemp;
        private final ImageView ivWeather;
        private final TextView tvWeatherDesc;
        private final TextView tvWeatherTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardWeather = itemView.findViewById(R.id.cardWeather);
            llWeather = itemView.findViewById(R.id.llWeather);
            tvWeatherTemp = itemView.findViewById(R.id.tvWeatherTemp);
            ivWeather = itemView.findViewById(R.id.ivWeather);
            tvWeatherDesc = itemView.findViewById(R.id.tvWeatherDesc);
            tvWeatherTime = itemView.findViewById(R.id.tvWeatherTime);
        }
    }
}
