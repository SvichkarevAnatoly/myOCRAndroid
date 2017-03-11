package ru.myocr.api.ocr;

import java.io.Serializable;
import java.util.List;

public class OcrReceiptResponse implements Serializable {
    private List<ReceiptItemMatches> itemMatches;
    private List<ParsedPrice> prices;

    public OcrReceiptResponse(List<ReceiptItemMatches> itemMatches, List<ParsedPrice> prices) {
        this.itemMatches = itemMatches;
        this.prices = prices;
    }

    public List<ReceiptItemMatches> getItemMatches() {
        return itemMatches;
    }

    public List<ParsedPrice> getPrices() {
        return prices;
    }
}
