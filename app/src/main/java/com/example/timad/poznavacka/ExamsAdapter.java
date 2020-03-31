package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder>{
    private ArrayList<AnswerObject> mParams;



    public static class ExamsViewHolder extends RecyclerView.ViewHolder {

        private TextView param;
        private EditText result;

        public ExamsViewHolder(@NonNull View itemView) {
            super(itemView);

            param = itemView.findViewById(R.id.parameter3);
            result = itemView.findViewById(R.id.answer3);

        }
    }
    public ExamsAdapter(ArrayList<AnswerObject> arr){
        this.mParams=arr;
    }

    @NonNull
    @Override
    public ExamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_place, parent, false);
        ExamsViewHolder viewHolder = new ExamsViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsViewHolder holder, int position) {
        AnswerObject item = mParams.get(position);
        holder.param.setText(item.getFieldName());

    }

    @Override
    public int getItemCount() {
        return mParams.size();
    }
}
