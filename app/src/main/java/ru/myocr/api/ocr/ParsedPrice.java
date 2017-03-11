package ru.myocr.api.ocr;

import java.io.Serializable;

public class ParsedPrice implements Serializable {
    private String stringValue;
    private Integer intValue;

    public ParsedPrice(String stringValue) {
        this.stringValue = stringValue;
    }

    public ParsedPrice(String stringValue, int intValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Integer getIntValue() {
        return intValue;
    }
}
