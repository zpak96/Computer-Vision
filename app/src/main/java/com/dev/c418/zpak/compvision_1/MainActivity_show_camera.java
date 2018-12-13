package com.dev.c418.zpak.compvision_1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

//OpenCV imports
import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity_show_camera extends AppCompatActivity implements CvCameraViewListener2 {

    //logging error messages
    private static final String LOG = "OCVSample::Activity";

    //this is for loading the camera view of OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    CascadeClassifier cascadeClassifier;
    int absoluteFaceSize;

    // for camera selection, like switching front or face cam
    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;

    //variables used to fix camera orientation
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(LOG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity_show_camera() {
        Log.i(LOG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.i(LOG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.show_camera);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    private void initializeOpenCVDependencies() {

        try {
            InputStream face_input = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascades", Context.MODE_PRIVATE);
            File faceCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream face_output = new FileOutputStream(faceCascadeFile);


            byte[] faceBuffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = face_input.read(faceBuffer)) != -1){
                face_output.write(faceBuffer, 0, bytesRead);
            }


            face_input.close();
            face_output.close();

            cascadeClassifier = new CascadeClassifier(faceCascadeFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e("MainActivity", "Error loading cascade", e);
        }

        //mOpenCvCameraView.enableView();

    }



    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(LOG, "Internal OpenCV library not found. Using OpenCV manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(LOG, "OpenCV library found inside package.");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void OnDestroy(){
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void OnPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height){

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        absoluteFaceSize = (int) (height * 0.2);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat faceDetection(Mat baseImage) {

        initializeOpenCVDependencies();

        Mat grayImage = new Mat();
        Imgproc.cvtColor(baseImage, grayImage, Imgproc.COLOR_RGB2GRAY);

        MatOfRect faces = new MatOfRect();

        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(grayImage, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(baseImage, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 255, 255), 3);
        }
        return baseImage;
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        //identify the intent that called
        Intent intent = getIntent();
        //grab the bundle
        Bundle extras = intent.getExtras();

        Integer effectNumber = extras.getInt("EFFECT_NUMBER");

        Mat baseImage = new Mat();

        baseImage = inputFrame.rgba();
        //default camera view
        mRgba = baseImage;

        //now we use the effect number to display
        switch (effectNumber) {
            case 0: //Default camera view
                mRgba = baseImage;
                break;
            case 1: // Grey scale camera view
                Imgproc.cvtColor(baseImage, mRgba, Imgproc.COLOR_BGR2GRAY);
                break;
            case 2: // Gaussian Blur camera view
                Imgproc.GaussianBlur(baseImage, mRgba,new Size(45,45), 0);
                break;
            case 3: // Laplacian
                Imgproc.Laplacian(baseImage,mRgba,-1);
                break;
            case 4: // Scharr
                Imgproc.Scharr(baseImage,mRgba,Imgproc.CV_SCHARR,0,1);
                break;
            case 5: // Pyrdown
                Imgproc.Sobel(baseImage,mRgba,-1,0,1);
                break;
            case 6: // Canny Edge detection
                Imgproc.cvtColor(baseImage, mRgba, Imgproc.COLOR_BGR2GRAY);
                Imgproc.Canny(baseImage,mRgba,50,150, 3, false);
                break;
            case 7: // Face Detection
                mRgba = faceDetection(baseImage);
                break;
        }

        // correct orientation
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        Core.flip(mRgbaF, mRgba, 1 );

        return mRgba; //this must return
    }
}
