package com.example.perbana.adapter;

import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.perbana.R;
import com.example.perbana.db.model.Weather;
import com.example.perbana.util.DateUtil;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private final String TAG = "WeatherAdapter";

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
        holder.tvWeatherTemp.setText(list.get(position).getTemperatur() + "℃");

        RequestBuilder<PictureDrawable> requestBuilder = GlideToVectorYou
                .init()
                .with(holder.itemView.getContext())
                .withListener(new GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {
                        Log.e(TAG, "onLoadFailed: Gagal load gambar cuaca");
                    }

                    @Override
                    public void onResourceReady() {
                        Log.i(TAG, "onResourceReady: Berhasil load gambar cuaca");
                    }
                })
                .setPlaceHolder(R.drawable.missing_image, R.drawable.missing_image)
                .getRequestBuilder();

        requestBuilder
                .load(Uri.parse(list.get(position).getImage()))
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(new RequestOptions()
                        .centerCrop())
                .into(holder.ivWeather);

        holder.tvWeatherDesc.setText(list.get(position).getWeatherDesc());
        holder.tvWeatherTime.setText(DateUtil.parseDate("yyyy-MM-dd HH:mm:ss", "dd MMMM yyyy (HH:mm)", list.get(position).getLocalDateTime()));
    }

    public void updateData(ArrayList<Weather> list) {
        if (list == null) return;
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
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
