package com.example.thearena.UI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.thearena.Activities.MapActivity;
import com.example.thearena.R;
import com.example.thearena.Utils.Constants;

import java.util.List;

import static android.content.ContentValues.TAG;


//------------------------------------- RecyclerView To Show the question in the RegisterQuestions Fragment------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------


public class RecyclerViewImageAdapter extends RecyclerView.Adapter<RecyclerViewImageAdapter.ViewHolder> {
    private Context context;
    private List<Integer> photosIds;
    private LayoutInflater mInflater;

    public RecyclerViewImageAdapter(Context context, List<Integer> photosIds) {
        Log.d("imageadapter", "RecyclerViewImageAdapter: ");
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.photosIds = photosIds;
    }

    @NonNull
    @Override
    public RecyclerViewImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("imageadapter", "onCreateViewHolder: ");
        View view = mInflater.inflate(R.layout.selected_user_photos, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_user_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewImageAdapter.ViewHolder holder, final int position) {
        int photoId = photosIds.get(position);
        Log.d("imageadapter", String.valueOf(photosIds.get(position)));
        GlideUrl glideUrl = new GlideUrl(Constants.PHOTOS_URL, new LazyHeaders.Builder()
                .addHeader("action", "getPhoto")
                .addHeader("photoId", String.valueOf(photoId))
                .build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        Log.d("imageadapter", "getItemCount: "+ photosIds.size());
        return photosIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            Log.d("imageadapter", "ViewHolder: ");
            Log.d(TAG, "ViewHolder: "+1);
            imageView = itemView.findViewById(R.id.selected_user_photo);
        }
    }
}
