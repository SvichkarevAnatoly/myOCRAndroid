package ru.myocr.api;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.myocr.api.ocr.OcrReceiptResponse;
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Server.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
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

    public FindResponse find(FindRequest request) {
        final Call<FindResponse> responseCall = api.find(request);
        return makeRequest(responseCall);
    }

    public List<String> getAllCities(Void v) {
        Call<List<String>> call = api.getAllCities();
        return makeRequest(call);
    }

    public OcrReceiptResponse ocr(OcrRequest request) {
        final MultipartBody.Part receiptItemsPart = BitmapUtil.buildMultipartBody(request.receiptItems, "receiptItemsImage");
        final MultipartBody.Part pricesPart = BitmapUtil.buildMultipartBody(request.prices, "pricesImage");

        Call<OcrReceiptResponse> call = api.ocr(receiptItemsPart, pricesPart, request.city, request.shop);
        return makeRequest(call);
    }

    public Integer save(SavePriceRequest request) {
        final Call<Integer> call = api.save(request);
        return makeRequest(call);
    }

    public List<String> getShops(String city) {
        Call<List<String>> call = api.getShops(city);
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
