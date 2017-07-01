package ru.myocr.api;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.model.City;
import ru.myocr.model.SearchReceiptItem;
import ru.myocr.model.Shop;
import ru.myocr.model.filter.Filter;
import ru.myocr.preference.Server;
import ru.myocr.util.BitmapUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ApiHelper {

    private static ApiHelper instance;
    private final Api api;

    public ApiHelper() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Server.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        api = retrofit.create(Api.class);
    }

    public static ApiHelper getInstance() {
        if (instance == null) {
            instance = new ApiHelper();
        }
        return instance;
    }

    public static <R, T> Subscription makeApiRequest(R request, ApiRequesterFunction<T, R> function,
                                                     Action1<Throwable> handler, @Nullable Action1<T> onNext,
                                                     final Action0 onCompleted) {
        return getApiObservable(request, function)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                            if (onNext != null) {
                                onNext.call(t);
                            }
                        }, handler,
                        () -> {
                            if (onCompleted != null) {
                                onCompleted.call();
                            }
                        });
    }

    public static <R, T> Observable<T> getApiObservable(R request, ApiRequesterFunction<T, R> function) {
        return Observable.create(
                (subscriber -> {
                    T resp = function.apply(ApiHelper.getInstance(), request);
                    if (resp == null) {
                        subscriber.onError(new Exception("Response shouldn't be null"));
                    } else {
                        subscriber.onNext(resp);
                        subscriber.onCompleted();
                    }
                })
        );
    }

    public List<City> getAllCities(Void v) {
        Call<List<City>> call = api.getAllCities();
        return makeRequest(call);
    }

    public boolean addShop() {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public OcrReceiptResponse ocr(OcrRequest request) {
        final MultipartBody.Part receiptItemsPart = BitmapUtil.buildMultipartBody(request.receiptItems, "receiptItemsImage");
        final MultipartBody.Part pricesPart = BitmapUtil.buildMultipartBody(request.prices, "pricesImage");

        Call<OcrReceiptResponse> call = api.ocr(receiptItemsPart, pricesPart, request.cityId, request.shopId);
        OcrReceiptResponse response = makeRequest(call);

        for (int i = 0; i < response.getPrices().size(); i++) {
            ParsedPrice price = response.getPrices().get(i);
            price.setStringValue(price.getStringValue().replace(" ", ""));
        }

        return response;
    }

    public Integer save(SavePriceRequest request) {
        final Call<Integer> call = api.save(request);
        return makeRequest(call);
    }

    public List<Shop> getShops(long cityId) {
        Call<List<Shop>> call = api.getShops(cityId);
        return makeRequest(call);
    }

    public List<String> getReceiptItemsInShop(ReceiptItemsInShopRequest request) {
        Call<List<String>> call = api.getReceiptItemsInShop(
                request.getCityId(), request.getShopId());
        return makeRequest(call);
    }

    public List<SearchReceiptItem> searchReceiptItems(Filter filter) {
        Call<List<SearchReceiptItem>> call = api.getReceiptItems(
                filter.getCityId(), filter.hasShopId() ? filter.getShopId() : null, filter.getQuery());
        return makeRequest(call);
    }

    private <T> T makeRequest(Call<T> httpCall) {
        Response<T> response = null;
        try {
            response = httpCall.execute();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throwApiError("Timeout");
        } catch (IOException e) {
            e.printStackTrace();
            throwApiError("Request failed");
        }
        if (response == null || !response.isSuccessful()) {
            throwApiError(response.message());
        }
        return response.body();
    }

    private void throwApiError(String msg) {
        throw new RuntimeException(msg);
    }

    public interface ApiRequesterFunction<T, R> {
        T apply(ApiHelper requester, R var);
    }

}
