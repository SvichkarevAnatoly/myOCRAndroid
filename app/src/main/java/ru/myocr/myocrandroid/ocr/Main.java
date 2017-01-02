package ru.myocr.myocrandroid.ocr;


import org.opencv.core.Mat;

public class Main {
    public static void main(String[] args) {
        // set working directory to resource
        final ReceiptScannerImpl scanner = new ReceiptScannerImpl();
        final Mat image = scanner.loadImage("receipt.jpg");
        
        final Mat smallImage = scanner.downScaleImage(image, 30);
        scanner.saveImage(smallImage, "smallReceipt.jpg");

        final Mat cannyImage = scanner.applyCannySquareEdgeDetectionOnImage(image);
        scanner.saveImage(cannyImage, "cannyReceipt.jpg");

        /*final opencv_core.CvSeq contour = scanner.findLargestSquareOnCannyDetectedImage(cannyImage);
        final Mat rotatedImage = scanner.applyPerspectiveTransformThresholdOnOriginalImage(image, contour);
        scanner.saveImage(rotatedImage, "rotatedReceipt.jpg");

        final Mat prismaImage = scanner.loadImage("prisma.jpg");
        final Mat cleanedImage = scanner.cleanImageSmoothingForOCR(prismaImage);
        scanner.saveImage(cleanedImage, "cleanedPrisma.jpg");

        final String textFromReceiptImage = scanner.getStringFromImage("cleanedPrisma.jpg");
        System.out.println(textFromReceiptImage);*/
    }
}
