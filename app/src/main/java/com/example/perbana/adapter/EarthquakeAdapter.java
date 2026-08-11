package com.example.perbana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perbana.MainActivity;
import com.example.perbana.R;
import com.example.perbana.db.model.Earthquake;

import java.util.ArrayList;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.ViewHolder> {
    private final String TAG = "EarthquakeAdapter";

    private final Context context;

    private ArrayList<Earthquake> earthquakes = new ArrayList<>();

    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes) {
        this.context = context;
        this.earthquakes = earthquakes;
    }

    @NonNull
    @Override
    public EarthquakeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earthquake, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarthquakeAdapter.ViewHolder holder, int position) {
        Earthquake earthquake = earthquakes.get(position);


        double magnitudeValue = Double.parseDouble(earthquake.getMagnitude());
        holder.tvMagnitudeValue.setText("[M " + magnitudeValue + "]");

        if (magnitudeValue >= 6.0) {
            holder.tvMagnitudeValue.setTextColor(ContextCompat.getColor(context, R.color.danger));
        } else if (magnitudeValue >= 5.0) {
            holder.tvMagnitudeValue.setTextColor(ContextCompat.getColor(context, R.color.warning));
        } else {
            holder.tvMagnitudeValue.setTextColor(ContextCompat.getColor(context, R.color.accent_blue));
        }

        holder.tvEarthquakeLocation.setText(earthquake.getWilayah());

        String dateStr = earthquake.getTanggal();
        String timeStr = earthquake.getJam();

        String cleanTime = "";
        if (timeStr != null && timeStr.length() >= 5) {
            cleanTime = timeStr.substring(0, 5) + " WIB";
        } else {
            cleanTime = timeStr;
        }

        holder.tvEarthquakeDate.setText(dateStr + " • " + cleanTime);
        holder.tvTsunamiPotential.setText("Potensi: " + earthquake.getPotensi());
        holder.tvEarthquakeDepth.setText("Kedalaman: " + earthquake.getKedalaman());
    }

    @Override
    public int getItemCount() {
        return earthquakes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMagnitudeValue;
        TextView tvEarthquakeLocation;
        TextView tvEarthquakeDate;
        TextView tvTsunamiPotential;
        TextView tvEarthquakeDepth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMagnitudeValue = itemView.findViewById(R.id.tv_magnitude_value);
            tvEarthquakeLocation = itemView.findViewById(R.id.tv_earthquake_location);
            tvEarthquakeDate = itemView.findViewById(R.id.tv_earthquake_date);
            tvTsunamiPotential = itemView.findViewById(R.id.tv_tsunami_potential);
            tvEarthquakeDepth = itemView.findViewById(R.id.tv_earthquake_depth);
        }
    }
}
