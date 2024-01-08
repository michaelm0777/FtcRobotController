package org.firstinspires.ftc.teamcode.vision;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import android.graphics.*;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


public class colordetector extends OpenCvPipeline {
    private Telemetry telemetry;
    Mat frame = new Mat();
    Mat leftCrop;
    Mat centerCrop;
    Mat rightCrop;
    Mat outPut = new Mat();

    Scalar rectColor = new Scalar(255.0,0.0,0.0);
    private Paint boxPaint;
    private Paint textPaint;

    public colordetector(Telemetry telemetry) {
        this.telemetry = telemetry;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);

        boxPaint = new Paint();
        boxPaint.setColor(Color.BLACK);
        boxPaint.setStyle(Paint.Style.FILL);
    }
    @Override
    public Mat processFrame(Mat input) {
        telemetry.addData("[>]", "Default pipeline selected.");
        telemetry.update();

        Imgproc.cvtColor(input, frame, Imgproc.COLOR_RGB2YCrCb);
        // 160x120!!
        Rect leftRect = new Rect(0,200,160,120);
        Rect centerRect = new Rect(240,200,160,120);
        Rect rightRect = new Rect(480,200,160,120);

        input.copyTo(outPut);
        Imgproc.rectangle(outPut,leftRect, rectColor,1);
        Imgproc.rectangle(outPut,centerRect, rectColor,1);
        Imgproc.rectangle(outPut,rightRect, rectColor,1);

        leftCrop = frame.submat(leftRect);
        centerCrop = frame.submat(centerRect);
        rightCrop = frame.submat(rightRect);

        Core.extractChannel(leftCrop,leftCrop, 2);
        Core.extractChannel(centerCrop,centerCrop,2);
        Core.extractChannel(rightCrop,rightCrop, 2);
        Scalar leftavg = Core.mean(leftCrop);
        Scalar centeravg = Core.mean(centerCrop);

        Scalar rightavg = Core.mean(rightCrop);

        if(leftavg.val[0] > centeravg.val[0]){
            Imgproc.rectangle(outPut,leftRect, new Scalar(0,255,0),1);
        } else if (centeravg.val[0] > rightavg.val[0]) {
            Imgproc.rectangle(outPut,centerRect, new Scalar(0,255,0),1);
        } else{
            Imgproc.rectangle(outPut,rightRect, new Scalar(0,255,0),1);
        }
        return (outPut);
    }
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        canvas.drawRect(new android.graphics.Rect(0, 0, 385, 45), boxPaint);
        canvas.drawText("Default pipeline selected", 5, 33, textPaint); }
}