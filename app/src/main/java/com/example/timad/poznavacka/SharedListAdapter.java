package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SharedListAdapter extends RecyclerView.Adapter<SharedListAdapter.downloadViewHolder> {
    private ArrayList<PrewiewPoznavacka> arr;
    public OnItemClickListener listener;

    public interface  OnItemClickListener{
        void onDownloadClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    // downloadViewHolder
    public static class downloadViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public ImageView mImageView2;

        public downloadViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            mImageView2 = itemView.findViewById(R.id.imgView2);


            mImageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onDownloadClick(position);

                    }

                }
            });
        }
    }
    public SharedListAdapter(ArrayList<PrewiewPoznavacka> arr) {
        this.arr = arr;
    }



    @NonNull
    @Override
    public SharedListAdapter.downloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        downloadViewHolder dvh = new downloadViewHolder(v,listener);
        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SharedListAdapter.downloadViewHolder holder, int position) {
        PrewiewPoznavacka item = arr.get(position);

        holder.mImageView.setImageResource(item.getImageRecource());
        holder.mTextView1.setText(item.getId());
        holder.mTextView2.setText(item.getName());
        holder.mImageView2.setImageResource(R.drawable.ic_file_download);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}
