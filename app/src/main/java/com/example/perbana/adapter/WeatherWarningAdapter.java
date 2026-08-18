package com.example.perbana.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perbana.R;
import com.example.perbana.db.model.Rss;
import com.example.perbana.db.model.WeatherWarning;
import com.example.perbana.presentation.weather.weather_warning_detail.WeatherWarningDetailActivity;
import com.example.perbana.util.DateUtil;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class WeatherWarningAdapter extends RecyclerView.Adapter<WeatherWarningAdapter.ViewHolder> {
    private final String TAG = "WeatherWarningAdapter";
    private List<WeatherWarning> list;
    private Context context;

    public WeatherWarningAdapter(List<WeatherWarning> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherWarningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_warning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherWarningAdapter.ViewHolder holder, int position) {
        WeatherWarning data = list.get(position);

        holder.tvWarningTitle.setText(data.getTitle());
        holder.tvWarningDate.setText(DateUtil.weatherWarningDate(data.getPubDate()));
        holder.tvWarningAuthor.setText(data.getAuthor());
        holder.tvWarningDescription.setText(data.getDescription());

        holder.cardItemWeatherWarning.setOnClickListener(view -> {
            Intent intent = new Intent(context, WeatherWarningDetailActivity.class);
            intent.putExtra("EXTRA_LINK" ,data.getLink());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<WeatherWarning> weatherWarningList) {
        this.list.clear();
        this.list.addAll(weatherWarningList);
        Log.i(TAG, "updateData: new data list: " + list.toString());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardItemWeatherWarning;
        private TextView tvWarningTitle;
        private TextView tvWarningDate;
        private TextView tvWarningAuthor;
        private TextView tvWarningDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardItemWeatherWarning = itemView.findViewById(R.id.card_item_weather_warning);
            tvWarningTitle = itemView.findViewById(R.id.tv_warning_title);
            tvWarningDate = itemView.findViewById(R.id.tv_warning_date);
            tvWarningAuthor = itemView.findViewById(R.id.tv_warning_author);
            tvWarningDescription = itemView.findViewById(R.id.tv_tsunami_potential);
        }
    }
}
