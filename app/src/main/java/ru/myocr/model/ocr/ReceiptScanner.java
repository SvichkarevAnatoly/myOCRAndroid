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
        if (null == seqFounded) {
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

    public Mat drawLargestSquareOnCannyDetectedImage(Mat image, MatOfPoint largestSquare) {
        final Mat destImage = image.clone();
        List<Point> points = largestSquare.toList();
        for (int i = 0; i < largestSquare.total(); i++) {
            final Point v = points.get(i);
            Scalar blue = new Scalar(255, 0, 0);
            Imgproc.circle(destImage, v, 2, blue, 2, 8, 0);
        }
        return destImage;
    }

    public Mat findLines(Mat rgba, Mat grayImage, double param1, double param2) {
        Mat out = rgba.clone();
        Mat lines = new Mat();
        Imgproc.HoughLinesP(grayImage, lines, 1, Math.PI / 180, (int) (255 * 0.5), rgba.width() / 5, rgba.width() / 10);

        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Imgproc.line(out, start, end, new Scalar(0, 255, 0, 255), 2);// here initimg is the original image.
        }
        lines.release();
        return out;
    }

    public boolean canReduceTo4Dots(MatOfPoint contour) {
        double numberOfDots = contour.size().height;
        return numberOfDots >= 4;
    }

    public MatOfPoint reduceTo4Dots(MatOfPoint contour) {
        if (contour.size().height == 4) {
            return contour;
        }

        final int delta = 50;

        final List<Point> contourPoints = contour.toList();
        final boolean[] isNear = new boolean[contourPoints.size()];
        for (int i = 0; i < contourPoints.size(); i++) {
            for (int j = i + 1; j < contourPoints.size(); j++) {
                Point pi = contourPoints.get(i);
                Point pj = contourPoints.get(j);
                if (dist(pi, pj) < delta) {
                    isNear[j] = true;
                }
            }
        }

        final List<Point> fourContour = new ArrayList<>();
        for (int i = 0; i < contourPoints.size(); i++) {
            if (!isNear[i]) {
                fourContour.add(contourPoints.get(i));
            }
        }

        MatOfPoint matOfPoint = new MatOfPoint();
        matOfPoint.fromList(fourContour);
        return matOfPoint;
    }

    private int dist(Point pi, Point pj) {
        double xDiff = pi.x - pj.x;
        double yDiff = pi.y - pj.y;
        return (int) Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
    }

    public Mat transformPerspective(Mat srcImage, int resizePercent, MatOfPoint contour) {
        // first, given the percentage, adjust to the original image
        List<Point> contourPoints = contour.toList();
        for (Point p : contourPoints) {
            p.x = p.x * 100 / resizePercent;
            p.y = p.y * 100 / resizePercent;
        }

        // get each corner point of the image
        final Point topRightPoint = contourPoints.get(0);
        final Point topLeftPoint = contourPoints.get(1);
        final Point bottomLeftPoint = contourPoints.get(2);
        final Point bottomRightPoint = contourPoints.get(3);

        double resultWidth = topRightPoint.x - topLeftPoint.x;
        final double bottomWidht = bottomRightPoint.x - bottomLeftPoint.x;
        if (bottomWidht > resultWidth) {
            resultWidth = bottomWidht;
        }

        double resultHeight = bottomLeftPoint.y - topLeftPoint.y;
        final double bottomHeight = bottomRightPoint.y - topRightPoint.y;
        if (bottomHeight > resultHeight) {
            resultHeight = bottomHeight;
        }

        final float[][] srcFloats = {
                {(float) topLeftPoint.x, (float) topLeftPoint.y},
                {(float) topRightPoint.x, (float) topRightPoint.y},
                {(float) bottomLeftPoint.x, (float) bottomLeftPoint.y},
                {(float) bottomRightPoint.x, (float) bottomRightPoint.y}
        };
        final Mat srcMat = new Mat(4, 2, CvType.CV_32FC1);
        for (int i = 0; i < srcFloats.length; i++) {
            srcMat.put(i, 0, srcFloats[i]);
        }

        final float[][] destFloats = {
                {0, 0},
                {(float) resultWidth, 0},
                {0, (float) resultHeight},
                {(float) resultWidth, (float) resultHeight}
        };
        final Mat destMat = new Mat(4, 2, CvType.CV_32FC1);
        for (int i = 0; i < destFloats.length; i++) {
            destMat.put(i, 0, destFloats[i]);
        }

        final Mat homography = Imgproc.getPerspectiveTransform(srcMat, destMat);

        final Mat destImage = srcImage.clone();
        Size newSize = new Size(resultWidth, resultHeight);
        Imgproc.warpPerspective(srcImage, destImage, homography, newSize);

        return cropImage(destImage, 0, 0, resultWidth, resultHeight);
    }

    public Mat cropImage(Mat srcImage,
                         int fromX, int fromY,
                         double toWidth, double toHeight) {
        srcImage.adjustROI(fromX, fromY, srcImage.width() - (int) toWidth,
                srcImage.height() - (int) toHeight);
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
