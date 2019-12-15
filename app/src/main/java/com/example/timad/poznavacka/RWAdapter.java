package com.example.timad.poznavacka;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timad.poznavacka.activities.lists.MyListsFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
        public CardView cView;

        public PoznavackaInfoViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.itemText1);
            textView2 = itemView.findViewById(R.id.itemText2);
            deleteImg = itemView.findViewById(R.id.img_delete);
            cView = itemView.findViewById(R.id.cardView1);

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
        PoznavackaInfoViewHolder ipvh = new PoznavackaInfoViewHolder(v, mListener);
        return ipvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PoznavackaInfoViewHolder holder, int position) {
        PoznavackaInfo currentPoznavackaInfo = mPoznavackaInfoList.get(position);
        holder.textView1.setText(currentPoznavackaInfo.getText1());
        holder.textView2.setText(currentPoznavackaInfo.getText2());

        if(MyListsFragment.mActivePoznavackaInfo==position){
            holder.cView.setCardBackgroundColor(Color.parseColor("#7CFC00"));
        }else{
            holder.cView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return mPoznavackaInfoList.size();
    }
}
