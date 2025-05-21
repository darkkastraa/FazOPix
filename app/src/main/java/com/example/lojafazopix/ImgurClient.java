package com.example.lojafazopix;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImgurClient {
    private static final String BASE_URL = "https://api.imgur.com/";
    private static ImgurAPI imgurAPI;

    public static ImgurAPI getImgurAPI() {
        if (imgurAPI == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException, IOException {
                            Request request = chain.request().newBuilder().addHeader("Authorization", "Client-ID " + "2092d8406b25f59").build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            imgurAPI = retrofit.create(ImgurAPI.class);
        }
        return imgurAPI;
    }
}
