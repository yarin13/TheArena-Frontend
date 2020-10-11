package com.example.thearena.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.thearena.Activities.MainActivity;
import com.example.thearena.Classes.NetworkClient;
import com.example.thearena.Interfaces.UploadApis;
import com.example.thearena.R;
import com.example.thearena.Utils.Constants;
import com.example.thearena.Utils.Preferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageUploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageUploadFragment extends Fragment implements View.OnClickListener{

    private Button chooseBtn;
    private Button submitBtn;
    private Button skipBtn;
    private ImageView imageView;
    private final int GALLERY_REQUEST_CODE = 123;
    private Uri imageData = null;
    private File file = null;
    private Boolean storagePermissionGranted;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ImageUploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageUploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageUploadFragment newInstance(String param1, String param2) {
        ImageUploadFragment fragment = new ImageUploadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image_upload, container, false);

        chooseBtn = v.findViewById(R.id.imageUploadSelectImageButton);
        submitBtn = v.findViewById(R.id.imageUploadSubmitButton);
        skipBtn = v.findViewById(R.id.imageUploadRegister);
        imageView = v.findViewById(R.id.imageUploadImageView);

        chooseBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        skipBtn.setOnClickListener(this);

        checkStoragePermissions(getActivity());
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.imageUploadSelectImageButton:
                selectImageFromGallery();
                break;
            case R.id.imageUploadSubmitButton:
                sendImageToServer(imageData);
                break;
            case R.id.imageUploadRegister:
                //moveToMap
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.moveToMap();
                break;
        }
    }

    private void sendImageToServer(Uri imageData) {
        if(file != null){
            Log.d("Testingg","before");
            Retrofit retrofit = NetworkClient.getRetrofit();
            UploadApis uploadApis = retrofit.create(UploadApis.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("newPhoto", file.getName(), requestFile);
            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), Preferences.getUserId(getContext()));
            Call call = uploadApis.uploadImage(body,userId);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    Toast.makeText(getContext(),"Your profile picture updated successfully"  ,Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(getContext(),"Oops something went wrong.."  ,Toast.LENGTH_LONG).show();
                }
            });
        }
        else
            Toast.makeText(getContext(),"Please select an image",Toast.LENGTH_LONG).show();
        file = null;
    }

    private void selectImageFromGallery(){
        if (storagePermissionGranted)
        {
            Intent intent = new Intent();
            intent.setType("image/*");                      //allow to select any kind of images
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Pick an image"),GALLERY_REQUEST_CODE);
        }
        else{
            requestPermission(getActivity());
        }

    }

    //this function will be called every time we choose an image from the gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE &&resultCode==RESULT_OK && data!=null)
        {
            imageData = data.getData();              // here we store the selected data
            imageView.setImageURI(imageData);
            imageView.setVisibility(View.VISIBLE);

            Uri selectedFileURI = data.getData();
            String fullPath = getPath(getContext(), selectedFileURI);
            file = new File(fullPath);
        }
    }




    @Nullable
    public static String getPath(Context context, Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
//                else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);

            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public  void checkStoragePermissions(Activity activity) {
        if(!(ContextCompat.checkSelfPermission(getContext(), Constants.READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Constants.WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED))
        {
            storagePermissionGranted = false;
            // We don't have permission so prompt the user
            requestPermission(activity);
        }
        else
            storagePermissionGranted = true;
    }

    public void requestPermission(Activity activity){
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                Constants.REQUEST_EXTERNAL_STORAGE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
              this function called by google activity(its a google's listener) after the user answers the prompt
              we are looping the "grantResult" array to check if all the permissions we asked are granted

        */
        if (requestCode == Constants.REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        storagePermissionGranted = false;
                        return;
                    }
                }
                storagePermissionGranted = true;

//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
            }
        }
    }

}