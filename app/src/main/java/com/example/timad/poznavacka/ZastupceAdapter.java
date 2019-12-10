package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ZastupceAdapter extends RecyclerView.Adapter<ZastupceAdapter.ZastupceViewHolder> {
    private ArrayList<Zastupce> mZastupceList;

    public static class ZastupceViewHolder extends RecyclerView.ViewHolder {
        public ImageView zastupceImage;
        public EditText editTZastupce;
        public EditText editTDruh;
        public EditText editTKmen;

        public ZastupceViewHolder(@NonNull View itemView) {
            super(itemView);
            zastupceImage = itemView.findViewById(R.id.imageViewZ);
            editTZastupce = itemView.findViewById(R.id.editText1);
            editTDruh = itemView.findViewById(R.id.editText2);
            editTKmen = itemView.findViewById(R.id.editText3);
        }
    }

    @NonNull
    @Override
    public ZastupceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zastupce, parent, false);
        ZastupceViewHolder ipvh = new ZastupceViewHolder(v);
        return ipvh;
    }

    public ZastupceAdapter(ArrayList<Zastupce> zastupceList){
        mZastupceList = zastupceList;
    }

    @Override
    public void onBindViewHolder(@NonNull ZastupceViewHolder holder, int position) {
        Zastupce currentZastupce = mZastupceList.get(position);

        //holder.zastupceImage.setImageResource(); dodělat img
        holder.editTZastupce.setText(currentZastupce.getZastupce());
        holder.editTDruh.setText(currentZastupce.getDruh());
        holder.editTKmen.setText(currentZastupce.getKmen());
    }

    @Override
    public int getItemCount() {
        return mZastupceList.size();
    }
}
