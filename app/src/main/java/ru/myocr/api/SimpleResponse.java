package ru.myocr.api;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SimpleResponse implements Serializable {

    @SerializedName("key")
    public String body;

}
