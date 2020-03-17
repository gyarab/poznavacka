package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TestAdpater extends RecyclerView.Adapter<TestAdpater.TestViewHolder>  {
    private ArrayList<TestObject> mTestList;
    private TestAdpater.OnItemClickListener mListener;

    public void setOnItemClickListener(TestAdpater.OnItemClickListener listener){
        mListener = listener;
    }


    public interface OnItemClickListener {
    }


    public static class TestViewHolder extends RecyclerView.ViewHolder{

        private TextView textView1;
        private ImageView prewiewImg1;
        private ImageView resutltImg1;
        private ImageView stop_startImg1;
        private ImageView deleteImg1;



        public TestViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);

            textView1=itemView.findViewById(R.id.testName2);
            prewiewImg1=itemView.findViewById(R.id.img_prewiew2);
            stop_startImg1=itemView.findViewById(R.id.img_start_end2);
            deleteImg1 = itemView.findViewById(R.id.img_delete2);
            resutltImg1 = itemView.findViewById(R.id.img_results2);


        }
    }
    public TestAdpater(ArrayList<TestObject> mTestList){
        mTestList = mTestList;
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

    }

    @Override
    public int getItemCount() {
        return mTestList.size();
    }

}
