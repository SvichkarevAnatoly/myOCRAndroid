package ru.myocr.model.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.BORDER_DEFAULT;


public class ReceiptScanner {

    private static final String TAG = "myOcr";

    public Mat loadImage(Bitmap img) {
        Mat mat = new Mat();
        Utils.bitmapToMat(img, mat);
        return mat;
    }

    public Mat downScaleImage(Mat srcImage, int percent) {
        Mat destImage = new Mat();
        Imgproc.resize(srcImage, destImage, new Size((srcImage.width() * percent) / 100,
                (srcImage.height() * percent) / 100));
        return destImage;
    }

    public Mat applyCannySquareEdgeDetectionOnImage(Mat srcImage, double param1, double param2) {
        final Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5),
                0.0, 0.0, BORDER_DEFAULT);
        Imgproc.erode(grayImage, grayImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Imgproc.dilate(grayImage, grayImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Imgproc.Canny(grayImage, grayImage, (int) (255.0 * param1), (int) (255.0 * param2));
        return grayImage;
    }

    public MatOfPoint findLargestSquareOnCannyDetectedImage(Mat image) {
        final List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int maxWidth = 0;
        int maxHeight = 0;
        Rect contour = null;
        MatOfPoint seqFounded = null;
        for (MatOfPoint m : contours) {
            contour = Imgproc.boundingRect(m);
            if ((contour.width >= maxWidth) && (contour.height >= maxHeight)) {
                maxWidth = contour.width;
                maxHeight = contour.height;
                seqFounded = m;
            }
        }
        if (null == seqFounded){
            return new MatOfPoint();
        }
        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint out = new MatOfPoint();
        MatOfPoint2f out2f = new MatOfPoint2f();

        seqFounded.convertTo(thisContour2f, CvType.CV_32FC2);

        Imgproc.approxPolyDP(thisContour2f, out2f, Imgproc.arcLength(thisContour2f, true) * 0.02, false);

        out2f.convertTo(out, CvType.CV_32S);

        return out;
    }

    public void drawLargestSquareOnCannyDetectedImage(Mat image, MatOfPoint largestSquare) {
        List<Point> points = largestSquare.toList();
        for (int i = 0; i < largestSquare.total(); i++) {
            final Point v = points.get(i);
            Scalar blue = new Scalar(255);
            Imgproc.circle(image, v, 5, blue, 20, 8, 0);
        }
    }

    // TODO: uncomment and use android version of opencv
    /*public Mat applyPerspectiveTransformThresholdOnOriginalImage(Mat srcImage, CvSeq contour) {
        final Mat warpImage = cvCloneImage(srcImage);

        // first, given the percentage, adjust to the original image
        for (int i = 0; i < contour.total(); i++) {
            final CvPoint p = new CvPoint(cvGetSeqElem(contour, i));
            p.x(p.x() * 100 / 30);
            p.y(p.y() * 100 / 30);
        }

        // get each corner point of the image
        final CvPoint topRightPoint = new CvPoint(cvGetSeqElem(contour, 0));
        final CvPoint topLeftPoint = new CvPoint(cvGetSeqElem(contour, 1));
        final CvPoint bottomLeftPoint = new CvPoint(cvGetSeqElem(contour, 2));
        final CvPoint bottomRightPoint = new CvPoint(cvGetSeqElem(contour, 3));

        int resultWidth = topRightPoint.x() - topLeftPoint.x();
        final int bottomWidht = bottomRightPoint.x() - bottomLeftPoint.x();
        if (bottomWidht > resultWidth) {
            resultWidth = bottomWidht;
        }

        int resultHeight = bottomLeftPoint.y() - topLeftPoint.y();
        final int bottomHeight = bottomRightPoint.y() - topRightPoint.y();
        if (bottomHeight > resultHeight) {
            resultHeight = bottomHeight;
        }

        float[] sourcePoints = {
                topLeftPoint.x(), topLeftPoint.y(),
                topRightPoint.x(), topRightPoint.y(),
                bottomLeftPoint.x(), bottomLeftPoint.y(),
                bottomRightPoint.x(), bottomRightPoint.y()
        };
        float[] destinationPoints = {
                0, 0, resultWidth,
                0, 0, resultHeight,
                resultWidth, resultHeight
        };

        final CvMat homography = cvCreateMat(3, 3, CV_32FC1);
        cvGetPerspectiveTransform(sourcePoints, destinationPoints, homography);

        System.out.println(homography.toString());

        final Mat destImage = cvCloneImage(warpImage);
        cvWarpPerspective(warpImage, destImage, homography,
                CV_INTER_LINEAR, CvScalar.ZERO);

        return cropImage(destImage,
                0, 0, resultWidth, resultHeight);
    }*/

    public Mat cropImage(Mat srcImage,
                         int fromX, int fromY,
                         int toWidth, int toHeight) {
        srcImage.adjustROI(fromX, fromY, srcImage.width() - toWidth,
                srcImage.height() - toHeight);
        final Mat destImage = srcImage.clone();

        return destImage;
    }

    public Mat cleanImageSmoothingForOCR(Mat srcImage) {
        final Mat destImage = new Mat(srcImage.size(), CvType.CV_8U);
        Imgproc.cvtColor(srcImage, destImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(destImage, destImage, 3);
        Imgproc.threshold(destImage, destImage, 0, 255, Imgproc.THRESH_OTSU);
        return destImage;
    }

    public String getStringFromImage(Bitmap img, Context context) {
        try {
            final URL tessDataResource = getClass().getResource("/");
            final File tessFolder = new File(tessDataResource.toURI());
            final String tessFolderPath = tessFolder.getAbsolutePath();

            System.out.println(tessFolderPath);

            //BytePointer outText;
            final TessBaseAPI api = new TessBaseAPI();

            // Initialize tesseract-ocr
            // download https://github.com/tesseract-ocr/tessdata/blob/master/rus.traineddata
            String folder = "file:///android_asset/raw/";
            if (api.init(folder, "rus")) {
                Log.e(TAG, "Could not initialize tesseract.");
            }

            // Open input image with leptonica library
            api.setImage(img);
            // Get OCR result
            final String string = api.getUTF8Text();

            // Destroy used object and release memory
            api.end();

            return string;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
