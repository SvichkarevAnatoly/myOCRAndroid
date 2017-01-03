package ru.myocr.model.ocr;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ImageHistory {
    private List<Mat> images = new ArrayList<>();

    public ImageHistory(Mat image) {
        add(image);
    }

    public Mat getLast(boolean isRepeated) {
        return isRepeated ? getPrevious() : getLast();
    }

    public void change(Mat newImage, boolean isRepeated) {
        if (isRepeated) {
            replaceLast(newImage);
        } else {
            add(newImage);
        }
    }

    private Mat getPrevious() {
        int lastIndex = images.size() - 2;
        lastIndex = lastIndex < 0 ? 0 : lastIndex;
        return images.get(lastIndex);
    }

    private Mat getLast() {
        int lastIndex = images.size() - 1;
        return images.get(lastIndex);
    }

    private void replaceLast(Mat image) {
        int lastIndex = images.size() - 1;
        images.set(lastIndex, image);
    }

    private void add(Mat image) {
        images.add(image);
    }
}
