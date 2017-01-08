package ru.myocr.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    public static final String SERVER_URL = "http://193.169.0.103:8080/";

    @POST("findAll")
    Call<FindAllResponse> findAll(@Body FindAllRequest request);
}
