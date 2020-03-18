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
    }


    public static class TestViewHolder extends RecyclerView.ViewHolder{

        private TextView textView1;
        private ImageView previewImg1;
        private ImageView resultImg1;
        private ImageView stop_startImg1;
        private ImageView deleteImg1;



        public TestViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);

            textView1=itemView.findViewById(R.id.testName2);
            previewImg1 =itemView.findViewById(R.id.img_prewiew2);
            stop_startImg1=itemView.findViewById(R.id.img_start_end2);
            deleteImg1 = itemView.findViewById(R.id.img_delete2);
            resultImg1 = itemView.findViewById(R.id.img_results2);


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
        holder.resultImg1.setImageResource(R.drawable.ic_result);
        holder.deleteImg1.setImageResource(R.drawable.ic_delete);
        holder.stop_startImg1.setImageResource(R.drawable.ic_list_play_black_24dp);
        Picasso.get().load(item.getPreviewImgUrl()).fit().error(R.drawable.ic_image).into(holder.previewImg1);

    }

    @Override
    public int getItemCount() {
        return mTestList.size();
    }

}
