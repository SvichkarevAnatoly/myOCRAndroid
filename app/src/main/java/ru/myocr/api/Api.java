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
import retrofit2.http.Query;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.model.SearchReceiptItem;

public interface Api {

    @POST("find")
    Call<FindResponse> find(@Body FindRequest request);

    @Multipart
    @POST("ocr/{city}/{shop}")
    Call<OcrReceiptResponse> ocr(
            @Part MultipartBody.Part receiptItemsImage,
            @Part MultipartBody.Part pricesImage,
            @Path("city") String city,
            @Path("shop") String shop);

    @POST("prices/save")
    Call<Integer> save(@Body SavePriceRequest request);

    @GET("cities/all")
    Call<List<String>> getAllCities();

    @GET("shops/inCity/{cityName}")
    Call<List<String>> getShops(@Path("cityName") String cityName);

    @GET("find/prices")
    Call<List<SearchReceiptItem>> getReceiptItems(
            @Query("city") String city,
            @Query("shop") String shop,
            @Query("q") String q);
}
