package com.adamec.timotej.poznavacka;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Interface.LoadMore;
import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

class LoadingViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;

    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.item_loading_progressBar);
    }
}


// downloadViewHolder
class DownloadViewHolder extends RecyclerView.ViewHolder {
    public ImageView mImageView;
    public TextView mTextView1;
    public TextView mTextView2;
    public TextView mLangTextView;
    public ImageView mImageView2;
    public ImageView mImageView3;

    public DownloadViewHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.imgView);
        mTextView1 = itemView.findViewById(R.id.textView);
        mTextView2 = itemView.findViewById(R.id.textView2);
        mLangTextView = itemView.findViewById(R.id.shared_lang_textView);
        mImageView2 = itemView.findViewById(R.id.imgView2);
        mImageView3 = itemView.findViewById(R.id.imgView3);
    }
}


public class SharedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private ArrayList<PreviewPoznavacka> arr;
    private ArrayList<PreviewPoznavacka> arrFull;
    private OnItemClickListener listener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    LoadMore loadMore;
    boolean isLoading;
    Activity activity;
    //private int visibleThreshold = 1;
    int lastVisibleItem;
    int totalItemCount;

    public SharedListAdapter(RecyclerView recyclerView, ArrayList<PreviewPoznavacka> arr, Activity activity) {
        this.arr = arr;
        this.activity = activity;
        Timber.plant(new Timber.DebugTree());

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                //lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading) {
                    if (loadMore != null) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                loadMore.onLoadMore();
                            }
                        });
                        //loadMore.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return arr.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(LoadMore loadMore) {
        this.loadMore = loadMore;
    }

    public interface OnItemClickListener {
        void onDownloadClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public SharedListAdapter(ArrayList<PreviewPoznavacka> arr) {
        this.arr = arr;
        arrFull = new ArrayList<>(arr);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item, parent, false);
            return new DownloadViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    /*    @NonNull
    @Override
    public SharedListAdapter.downloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        downloadViewHolder dvh = new downloadViewHolder(v,listener);
        return dvh;
    }*/

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DownloadViewHolder) {
            PreviewPoznavacka previewPoznavacka = arr.get(position);
            final DownloadViewHolder viewHolder = (DownloadViewHolder) holder;

            PreviewPoznavacka item = arr.get(position);
            //Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Asian_Elephant_Prague_Zoo.jpg/258px-Asian_Elephant_Prague_Zoo.jpg").fit().into(holder.mImageView);
            Picasso.get().load(item.getImageRecource()).fit().error(R.drawable.ic_image).into(viewHolder.mImageView);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            TextViewCompat.setAutoSizeTextTypeWithDefaults(viewHolder.mTextView1, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            viewHolder.mTextView1.setText(item.getName() + " (" + item.getRepresentativesCount() + ")");
            viewHolder.mTextView2.setText(item.getAuthorsName());
            viewHolder.mLangTextView.setText(item.getLanguageURL());
            viewHolder.mImageView2.setImageResource(R.drawable.ic_file_download);
            viewHolder.mImageView3.setEnabled(false);
            if (item.getAuthorsUuid().equals(user.getUid())) {
                viewHolder.mImageView3.setEnabled(true);
                viewHolder.mImageView3.setImageResource(R.drawable.ic_delete);
                Timber.d("Delete button active for %s", previewPoznavacka.getName());
            }

            viewHolder.mImageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDownloadClick(position);

                    }

                }
            });

            viewHolder.mImageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public Filter getFilter() {
        return previewPoznavackaFilter;
    }

    private Filter previewPoznavackaFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<PreviewPoznavacka> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (PreviewPoznavacka item :
                        arrFull) {
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getAuthorsName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arr.clear();
            arr.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
