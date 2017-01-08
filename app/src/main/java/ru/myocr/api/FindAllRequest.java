package ru.myocr.api;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindAllRequest {

    @SerializedName("products")
    public List<String> products;

    public FindAllRequest(List<String> products) {
        this.products = products;
    }
}
