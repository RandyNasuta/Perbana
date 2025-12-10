package com.example.perbana.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perbana.R;
import com.example.perbana.model.MainMenu;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder>{

    private ArrayList<MainMenu> menus = new ArrayList<>();

    public MainMenuAdapter(ArrayList<MainMenu> menus) {
        this.menus = menus;
    }

    @NonNull
    @Override
    public MainMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainMenuAdapter.ViewHolder holder, int position) {
        holder.ivMainMenu.setImageResource(menus.get(position).getDrawableId());
        holder.tvMainMenu.setText(menus.get(position).getStringId());
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardMainMenu;
        private final LinearLayout llMainMenu;
        private final ImageView ivMainMenu;
        private final TextView tvMainMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMainMenu = itemView.findViewById(R.id.cardMainMenu);
            llMainMenu = itemView.findViewById(R.id.llMainMenu);
            ivMainMenu = itemView.findViewById(R.id.ivMainMenu);
            tvMainMenu = itemView.findViewById(R.id.tvMainMenu);
        }
    }
}
