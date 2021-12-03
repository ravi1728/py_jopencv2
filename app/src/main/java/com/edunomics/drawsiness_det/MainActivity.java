package com.edunomics.drawsiness_det;

import static org.opencv.core.Core.ROTATE_90_COUNTERCLOCKWISE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, ActivityCompat.OnRequestPermissionsResultCallback {

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    int counter = 0;
    private static final String TAG = "MainActivityTAG";
    private static final int PERMISSION_REQUESTS = 1;
    Context context;
    String path;

//    static {
//       System.loadLibrary("drawsiness_det");
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = getApplicationContext();
        checkFirstRun();

        if (!Python.isStarted())
        {
            Python.start(new AndroidPlatform(this));
            Log.w(TAG,"Python started");
        }
        else
        {
            Log.w(TAG, "Python started");

        }

        Button button = findViewById(R.id.button1);


        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setCameraIndex(0);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);


        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }

            }

        };

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }



    }

    public final void saveMat(String path, Mat mat) {
        File file = new File(path).getAbsoluteFile();
        file.getParentFile().mkdirs();
        try {
            int cols = mat.cols();
            float[] data = new float[(int) mat.total() * mat.channels()];
            mat.get(0, 0, data);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(cols);
                oos.writeObject(data);
                oos.close();
            }
        } catch (IOException | ClassCastException ex) {
            System.err.println("ERROR: Could not save mat to file: " + path);
            Log.e("function_saveMatTAG", String.valueOf(ex));
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();
//        frame = frame.;

        Core.rotate(frame, frame, ROTATE_90_COUNTERCLOCKWISE);
        return frame;


//        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2BGR);
//
//        byte[] b = new byte[(int) (frame.total() * frame.channels())];
//        frame.get(0, 0,b);
//
//        int r=frame.cols();
//        int c=frame.rows();
//        int ch=frame.channels();
//        Log.d(TAG, "InitSize: " + String.valueOf(frame.total() * frame.channels()));
////        resources.openRawResource(R.raw.filename)
//
////        Log.d(TAG, "bytearraycreated");
////        prototxt = loadUrl("file:///android_res/MobileNetSSD_deploy.prototxt.txt");
//
//
//        Python python = Python.getInstance();
//        PyObject pythonFile = python.getModule("utils");
//
//        PyObject byteFrame = PyObject.fromJava(b);
//        PyObject result = pythonFile.callAttr("fun1",byteFrame, r ,c, ch);
////        PyObject.
//
//        Log.d(TAG, result.toString());
//        byte[] b1 = new byte[(int) (frame.total() * frame.channels())];
//        b1 = result.toJava(b.getClass());
//        frame.put(0, 0, b1);
//        Log.d(TAG, "FinalSize: " + String.valueOf(frame.total() * frame.channels()));
//
//
//        // try-catch block to handle exceptions
////        try {
////
////            // Create a file object
////            File f = new File(String.valueOf(result));
////
////            // Get all the names of the files present
////            // in the given directory
////            File[] files = f.listFiles();
////
//////            System.out.println("Files are:");
////
////            // Display the names of the files
////            for (int i = 0; i < files.length; i++) {
////                Log.d("File_" + TAG, files[i].getName());
////            }
////        }
////        catch (Exception e) {
////            System.err.println(e.getMessage());
////        }
//
//
////        try {
////            Files.list(new File(String.valueOf(result)).toPath())
////                    .limit(10)
////                    .forEach(path -> {
////                        Log.d(TAG, String.valueOf(path));
////                    });
////        } catch (IOException e) {
////            e.printStackTrace();
////            Log.d(TAG, String.valueOf(e));
////        }
////
////        //frame=(Mat)result;
////        Log.d(TAG, frame.size().toString());
////        Imgproc.putText(frame,
//////                result.toString(),
////                "CENTER",
////                new Point((int)1024/2, (int)768/2),               // point
////                Core.FONT_HERSHEY_SIMPLEX ,      // front face
////                10,                               // front scale
////                new Scalar(123, 210, 95),             // Scalar object for color
////                4
////        );
////
////        counter = counter + 1;
////        Log.d(TAG,"Frame Written");
//        return frame;
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            Log.d("function_checkFirstRunTAG", "NORMAL RUN");
            // This is just a normal run
//            try {
//                Log.d("function_checkFirstRunTAG", "new install");
//                _copydatabase();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("function_copydatabaseTAG", "ERROR _copydatabase" + String.valueOf(e));
//            }
            copyAssets();
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)
//            try {
//                Log.d("function_checkFirstRunTAG", "new install");
////                _copydatabase();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("function_copydatabaseTAG", "ERROR _copydatabase");
//            }
            copyAssets();

        }
//        else if (currentVersionCode > savedVersionCode) {
//
//            // TODO This is an upgrade
//        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

//    Context context = getApplicationContext();
    public final String modelName = "mobilenetssd_deploy.caffemodel";

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            Log.d("FileNameTAG", filename);
            try {
                in = assetManager.open(filename);
                File outFile = new File(context.getDataDir().getAbsolutePath(), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
//                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

//    public void _copydatabase() throws IOException {
////        context.getFilesDir()
////        context.getDataDir().getAbsolutePath()
//        path = context.getDataDir() + "/models/" ; //"/data/data/com.aliserver.shop/databases/";
//        Log.d("function_copydatabaseTAG", path);
//
//        File file = new File(path);
//        if(!file.exists())
//            file.createNewFile();
//            Log.d("function_copydatabaseTAG", "created new");
//
//
//
//        OutputStream myOutput = new FileOutputStream(path);
//        byte[] buffer = new byte[1024];
//        int length;
////        InputStream myInput = context.getAssets().open("mobilenetssd_deploy.caffemodel");
////        while ((length = myInput.read(buffer)) > 0) {
////            myOutput.write(buffer, 0, length);
////        }
////        myInput.close();
////        myOutput.flush();
////        myOutput.close();
//
//        InputStream myInput = context.getAssets().open("mobilenetssd_deploy.prototxt.txt");
//        while ((length = myInput.read(buffer)) > 0) {
//            myOutput.write(buffer, 0, length);
//        }
//        myInput.close();
//        myOutput.flush();
//        myOutput.close();
//
//    }

//    View.onCli\\\
//    @Override
    public void buttonClick(View view)
    {
        Uri selectedUri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW, selectedUri);
//        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            startActivity(intent);
        }
        else
        {
            Log.d("function_buttonClickTAG", "not opened filemanager");
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }


    @Override
    public void onCameraViewStopped() {

    }


    @Override
    protected void onResume() {
        super.onResume();



        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }
}
