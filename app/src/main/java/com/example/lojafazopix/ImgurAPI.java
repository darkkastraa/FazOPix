package com.example.lojafazopix;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import com.google.gson.annotations.SerializedName;

public interface ImgurAPI {
    @Multipart
    @POST("3/image")
    Call<ImgurResponse> uploadImage(
            @Part MultipartBody.Part image,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );
}
