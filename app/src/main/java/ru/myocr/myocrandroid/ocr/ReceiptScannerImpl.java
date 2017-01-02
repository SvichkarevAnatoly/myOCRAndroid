package ru.myocr.myocrandroid.ocr;

import android.content.Context;
import android.content.res.AssetManager;
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


public class ReceiptScannerImpl implements ReceiptScanner {

    private static final String TAG = "myOcr";

    public String getTextFromReceiptImage(Bitmap img) {
        final Mat Mat = loadImage(img);
        return null;
    }

    public Mat loadImage(Bitmap img) {
        Mat mat = new Mat();
        Utils.bitmapToMat(img, mat);
        return mat;
    }

    public void saveImage(Mat image, String fileName) {
        // final String folder = System.getProperty("user.dir");
        // final String filePath = folder + File.separator + fileName;
        //final File imageFile = new File(fileName);
        //cvSaveImage(imageFile.getAbsolutePath(), image);
    }

    public Mat downScaleImage(Mat srcImage, int percent) {
        System.out.println("srcImage - height - " + srcImage.height()
                + ", width - " + srcImage.width());
        Mat destImage = new Mat();
        //cvResize(srcImage, destImage);
        //Core.re
        Imgproc.resize(srcImage, destImage, new Size((srcImage.width() * percent) / 100,
                (srcImage.height() * percent) / 100));
        //core
        System.out.println("destImage - height - " + destImage.height()
                + ", width - " + destImage.width());
        return destImage;
    }

    public Mat applyCannySquareEdgeDetectionOnImage(Mat srcImage, double value) {
        // Mat destImage = downScaleImage(srcImage, 30);
        //final Mat grayImage = cvCreateImage(cvGetSize(destImage), IPL_DEPTH_8U, 1);
        final Mat grayImage = new Mat();//srcImage.clone();
        //cvCvtColor(destImage, grayImage, CV_BGR2GRAY);
        Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        /*OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        final Frame grayImageFrame = converterToMat.convert(grayImage);
        final Mat grayImageMat = converterToMat.convert(grayImageFrame);*/
        // apply gausian blur
        /*GaussianBlur(grayImageMat, grayImageMat,
                new Size(5, 5),
                0.0, 0.0, BORDER_DEFAULT);*/
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5),
                0.0, 0.0, BORDER_DEFAULT);
        //destImage = converterToMat.convertToMat(grayImageFrame);
        // clean it for better detection
        //cvErode(destImage, destImage);
        Imgproc.erode(grayImage, grayImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));
        // cvDilate(destImage, destImage);
        Imgproc.dilate(grayImage, grayImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));
        // apply the canny edge detection method
        // cvCanny(destImage, destImage, 75.0, 200.0);
        Imgproc.Canny(grayImage, grayImage, (int)(255.0 * value), 200.0);
        return grayImage;
    }

    public MatOfPoint findLargestSquareOnCannyDetectedImage(Mat image) {
        /*final Mat foundedContoursImage = cvCloneImage(image);
        final CvMemStorage memory = CvMemStorage.create();*/
        final List<MatOfPoint> contours = new ArrayList<>();
        /*cvFindContours(foundedContoursImage, memory, contours,
                Loader.sizeof(CvContour.class), CV_RETR_LIST,
                CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));*/
        Mat hierarchy = new Mat();
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int maxWidth = 0;
        int maxHeight = 0;
        Rect contour = null;
        MatOfPoint seqFounded = null;
        List<MatOfPoint> nextSeq = new ArrayList<>();
        //for (nextSeq = contours; nextSeq != null; nextSeq = nextSeq.h_next()) {
        for (MatOfPoint m : contours){
            //contour = cvBoundingRect(nextSeq, 0);
            contour = Imgproc.boundingRect(m);
            if ((contour.width >= maxWidth) && (contour.height >= maxHeight)) {
                maxWidth = contour.width;
                maxHeight = contour.height;
                seqFounded = m;
            }
        }

        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint out = new MatOfPoint();
        MatOfPoint2f out2f = new MatOfPoint2f();

        seqFounded.convertTo(thisContour2f, CvType.CV_32FC2);

        Imgproc.approxPolyDP(thisContour2f, out2f, Imgproc.arcLength(thisContour2f, true) * 0.02, false);

        out2f.convertTo(out, CvType.CV_32S);

        return out;
    }

    public void drawLargestSquareOnCannyDetectedImage(Mat image, MatOfPoint largestSquare){
        //final Mat foundedContoursImage = image.clone();

/*
        final List<MatOfPoint> result = ApproxPoly(seqFounded, Loader.sizeof(CvContour.class),
                memory, CV_POLY_APPROX_DP,
                cvContourPerimeter(seqFounded) * 0.02, 0);
*/
        List<Point> points = largestSquare.toList();
        for (int i = 0; i < largestSquare.total(); i++) {

            final Point v = points.get(i);//new Point(cvGetSeqElem(result, i));
            //cvDrawCircle(foundedContoursImage, v, 5,
            //        CvScalar.BLUE, 20, 8, 0);
            Scalar blue = new Scalar(255);
            Imgproc.circle(image, v, 5, blue, 20, 8, 0);
            /*System.out.println("found point(" + v.x
                    + "," + v.y + ")");*/
        }
        // saveImage(foundedContoursImage, "contoursReceipt.jpg");
    }

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
        //cvSetImageROI(srcImage, cvRect(fromX, fromY, toWidth, toHeight));
        srcImage.adjustROI(fromX, fromY, srcImage.width() - toWidth,
                srcImage.height() - toHeight);
        final Mat destImage = srcImage.clone();
        //cvCopy(srcImage, destImage); // TODO: 17.12.16 why?

        return destImage;
    }

    public Mat cleanImageSmoothingForOCR(Mat srcImage) {
        //final Mat destImage = new Mat(cvGetSize(srcImage), IPL_DEPTH_8U, 1);
        final Mat destImage = new Mat(srcImage.size(), CvType.CV_8U);
        //cvCvtColor(srcImage, destImage, CV_BGR2GRAY);
        Imgproc.cvtColor(srcImage, destImage, Imgproc.COLOR_BGR2GRAY);
        //cvSmooth(destImage, destImage, CV_MEDIAN, 3, 0, 0, 0);
        Imgproc.medianBlur(destImage, destImage, 3);
        //cvThreshold(destImage, destImage, 0, 255, CV_THRESH_OTSU);
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
            /*api.SetVariable("tessedit_char_whitelist",
                    "1234567890");*/

            // Initialize tesseract-ocr
            // download https://github.com/tesseract-ocr/tessdata/blob/master/rus.traineddata
            //final String folder = System.getProperty("user.dir");
            //context.getResources().getAssets().openFd("dfsdf").
            String folder = "file:///android_asset/raw/";
            if (api.init(folder, "rus")) {
                Log.e(TAG, "Could not initialize tesseract.");
            }

            // Open input image with leptonica library
            //final PIX image = api. pixRead(pathToReceiptImageFile);
            //api.SetImage(image);
            api.setImage(img);
            // Get OCR result
            //String outText = api.getUTF8Text();
            final String string = api.getUTF8Text();;

            // Destroy used object and release memory
            api.end();
            //outText.deallocate();
            //pixDestroy(image);

            return string;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
