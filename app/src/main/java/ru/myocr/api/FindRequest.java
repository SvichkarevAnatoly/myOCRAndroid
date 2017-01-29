package ru.myocr.api;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindRequest {

    @SerializedName("products")
    public List<String> products;

    public FindRequest(List<String> products) {
        this.products = products;
    }
}
