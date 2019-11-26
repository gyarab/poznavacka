package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/** Pracuje s recyclerWiev **/
public class RWAdapter extends RecyclerView.Adapter<RWAdapter.PoznavackaInfoViewHolder> {
    private ArrayList<PoznavackaInfo> mPoznavackaInfoList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class PoznavackaInfoViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public TextView textView2;
        public ImageView deleteImg;

        public PoznavackaInfoViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
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

    public RWAdapter(ArrayList<PoznavackaInfo> poznavackaInfoList){
        mPoznavackaInfoList = poznavackaInfoList;
    }

    @NonNull
    @Override
    public PoznavackaInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poznavacka_info, parent, false);
        PoznavackaInfoViewHolder zvh = new PoznavackaInfoViewHolder(v, mListener);
        return zvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PoznavackaInfoViewHolder holder, int position) {
        PoznavackaInfo currentPoznavackaInfo = mPoznavackaInfoList.get(position);
        holder.textView1.setText(currentPoznavackaInfo.getText1());
        holder.textView1.setText(currentPoznavackaInfo.getText2());
    }

    @Override
    public int getItemCount() {
        return mPoznavackaInfoList.size();
    }
}
