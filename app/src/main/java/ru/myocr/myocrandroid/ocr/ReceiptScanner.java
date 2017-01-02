package ru.myocr.myocrandroid.ocr;

import android.graphics.Bitmap;

public interface ReceiptScanner {
    String getTextFromReceiptImage(Bitmap img);
}
