package com.example.perbana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perbana.R;
import com.example.perbana.db.model.Earthquake;
import com.example.perbana.db.model.EarthquakeFelt;

import java.util.ArrayList;

public class EarthquakeFeltAdapter extends RecyclerView.Adapter<EarthquakeFeltAdapter.ViewHolder> {
    private final String TAG = "EarthquakeFeltAdapter";

    private final Context context;

    private ArrayList<EarthquakeFelt> earthquakes = new ArrayList<>();

    public EarthquakeFeltAdapter(Context context, ArrayList<EarthquakeFelt> earthquakes) {
        this.context = context;
        this.earthquakes = earthquakes;
    }

    @NonNull
    @Override
    public EarthquakeFeltAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earthquake, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarthquakeFeltAdapter.ViewHolder holder, int position) {
        EarthquakeFelt earthquake = earthquakes.get(position);


        holder.tvMagnitudeValue.setText("[M " + earthquake.getMagnitude() + "]");
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
        holder.tvTsunamiPotential.setText("Dirasakan: " + earthquake.getDirasakan());
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
