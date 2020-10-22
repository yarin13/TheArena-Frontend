package com.example.thearena.UI;

import android.content.Context;
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
import com.example.thearena.R;
import com.example.thearena.Utils.Constants;

import java.util.List;


//------------------------------------- RecyclerView To Show the question in the RegisterQuestions Fragment------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------------------


public class LoggedInRecyclerViewImageAdapter extends RecyclerView.Adapter<LoggedInRecyclerViewImageAdapter.ViewHolder> {
    private Context context;
    private List<Integer> photosIds;
    private LayoutInflater mInflater;

    public LoggedInRecyclerViewImageAdapter(Context context, List<Integer> photosIds) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.photosIds = photosIds;
    }

    @NonNull
    @Override
    public LoggedInRecyclerViewImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.loggedin_user_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LoggedInRecyclerViewImageAdapter.ViewHolder holder, final int position) {
        int photoId = photosIds.get(position);
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
        return photosIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.loggedIn_user_photo);
        }
    }
}
