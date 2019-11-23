package com.example.timad.poznavacka;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/** Pracuje s recyclerWiev **/
public class RWAdapter extends RecyclerView.Adapter<RWAdapter.ZastupceViewHolder> {
    private ArrayList<Zastupce> mZastupceList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ZastupceViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public TextView textView2;
        public ImageView deleteImg;

        public ZastupceViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.itemText1);
            textView2 = itemView.findViewById(R.id.itemText2);
            deleteImg = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            deleteImg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public RWAdapter(ArrayList<Zastupce> zastupceList){
        mZastupceList = zastupceList;
    }

    @NonNull
    @Override
    public ZastupceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zastupce, parent, false);
        ZastupceViewHolder zvh = new ZastupceViewHolder(v, mListener);
        return zvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ZastupceViewHolder holder, int position) {
        Zastupce currentZastupce = mZastupceList.get(position);
        holder.textView1.setText(currentZastupce.getText1());
        holder.textView1.setText(currentZastupce.getText2());
    }

    @Override
    public int getItemCount() {
        return mZastupceList.size();
    }
}
