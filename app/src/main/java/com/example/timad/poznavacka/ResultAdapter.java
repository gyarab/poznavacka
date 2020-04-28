package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * recycler view výsledků
 */

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private ArrayList<PreviewResultObject> mTestList;



    public static class ResultViewHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private TextView result;


        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNam4);
            result = itemView.findViewById(R.id.result4);

        }
    }
    public ResultAdapter(ArrayList<PreviewResultObject> mTestList){
        this.mTestList = mTestList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result, parent, false);
        ResultViewHolder rvh = new ResultViewHolder(v);

        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        PreviewResultObject item = mTestList.get(position);
        holder.userName.setText(item.getUserName());
        holder.result.setText(item.getResult());


    }

    @Override
    public int getItemCount() {
        return  mTestList.size();
    }
}
