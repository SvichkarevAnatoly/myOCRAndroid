package ru.myocr.api;

import com.google.gson.annotations.SerializedName;

public class OcrResponse {
    @SerializedName("ocrText")
    public String ocrText;

    public OcrResponse(String ocrText) {
        this.ocrText = ocrText;
    }
}
