package com.example.timad.poznavacka;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ZastupceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "ZastupceAdapter";
    private static final int EDITTEXT_WIDTH = 250;

    private static ArrayList<Object> mZastupceList;
    private static int mParameters;
    private static int[] mIds;

    private OnItemClickListener listener;

    // Classification item view type.
    private static final int CLASSIFICATION_ITEM_VIEW_TYPE = 0;

    // Classic zastupce item view type.
    private static final int ZASTUPCE_ITEM_VIEW_TYPE = 1;

    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = mZastupceList.get(position);
        if (recyclerViewItem instanceof Zastupce) {
            return ZASTUPCE_ITEM_VIEW_TYPE;
        }
        return CLASSIFICATION_ITEM_VIEW_TYPE;
    }

    public interface OnItemClickListener {
        void onViewClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public static class ZastupceViewHolder extends RecyclerView.ViewHolder {
        ImageView zastupceImage;
        ArrayList<EditText> editTArr;
        ImageButton deleteButton;

        ZastupceViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            editTArr = new ArrayList<>();
            for (int x = 0; x < mParameters; x++) {
                final int pos = x;
                editTArr.add(x, (EditText) itemView.findViewById(mIds[x]));
                //Log.d("WELP", "Scroll: " + mIds[x]);
                editTArr.get(x).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        ((Zastupce) mZastupceList.get(getAdapterPosition())).setParameter(editTArr.get(pos).getText().toString(), pos);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            }

            zastupceImage = itemView.findViewById(mIds[mParameters]);

            zastupceImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onViewClick(position);
                        }
                    }
                }
            });

            deleteButton = itemView.findViewById(mIds[mParameters] + 1);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    static class ClassificationViewHolder extends RecyclerView.ViewHolder {
        ArrayList<TextView> textViewArr;

        /**
         * Konstruktor - inicializuje proměnné.
         *
         * @param itemView
         */
        ClassificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewArr = new ArrayList<>();
            for (int x = 0; x < mParameters - 1; x++) {
                textViewArr.add(x, (TextView) itemView.findViewById(mIds[x]));
            }
        }
    }

    private View createClassificationCardView(ViewGroup parent) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.classification, parent, false);
        LinearLayout ll = (LinearLayout) cardView.getChildAt(0);
        Context context = parent.getContext();

        /*Space space = new Space(context);
        space.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
        ));
        space.setMinimumHeight(300);
        space.setMinimumWidth(EDITTEXT_WIDTH);
        //space.setId(mIds[0]);
        ll.addView(space);*/

        createClassificationTexts(context, ll, mParameters);
        return cardView;
    }

    private View createZastupceCardView(ViewGroup parent) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.zastupce, parent, false);
        LinearLayout ll = (LinearLayout) cardView.getChildAt(0);
        Context context = parent.getContext();

        //ll.setWeightSum(mParameters + 1);
        createEditTexts(context, ll, mParameters);

        ImageView imgV = new ImageView(parent.getContext());
        imgV.setLayoutParams(new LinearLayout.LayoutParams(
                //LinearLayout.LayoutParams.WRAP_CONTENT,
                300,
                //LinearLayout.LayoutParams.MATCH_PARENT,
                300,
                1f
        ));
        imgV.setMinimumWidth(400);
        imgV.setMaxWidth(400);
        imgV.setId(mIds[mParameters]);
        //Log.i("GenerateId", "Img: " + imgV.getId());
        ll.addView(imgV);

        ImageButton deleteButton = new ImageButton(parent.getContext());
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                100,
                100,
                1f
        ));
        deleteButton.setMinimumWidth(100);
        deleteButton.setMaxWidth(100);
        deleteButton.setId(mIds[mParameters] + 1);
        ll.addView(deleteButton);

        return cardView;
    }

    private void createEditTexts(Context context, LinearLayout ll, int numOfEditTexts) {
        for (int i = 0; i < numOfEditTexts; i++) {
            //Log.d("WELP", "Button: " + i);
            EditText editT = new EditText(context);
            editT.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
            ));
            editT.setTextSize(14);
            //editT.setMaxLines(2);
            editT.setWidth(EDITTEXT_WIDTH);
            editT.setId(mIds[i]);
            //Log.i("GenerateId", "ET" + i + ": " + editT.getId());
            ll.addView(editT);
        }
    }

    private void createClassificationTexts(Context context, LinearLayout ll, int mParameters) {
        for (int i = 0; i < mParameters - 1; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
            ));
            textView.setTextSize(14);
            textView.setWidth(EDITTEXT_WIDTH);
            textView.setGravity(Gravity.START);
            textView.setId(mIds[i]);
            ll.addView(textView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CLASSIFICATION_ITEM_VIEW_TYPE:
                return new ClassificationViewHolder(createClassificationCardView(parent));
        }
        return new ZastupceViewHolder(createZastupceCardView(parent), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case CLASSIFICATION_ITEM_VIEW_TYPE:
                ClassificationData currentClassification = (ClassificationData) mZastupceList.get(position);
                ClassificationViewHolder cvh = (ClassificationViewHolder) holder;
                for (int i = 0; i < mParameters - 1; i++) {
                    cvh.textViewArr.get(i).setText(Objects.requireNonNull(currentClassification.getClassification()).get(i));
                }
                break;
            case ZASTUPCE_ITEM_VIEW_TYPE:
                //fall through
            default:
                Zastupce currentZastupce = (Zastupce) mZastupceList.get(position);
                ZastupceViewHolder zvh = (ZastupceViewHolder) holder;
                for (int i = 0; i < mParameters; i++) {
                    zvh.editTArr.get(i).setText(currentZastupce.getParameter(i));
                }
                zvh.zastupceImage.setImageDrawable(currentZastupce.getImage()); // IMG
                zvh.deleteButton.setImageDrawable(ResourcesCompat.getDrawable(zvh.deleteButton.getResources(), R.drawable.ic_cross_red_24dp, null));
                zvh.deleteButton.setBackgroundColor(zvh.deleteButton.getResources().getColor(R.color.colorAccentSecond));
        }
    }

    public ZastupceAdapter(ArrayList<Object> zastupceList, int parameters) {
        mZastupceList = zastupceList;
        mParameters = parameters;
        mIds = new int[mParameters + 1];
        for (int i = 0; i < mParameters + 1; i++) {
            mIds[i] = View.generateViewId();
            //Log.i("GenerateId", i + ": " + Integer.toString(mIds[i]));
        }
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


