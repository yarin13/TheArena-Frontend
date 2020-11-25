package com.example.thearena.Interfaces;





import com.example.thearena.Classes.DeletePhoto;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UploadApis {

    @Multipart
    @PUT("PhotosServlet")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part image, @Part("userId") RequestBody requestBody);

    @Multipart
    @POST("PhotosServlet")
    Call<ResponseBody> postRequestImage(@Part MultipartBody.Part image, @Part("email") RequestBody requestBody);

//    @DELETE("PhotosServlet")
    @HTTP(method = "DELETE", path = "PhotosServlet", hasBody = true)
    Call<ResponseBody> deleteImage(@Body DeletePhoto deletePhoto);

//
//    @GET("PhotosServlet")
//    @Streaming
//    Call<ResponseBody> getImage(@Header("action") String action,@Header("userId") int userId);

}


