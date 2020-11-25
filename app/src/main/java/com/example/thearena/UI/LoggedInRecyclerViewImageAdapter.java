package com.example.thearena.UI;

import android.app.admin.DelegatedAdminReceiver;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.thearena.Activities.MapActivity;
import com.example.thearena.Classes.DeletePhoto;
import com.example.thearena.Classes.NetworkClient;
import com.example.thearena.Interfaces.UploadApis;
import com.example.thearena.R;
import com.example.thearena.Utils.Constants;
import com.example.thearena.Utils.Preferences;

import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


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


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = NetworkClient.getRetrofit();
                UploadApis uploadApis = retrofit.create(UploadApis.class);
                DeletePhoto deletePhoto = new DeletePhoto();
                deletePhoto.email = Preferences.getMail(Objects.requireNonNull(context));
                deletePhoto.photoId = photoId;
                Call call = uploadApis.deleteImage(deletePhoto);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        view.setVisibility(View.GONE);
                        photosIds.remove(position);
                    }
                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return photosIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageButton deleteBtn;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.loggedIn_user_photo);
            deleteBtn = itemView.findViewById(R.id.loggedIn_user_delete_btn);
        }
    }
}
