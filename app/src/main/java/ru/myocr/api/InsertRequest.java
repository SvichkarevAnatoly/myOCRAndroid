package ru.myocr.api;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InsertRequest {

    @SerializedName("productDataSets")
    public List<ProductDataSet> products;

    public InsertRequest(List<ProductDataSet> products) {
        this.products = products;
    }

    public static class ProductDataSet {

        @SerializedName("name")
        public String name;
        @SerializedName("price")
        public String price;

        public ProductDataSet(String name, String price) {
            this.name = name;
            this.price = price;
        }
    }
}
