package ru.myocr.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindResponse {

    @SerializedName("match")
    public List<String> match;

    public FindResponse(List<String> match) {
        this.match = match;
    }
}
