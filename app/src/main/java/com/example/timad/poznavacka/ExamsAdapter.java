package com.example.timad.poznavacka;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timad.poznavacka.activities.test.TestUserActivity;

import java.util.ArrayList;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder>{
    private static ArrayList<AnswerObject> mParams;
    private onChangeTextListener mListener;


    public interface onChangeTextListener{
    void onChangeTextResult(int position);
    }

    public void setOnChangeTextListener(onChangeTextListener listener){
        this.mListener = listener;
    }
    public static class ExamsViewHolder extends RecyclerView.ViewHolder {

        private TextView param;
        private EditText result;

        public ExamsViewHolder(@NonNull final View itemView, final onChangeTextListener listener) {
            super(itemView);

            param = itemView.findViewById(R.id.parameter3);
            result = itemView.findViewById(R.id.answer3);

            result.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    int curr=(TestUserActivity.index-1)*TestUserActivity.parametrs+getAdapterPosition();
                    if(getAdapterPosition()!=RecyclerView.NO_POSITION) {
                        if (s.toString().equals(mParams.get(getAdapterPosition()).getAnswer())&&TestUserActivity.tempResult[curr]==0){
                            TestUserActivity.tempResult[(TestUserActivity.index-1)*TestUserActivity.parametrs+getAdapterPosition()]++;
                            Toast.makeText(itemView.getContext(), Integer.toString(TestUserActivity.tempResult[(TestUserActivity.index-1)*TestUserActivity.parametrs+getAdapterPosition()]),Toast.LENGTH_SHORT).show();
                            result.setEnabled(false);
                        }else if(TestUserActivity.tempResult[curr]!=0&&TestUserActivity.tempResult[curr]!=1){
                            TestUserActivity.tempResult[curr]--;
                            Toast.makeText(itemView.getContext(),Integer.toString(TestUserActivity.tempResult[curr]),Toast.LENGTH_SHORT).show();
                        }
                    }
                    TestUserActivity.tempAnswer[curr]=result.getText().toString();
                    Toast.makeText(itemView.getContext(),mParams.get(getAdapterPosition()).getAnswer()+","+s.toString(),Toast.LENGTH_SHORT).show();

                }
            });

            }

        }

    public ExamsAdapter(ArrayList<AnswerObject> arr){
        this.mParams=arr;
    }

    @NonNull
    @Override
    public ExamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_place, parent, false);
        ExamsViewHolder viewHolder = new ExamsViewHolder(v,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsViewHolder holder, int position) {
        AnswerObject item = mParams.get(position);
        holder.param.setText(item.getFieldName());
        int curr = (TestUserActivity.index - 1) * TestUserActivity.parametrs + position;
        try {
            if (!TestUserActivity.tempAnswer[(TestUserActivity.index - 1) * TestUserActivity.parametrs + position].equals("")) {
                holder.result.setText(TestUserActivity.tempAnswer[curr]);
            }
            if(TestUserActivity.tempResult[curr]==1){
                holder.result.setEnabled(false);
            }
        }catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return mParams.size();
    }
}
