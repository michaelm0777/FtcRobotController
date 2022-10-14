package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline



class Vision : OpMode() {
    var webcam = null;
    override fun init() {
        val webcamName = hardwareMap.get(WebcamName::class.java, "webcam1");
        val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
            "cameraMonitorViewId",
            "id",
            hardwareMap.appContext.packageName
        )
        var webcam = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);
        webcam.setPipeline(testPipeline());
        webcam.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() {
                webcam.startStreaming(640,360, OpenCvCameraRotation.UPRIGHT)
            }

            override fun onError(errorCode: Int) {
                /*
                * This will be called if the camera could not be opened
            */
            }
        })
    }

    override fun loop() {

    }
    class testPipeline : OpenCvPipeline() {
        var YCbCr = Mat();
        var leftCrop: Mat? = null;
        var rightCrop: Mat? = null;
        var leftavgfin: Double = 0.0;
        var rightavgfin: Double=0.0;
        var outPut = Mat();
        var rectColor = Scalar(255.0,0.0,0.0);
        override fun processFrame(input: Mat): Mat {
            Imgproc.cvtColor(input,YCbCr,Imgproc.COLOR_RGB2YCrCb);
            telemetry.addLine("Pipeline Running");

            var leftRect = Rect(1,1,319,359);
            var rightRect = Rect(320,1,319,359);

            input.copyTo(outPut);
            Imgproc.rectangle(outPut, leftRect, rectColor, 2);
            Imgproc.rectangle(outPut, rightRect, rectColor, 2);

            leftCrop = YCbCr.submat(leftRect);
            rightCrop = YCbCr.submat(rightRect);

            Core.extractChannel(leftCrop,leftCrop,2);
            Core.extractChannel(rightCrop,rightCrop,2);

            var leftavg = Core.mean(leftCrop);
            var rightavg = Core.mean(rightCrop);

            leftavgfin = leftavg.`val`[0];
            rightavgfin = rightavg.`val`[0];

            if(leftavgfin > rightavgfin){
                telemetry.addLine("Left");
            }
            else{
                telemetry.addLine("Right");
            }
            return outPut;
        }
    }
}
