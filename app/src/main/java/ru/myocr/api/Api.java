package ru.myocr.api;


import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    String SERVER_URL_LOCAL = "http://192.168.1.105:8080/";
    String SERVER_URL_AMAZON = "http://Sample-env.323rjbfqpy.us-west-2.elasticbeanstalk.com:8080/";

    @POST("find")
    Call<FindResponse> find(@Body FindRequest request);

    @Multipart
    @POST("image")
    Call<OcrResponse> ocr(@Part MultipartBody.Part request);

    @POST("add")
    Call<InsertResponse> insert(@Body InsertRequest request);

    @GET("cities/all")
    Call<List<String>> getAllCities();
}
