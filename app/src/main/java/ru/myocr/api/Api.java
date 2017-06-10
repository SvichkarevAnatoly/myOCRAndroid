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
import ru.myocr.model.City;
import ru.myocr.model.SearchReceiptItem;
import ru.myocr.model.Shop;

public interface Api {

    @POST("find")
    Call<FindResponse> find(@Body FindRequest request);

    @Multipart
    @POST("ocr/{cityId}/{shopId}")
    Call<OcrReceiptResponse> ocr(
            @Part MultipartBody.Part receiptItemsImage,
            @Part MultipartBody.Part pricesImage,
            @Path("cityId") long cityId,
            @Path("shopId") long shopId);

    @POST("prices/save")
    Call<Integer> save(@Body SavePriceRequest request);

    @GET("cities/all")
    Call<List<City>> getAllCities();

    @GET("shops/inCity/{cityId}")
    Call<List<Shop>> getShops(@Path("cityId") long cityId);

    @GET("find/prices")
    Call<List<SearchReceiptItem>> getReceiptItems(
            @Query("cityId") Long cityId,
            @Query("shopId") Long shopId,
            @Query("q") String q);

    @GET("receiptItems")
    Call<List<String>> getReceiptItemsInShop(
            @Query("cityId") long cityId,
            @Query("shopId") long shopId);
}
