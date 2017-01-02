package ru.myocr.myocrandroid.ocr;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URL;


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

    public Mat applyCannySquareEdgeDetectionOnImage(Mat srcImage) {
        Mat destImage = downScaleImage(srcImage, 30);
        /*final Mat grayImage = cvCreateImage(cvGetSize(destImage), IPL_DEPTH_8U, 1);
        cvCvtColor(destImage, grayImage, CV_BGR2GRAY);
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        final Frame grayImageFrame = converterToMat.convert(grayImage);
        final Mat grayImageMat = converterToMat.convert(grayImageFrame);
        // apply gausian blur
        GaussianBlur(grayImageMat, grayImageMat,
                new Size(5, 5),
                0.0, 0.0, BORDER_DEFAULT);
        destImage = converterToMat.convertToMat(grayImageFrame);
        // clean it for better detection
        cvErode(destImage, destImage);
        cvDilate(destImage, destImage);
        // apply the canny edge detection method
        cvCanny(destImage, destImage, 75.0, 200.0);*/
        return destImage;
    }

    /*public CvSeq findLargestSquareOnCannyDetectedImage(Mat image) {
        final Mat foundedContoursImage = cvCloneImage(image);
        final CvMemStorage memory = CvMemStorage.create();
        final CvSeq contours = new CvSeq();
        cvFindContours(foundedContoursImage, memory, contours,
                Loader.sizeof(CvContour.class), CV_RETR_LIST,
                CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

        int maxWidth = 0;
        int maxHeight = 0;
        CvRect contour = null;
        CvSeq seqFounded = null;
        CvSeq nextSeq = new CvSeq();
        for (nextSeq = contours; nextSeq != null; nextSeq = nextSeq.h_next()) {
            contour = cvBoundingRect(nextSeq, 0);
            if ((contour.width() >= maxWidth) && (contour.height() >= maxHeight)) {
                maxWidth = contour.width();
                maxHeight = contour.height();
                seqFounded = nextSeq;
            }
        }
        final CvSeq result = cvApproxPoly(seqFounded, Loader.sizeof(CvContour.class),
                memory, CV_POLY_APPROX_DP,
                cvContourPerimeter(seqFounded) * 0.02, 0);
        for (int i = 0; i < result.total(); i++) {
            final CvPoint v = new CvPoint(cvGetSeqElem(result, i));
            cvDrawCircle(foundedContoursImage, v, 5,
                    CvScalar.BLUE, 20, 8, 0);
            System.out.println("found point(" + v.x()
                    + "," + v.y() + ")");
        }
        saveImage(foundedContoursImage, "contoursReceipt.jpg");
        return result;
    }

    public Mat applyPerspectiveTransformThresholdOnOriginalImage(Mat srcImage, CvSeq contour) {
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
