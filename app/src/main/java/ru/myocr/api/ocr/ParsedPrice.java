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

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }
}
