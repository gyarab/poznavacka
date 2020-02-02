package com.example.timad.poznavacka;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ZastupceAdapter extends RecyclerView.Adapter<ZastupceAdapter.ZastupceViewHolder> {
    private static String TAG = "ZastupceAdapter";

    private static ArrayList<Zastupce> mZastupceList;
    private static int mParameters;
    private static int[] mIds;

    public static class ZastupceViewHolder extends RecyclerView.ViewHolder {
        public ImageView zastupceImage;
        public ArrayList<EditText> editTArr;



        //image  --   https://stackoverflow.com/a/41479670/10746262

        public ZastupceViewHolder(@NonNull View itemView) {
            super(itemView);
            editTArr = new ArrayList<>();
            for (int x = 0; x < mParameters; x++){
                final int pos = x;
                editTArr.add(x, (EditText) itemView.findViewById(mIds[x]));
                editTArr.get(x).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        mZastupceList.get(getAdapterPosition()).setParameter(editTArr.get(pos).getText().toString(), pos);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {   }
                });
            }
            zastupceImage = itemView.findViewById(mIds[mParameters]);

            /*for (int i = 0; i < mParameters; i++){
                // https://stackoverflow.com/questions/31844373/saving-edittext-content-in-recyclerview
                editTArr.get(i).addTextChangedListener(new TextWatcher() {

                });
            }*/
        }
    }

    private View createCardView(ViewGroup parent){
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.zastupce, parent, false);
        LinearLayout ll = (LinearLayout) cardView.getChildAt(0);
        Context context = parent.getContext();

        int length = 4; // Set number of edit text's in row CHANGE
        //Log.d("WELP", "mParameters: " + mParameters + " ");
        if(mParameters < length + 1) {
            ll.setWeightSum(mParameters + 1);
            createEditTexts(context, ll, 0, mParameters);
        } else {
            int count = mParameters / length; // no of cols
            if(mParameters % length > 0){
                count++;
            }

            ll.setWeightSum(mParameters + 1);

            LinearLayout vert = new LinearLayout(context);
            vert.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    4f

            ));
            vert.setOrientation(LinearLayout.VERTICAL);
            vert.setWeightSum(count);
            ll.addView(vert);

            for (int i = 0; i < count; i++){
                //Log.d("WELP", "Count/Cols: " + count);
                LinearLayout horiz = new LinearLayout(context);
                horiz.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                ));
                horiz.setOrientation(LinearLayout.HORIZONTAL);
                vert.addView(horiz);

                int end; // no of edit texts in 1 row
                if((i + 1) * length > mParameters){
                    end = mParameters;
                } else {
                    end = ((i + 1) * length);
                }
                horiz.setWeightSum(end - (i * length));

                createEditTexts(context, horiz, i * length, end);
            }
        }

        ImageView imgV = new ImageView(parent.getContext());
        imgV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
        ));
        imgV.setId(mIds[mParameters]);
        //Log.i("GenerateId", "Img: " + imgV.getId());
        ll.addView(imgV);

        return cardView;
    }

    private void createEditTexts(Context context, LinearLayout ll, int start, int end){
        for(int i = start; i < end; i++){
            //Log.d("WELP", "Button" + i);
            EditText editT = new EditText(context);
            editT.setTextSize(15);
            editT.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
            ));
            editT.setId(mIds[i]);
            //Log.i("GenerateId", "ET" + i + ": " + editT.getId());
            ll.addView(editT);
        }
    }

    @NonNull
    @Override
    public ZastupceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zastupce, parent, false);
        ZastupceViewHolder ipvh = new ZastupceViewHolder(createCardView(parent));
        return ipvh;
    }

    public ZastupceAdapter(ArrayList<Zastupce> zastupceList, int parameters){
        mZastupceList = zastupceList;
        mParameters = parameters;
        mIds = new int[mParameters + 1];
        for (int i = 0; i < mParameters + 1; i++){
            mIds[i] = View.generateViewId();
            //Log.i("GenerateId", i + ": " + Integer.toString(mIds[i]));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ZastupceViewHolder holder, int position) {
        Zastupce currentZastupce = mZastupceList.get(position);
        for (int i = 0; i < mParameters; i++){
            holder.editTArr.get(i).setText(currentZastupce.getParameter(i));
        }

        holder.zastupceImage.setImageDrawable(currentZastupce.getImage()); // IMG
    }

    public int getmParameters() {
        return mParameters;
    }

    public void setmParameters(int mParameters) {
        ZastupceAdapter.mParameters = mParameters;
    }

    @Override
    public int getItemCount() {
        return mZastupceList.size();
    }
}


