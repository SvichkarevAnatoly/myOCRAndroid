package ru.myocr.model.filter;


import ru.myocr.api.ApiHelper;

public class SearchSourceRemote implements SearchSource {

    @Override
    public void search(Filter filter, SearchResultCallback callback) {

        ApiHelper.makeApiRequest(filter, ApiHelper::searchReceiptItems,
                throwable -> callback.onFailed(),
                callback::onSuccess, null);
    }
}
