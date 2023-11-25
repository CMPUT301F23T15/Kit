package com.example.kit;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kit.databinding.ColorSplotchBinding;

import java.util.ArrayList;

public class ColorSplotchAdapter extends RecyclerView.Adapter<ColorSplotchAdapter.ColorSplotchViewHolder> {
    private final ArrayList<Integer> colors;
    private final Context context;

    public ColorSplotchAdapter(Context context) {
        this.context = context;
        colors = new ArrayList<>();
        colors.add(R.color.black);
        colors.add(R.color.primary1);
        colors.add(R.color.primary2);
        colors.add(R.color.primary1);
        colors.add(R.color.primary2);
    }

    @NonNull
    @Override
    public ColorSplotchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ColorSplotchBinding binding = ColorSplotchBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ColorSplotchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorSplotchViewHolder holder, int position) {
        int colorRes = colors.get(position);
        holder.binding.colorSplotch.setBackgroundColor(ContextCompat.getColor(context, colorRes));
        holder.binding.colorSplotch.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ColorSplotchViewHolder extends RecyclerView.ViewHolder {
        protected ColorSplotchBinding binding;

        public ColorSplotchViewHolder(ColorSplotchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
