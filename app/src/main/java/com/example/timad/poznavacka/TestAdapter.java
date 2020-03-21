package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder>  {
    private ArrayList<PreviewTestObject> mTestList;
    private TestAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(TestAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onResultsClick(int position);
        void onStart_EndClick(int position);
    }


    public static class TestViewHolder extends RecyclerView.ViewHolder{

        private TextView textView1;
        private TextView PIN1;
        private ImageView previewImg1;
        private ImageView resultImg1;
        private ImageView stop_startImg1;
        private ImageView deleteImg1;




        public TestViewHolder(@NonNull View itemView, final OnItemClickListener mListener) {
            super(itemView);

            textView1=itemView.findViewById(R.id.testName2);
            PIN1 = itemView.findViewById(R.id.PIN2);
            previewImg1 =itemView.findViewById(R.id.img_preview2);
            stop_startImg1=itemView.findViewById(R.id.img_start_end2);
            deleteImg1 = itemView.findViewById(R.id.img_delete2);
            resultImg1 = itemView.findViewById(R.id.img_results2);

            deleteImg1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onDeleteClick(position);
                        }
                    }
                }
            });
            stop_startImg1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onStart_EndClick(position);
                        }
                    }
                }
            });
            resultImg1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onResultsClick(position);
                        }
                    }
                }
            });

        }
    }
    public TestAdapter(ArrayList<PreviewTestObject> mTestList){
        this.mTestList = mTestList;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test, parent, false);
        TestViewHolder tvh = new TestViewHolder(v,mListener);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        PreviewTestObject item = mTestList.get(position);
        holder.textView1.setText(item.getName());
        holder.PIN1.setText("PIN:"+item.getTestCode());
        holder.resultImg1.setImageResource(R.drawable.ic_result);
        if(item.isStarted()&&!item.isFinished()){
            holder.deleteImg1.setImageResource(0);
            holder.deleteImg1.setEnabled(false);
        }else {
            holder.deleteImg1.setEnabled(true);
            holder.deleteImg1.setImageResource(R.drawable.ic_delete);
        }
        if(item.isFinished()) {
            holder.stop_startImg1.setImageResource(0);
            holder.stop_startImg1.setEnabled(false);
        }else if (!item.isStarted()) {
            holder.stop_startImg1.setImageResource(R.drawable.ic_list_play_black_24dp);
        }else{
            holder.stop_startImg1.setImageResource(R.drawable.ic_stop);
        }

        Picasso.get().load(item.getPreviewImgUrl()).fit().error(R.drawable.ic_image).into(holder.previewImg1);

    }

    @Override
    public int getItemCount() {
        return mTestList.size();
    }

}
