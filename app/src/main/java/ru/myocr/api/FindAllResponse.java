package ru.myocr.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindAllResponse {

    @SerializedName("match")
    public List<String> match;

    public FindAllResponse(List<String> match) {
        this.match = match;
    }
}
