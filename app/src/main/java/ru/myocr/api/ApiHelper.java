package ru.myocr.api;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.myocr.api.ocr.Match;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;
import ru.myocr.model.City;
import ru.myocr.model.DummyReceipt;
import ru.myocr.model.Receipt;
import ru.myocr.model.SearchReceiptItem;
import ru.myocr.model.filter.Filter;
import ru.myocr.preference.Server;
import ru.myocr.preference.Settings;
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

    public FindResponse find(FindRequest request) {
        final Call<FindResponse> responseCall = api.find(request);
        return makeRequest(responseCall);
    }

    public List<City> getAllCities(Void v) {
        Call<List<String>> call = api.getAllCities();
        List<String> citiesStr = makeRequest(call);

        List<City> cities = new ArrayList<>(citiesStr.size());
        for (String city : citiesStr) {
            City c = new City(city, city);
            c.putIfNotExist();
            cities.add(c);
        }

        if ((Settings.getString(Settings.CITY) == null) && (cities.size() != 0)) {
            Settings.setString(Settings.CITY, cities.get(0).id);
        }

        return cities;
    }

    public List<Receipt> getAllReceipt(Void v) {
        return DummyReceipt.LIST;
    }

    public OcrReceiptResponse ocr(OcrRequest request) {
        final MultipartBody.Part receiptItemsPart = BitmapUtil.buildMultipartBody(request.receiptItems, "receiptItemsImage");
        final MultipartBody.Part pricesPart = BitmapUtil.buildMultipartBody(request.prices, "pricesImage");

        Call<OcrReceiptResponse> call = api.ocr(receiptItemsPart, pricesPart, request.city, request.shop);
        OcrReceiptResponse response = makeRequest(call);

        for (int i = 0; i < response.getPrices().size(); i++) {
            ParsedPrice price = response.getPrices().get(i);
            price.setStringValue(price.getStringValue().replace(" ", ""));
        }

        return response;
    }

    public OcrReceiptResponse ocrFake(OcrRequest request) {
        List<ReceiptItemMatches> itemMatches = new ArrayList<>();
        List<ParsedPrice> prices = new ArrayList<>();

        ArrayList<Match> matches = new ArrayList<>();
        matches.add(new Match(DummyReceipt.getDummyProduct(), 10));

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            itemMatches.add(new ReceiptItemMatches(DummyReceipt.getDummyProduct(), matches));
            prices.add(new ParsedPrice("1000", random.nextInt(100) * 1000));
        }

        return new OcrReceiptResponse(itemMatches, prices);
    }

    public Integer save(SavePriceRequest request) {
        final Call<Integer> call = api.save(request);
        return makeRequest(call);
    }

    public List<String> getShops(String city) {
        Call<List<String>> call = api.getShops(city);
        return makeRequest(call);
    }

    public List<String> getReceiptItemsInShop(ReceiptItemsInShopRequest request) {
        Call<List<String>> call = api.getReceiptItemsInShop(
                request.getCity(), request.getShop());
        return makeRequest(call);
    }

    public List<SearchReceiptItem> searchReceiptItems(Filter filter) {
        Call<List<SearchReceiptItem>> call = api.getReceiptItems(
                filter.getCity(), filter.getShop(), filter.getQuery());
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
