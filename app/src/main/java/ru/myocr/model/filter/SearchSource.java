package ru.myocr.model.filter;


import java.util.List;

import ru.myocr.model.SearchReceiptItem;

public interface SearchSource {

    void search(Filter filter, SearchResultCallback callback);

    interface SearchResultCallback {

        void onFailed();

        void onSuccess(List<SearchReceiptItem> result);
    }
}
