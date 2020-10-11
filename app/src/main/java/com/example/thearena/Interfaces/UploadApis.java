package com.example.thearena.Interfaces;





import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UploadApis {

    @Multipart
    @PUT("PhotosServlet")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part image, @Part("userId") RequestBody requestBody);

//    @GET("PhotosServlet")
//    @Streaming
//    Observable<ResponseBody> getImage(@Header("action") String action, @Header("userId") int userId);

//
//    @GET("PhotosServlet")
//    @Streaming
//    Call<ResponseBody> getImage(@Header("action") String action,@Header("userId") int userId);

}


