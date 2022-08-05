package com.example.android.remedicappml;

import static java.lang.Math.ceil;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class CropImage {

    private static final String TAG = "CropImage";
    private static final String VIDEO_DIRECTORY_NAME = "RemedicApp";
    private static final Integer TIME_LAG_VITALS = 10;

    // Freq + Timer variable
    private static long startTime = 0;

    // Arraylist
    private final ArrayList<Double> RedAvgList = new ArrayList<>();
    private final ArrayList<Double> BlueAvgList = new ArrayList<>();
    private final ArrayList<Double> GreenAvgList = new ArrayList<>();

    public ArrayList<Double> rAvgList = new ArrayList<>();
    public ArrayList<Double> bAvgList = new ArrayList<>();
    public ArrayList<Double> gAvgList = new ArrayList<>();

    private int counter = 0;

    private final ArrayList<Float> vitals = new ArrayList<>(2);
    public HashMap<String, HashMap<String, ArrayList<Double>>> data = new HashMap<>();

    public CropImage() {
        // Initialize vitals
        vitals.add(0, 0f);
        vitals.add(1, 0f);

        // Initialize data
        // Forehead
        HashMap<String, ArrayList<Double>> fmap = new HashMap<>();
        ArrayList<Double> fral = new ArrayList<>();
        ArrayList<Double> fbal = new ArrayList<>();
        ArrayList<Double> fgal = new ArrayList<>();

        fral.add(0.0); fbal.add(0.0); fgal.add(0.0);
        fmap.put("RedAvg", fral); fmap.put("BlueAvg", fbal); fmap.put("GreenAvg", fgal);
        data.put("forehead", fmap);

        // Left Cheek
        HashMap<String, ArrayList<Double>> lcmap = new HashMap<>();
        ArrayList<Double> lcral = new ArrayList<>();
        ArrayList<Double> lcbal = new ArrayList<>();
        ArrayList<Double> lcgal = new ArrayList<>();

        lcral.add(0.0); lcbal.add(0.0); lcgal.add(0.0);
        lcmap.put("RedAvg", lcral); lcmap.put("BlueAvg", lcbal); lcmap.put("GreenAvg", lcgal);
        data.put("leftCheek", lcmap);

        // Right Cheek
        HashMap<String, ArrayList<Double>> rcmap = new HashMap<>();
        ArrayList<Double> rcral = new ArrayList<>();
        ArrayList<Double> rcbal = new ArrayList<>();
        ArrayList<Double> rcgal = new ArrayList<>();

        rcral.add(0.0); rcbal.add(0.0); rcgal.add(0.0);
        rcmap.put("RedAvg", rcral); rcmap.put("BlueAvg", rcbal); rcmap.put("GreenAvg", rcgal);
        data.put("rightCheek", rcmap);

    }

    public ArrayList<Float> setVariables(ByteBuffer data, FrameMetadata frameMetadata,
                                          List<Face> faces) {
        Bitmap bitmap;
        for (Face face: faces) {
            if (data == null) {
                Log.d(TAG, "Image Byte Buffer was NULL !! ");
            } else {
                bitmap = BitmapUtils.getBitmap(data, frameMetadata);
                if (bitmap != null)
                    return convertToMatrix(bitmap, face);
            }
        }
        return vitals;
    }

    public HashMap<String, MatOfPoint> getFaceRegions(Face face) {
        HashMap<String, MatOfPoint> faceRegions = new HashMap<>();
        List<Point> foreheadPoints = new ArrayList<>();
        List<Point> leftCheekPoints = new ArrayList<>();
        List<Point> rightCheekPoints = new ArrayList<>();
        List<Point> lipsPoints = new ArrayList<>();

        MatOfPoint ffh = new MatOfPoint();
        MatOfPoint flc = new MatOfPoint();
        MatOfPoint frc = new MatOfPoint();

        int lo; int hi;

        // Draws all face contours.
        // Log.d(TAG, String.valueOf(face.getAllContours()));
        for (FaceContour contour : face.getAllContours()) {

            // Get forehead points
            if (contour.getFaceContourType() == 1) {
                foreheadPoints.add(new Point((int) contour.getPoints().get(2).x, (int) contour.getPoints().get(2).y));
                foreheadPoints.add(new Point((int) contour.getPoints().get(33).x, (int) contour.getPoints().get(33).y));
            }

            if (contour.getFaceContourType() == 2 || contour.getFaceContourType() == 4)
                foreheadPoints.add(new Point((int) contour.getPoints().get(2).x, (int) contour.getPoints().get(2).y));


            // Get left cheek points
            if (contour.getFaceContourType() == 1) {
                lo = 25; hi = 28;
                for (int i = lo; i <= hi; i++)
                    leftCheekPoints.add(new Point((int) contour.getPoints().get(i).x, (int) contour.getPoints().get(i).y));
            }

            if (contour.getFaceContourType() == 6) {
                lo = 9; hi = 15;
                for (int i = hi; i >= lo; i--)
                    leftCheekPoints.add(new Point((int) contour.getPoints().get(i).x, (int) contour.getPoints().get(i).y));
            }

            if (contour.getFaceContourType() == 13) {
                leftCheekPoints.add(new Point((int) contour.getPoints().get(0).x, (int) contour.getPoints().get(0).y));
            }

            // Get right cheek points
            if (contour.getFaceContourType() == 1) {
                lo = 7; hi = 11;
                for (int i = hi; i >= lo; i--)
                    rightCheekPoints.add(new Point((int) contour.getPoints().get(i).x, (int) contour.getPoints().get(i).y));
            }

            if (contour.getFaceContourType() == 7) {
                lo = 9; hi = 15;
                for (int i = lo; i <= hi; i++)
                    rightCheekPoints.add(new Point((int) contour.getPoints().get(i).x, (int) contour.getPoints().get(i).y));
            }

            if (contour.getFaceContourType() == 13) {
                rightCheekPoints.add(new Point((int) contour.getPoints().get(2).x, (int) contour.getPoints().get(2).y));
            }
        }

        ffh.fromList(foreheadPoints); faceRegions.put("forehead", ffh);
        flc.fromList(leftCheekPoints); faceRegions.put("leftCheek", flc);
        frc.fromList(rightCheekPoints); faceRegions.put("rightCheek", frc);
        return faceRegions;
    }

    private Mat getMask(Bitmap bitmap, MatOfPoint region) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat mask =  new Mat(new Size(src.cols(), src.rows()), CvType.CV_8UC1);
        mask.setTo(new Scalar(0.0));
        Scalar white = new Scalar(255, 255, 255);

        Imgproc.fillConvexPoly(mask, region, white);
        return mask;
    }

    private Double calculateMean(Mat src, Bitmap bitmap, HashMap<String,
            MatOfPoint> regions, String region) {

        Mat mask;
        ArrayList<Mat> dst = new ArrayList(3);
        Core.split(src, dst);

        mask = getMask(bitmap, regions.get(region));

        Scalar BlueAvg = Core.mean(dst.get(0), mask);
        Scalar GreenAvg = Core.mean(dst.get(1), mask);
        Scalar RedAvg = Core.mean(dst.get(2), mask);

        data.get(region).get("RedAvg").add(RedAvg.val[0]);
        data.get(region).get("BlueAvg").add(BlueAvg.val[0]);
        data.get(region).get("GreenAvg").add(GreenAvg.val[0]);

        return RedAvg.val[0];
    }

    public ArrayList<Float> convertToMatrix(Bitmap bitmap, Face face) {
        double SamplingFreq;
        HashMap<String, MatOfPoint> regions;
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        regions = getFaceRegions(face);

        Double red;
        red = calculateMean(src, bitmap, regions, "forehead");
        RedAvgList.add(red);

        red = calculateMean(src, bitmap, regions, "leftCheek");
        red = calculateMean(src, bitmap, regions, "rightCheek");
        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d; // To convert time to seconds
        counter++;

        if (totalTimeInSecs >= TIME_LAG_VITALS) {

            startTime = System.currentTimeMillis();
            SamplingFreq = (counter / totalTimeInSecs);
            Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);
            double HRFreq = FFT.FFT(Red, counter, SamplingFreq);
            Float bpm = (float) ceil(HRFreq * 60);

            // Reset the counter to start counting new frames
            counter = 0;

            // Clear the data points so far
            RedAvgList.clear();
            vitals.set(0, bpm);
            vitals.set(1, 0f);
        }
        return vitals;

         // fillArea(src, mPoints);
         // create a blank temp bitmap:
         // Bitmap tempBmp1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
         // bitmap.getConfig());
         // Utils.matToBitmap(mask, tempBmp1);
         // writeToDirectory(tempBmp1);
    }

    public void fillArea(Mat src, MatOfPoint points) {
        Scalar color = new Scalar(0, 0, 255);
        Imgproc.fillConvexPoly (src, points, color);
    }

    /*  Save the byte array to a local file
     *  Create directory and return file returning video file
     */
    public static File getOutputMediaFile() {
        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                VIDEO_DIRECTORY_NAME);
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + VIDEO_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMAGE_" + timeStamp + ".bmp");
        return mediaFile;
    }

    public void saveImage(MatOfPoint points, Mat src, Bitmap bitmap) {
        fillArea(src, points);
        Bitmap tempBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                                    bitmap.getConfig());
        Utils.matToBitmap(src, tempBmp);
        writeToDirectory(tempBmp);
    }

    public void writeToDirectory(Bitmap bm) {

        File file = getOutputMediaFile();
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not write to the storage file.", e);
        }
    }

}
