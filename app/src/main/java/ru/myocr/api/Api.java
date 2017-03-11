package ru.myocr.api;


import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Api {

    @POST("find")
    Call<FindResponse> find(@Body FindRequest request);

    @Multipart
    @POST("image")
    Call<OcrResponse> ocr(@Part MultipartBody.Part request);

    @POST("add")
    Call<InsertResponse> insert(@Body InsertRequest request);

    @GET("cities/all")
    Call<List<String>> getAllCities();

    @GET("shops/inCity/{cityName}")
    Call<List<String>> getShops(@Path("cityName") String cityName);
}
