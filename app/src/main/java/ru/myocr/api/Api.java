package ru.myocr.api;


import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    public static final String SERVER_URL = "http://193.169.0.103:8080/";

    @GET("mirror?key=pasha4")
    Call<SimpleResponse> simpleMethod();
}
