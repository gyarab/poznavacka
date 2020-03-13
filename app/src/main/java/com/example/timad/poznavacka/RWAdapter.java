package com.example.timad.poznavacka;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timad.poznavacka.activities.lists.MyListsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/** Pracuje s recyclerWiev **/
public class RWAdapter extends RecyclerView.Adapter<RWAdapter.PoznavackaInfoViewHolder> {
    private ArrayList<PoznavackaInfo> mPoznavackaInfoList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onPracticeClick(int position);
        void onShareClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class PoznavackaInfoViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public TextView textView2;
        public TextView languageURL;
        public ImageView practiceImg;
        public ImageView shareImg;
        public ImageView deleteImg;
        public ImageView prewiewImg;
        public CardView cView;

        public PoznavackaInfoViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.itemText1);
            textView2 = itemView.findViewById(R.id.itemText2);
            languageURL = itemView.findViewById(R.id.languageURL);
            practiceImg = itemView.findViewById(R.id.img_practice);
            shareImg = itemView.findViewById(R.id.img_share);
            deleteImg = itemView.findViewById(R.id.img_delete);
            prewiewImg = itemView.findViewById(R.id.img_prewiew);
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

            practiceImg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onPracticeClick(position);
                        }
                    }
                }
            });

            shareImg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onShareClick(position);
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
        TextViewCompat.setAutoSizeTextTypeWithDefaults(holder.textView1, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(holder.textView2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        holder.textView1.setText(currentPoznavackaInfo.getName());
        holder.textView2.setText(currentPoznavackaInfo.getAuthor());
        holder.languageURL.setText(currentPoznavackaInfo.getLanguageURL());

        Drawable d = MyListsActivity.getSMC(holder.prewiewImg.getContext()).readDrawable(mPoznavackaInfoList.get(position).getId() + "/", mPoznavackaInfoList.get(position).getPrewievImageLocation(), holder.prewiewImg.getContext());
        holder.prewiewImg.setImageDrawable(d);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!user.getUid().equals(currentPoznavackaInfo.getAuthorsID())) {
             holder.shareImg.setEnabled(false);
        } else {
            if (currentPoznavackaInfo.isUploaded()) {
                holder.shareImg.setImageResource(R.drawable.ic_file_upload_blue_24dp);
            } else {
                holder.shareImg.setImageResource(R.drawable.ic_file_upload_dark_purple_24dp);
            }
         }


        if (MyListsActivity.sPositionOfActivePoznavackaInfo == position) {
            //selected
            holder.cView.setCardBackgroundColor(holder.prewiewImg.getResources().getColor(R.color.colorAccentSecond));
        }else{
            //not selected
            holder.cView.setCardBackgroundColor(holder.prewiewImg.getResources().getColor(R.color.colorAccentSecond));
        }
    }

    @Override
    public int getItemCount() {
        return mPoznavackaInfoList.size();
    }
}
