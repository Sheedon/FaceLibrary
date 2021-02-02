package com.yanhangtec.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yanhangtec.faceonlinelibrary.client.FaceClient;
import com.yanhangtec.faceonlinelibrary.handler.UsbCameraHandler;
import com.yanhangtec.faceonlinelibrary.listener.OnFaceCallbackListener;
import com.yanhangtec.faceonlinelibrary.listener.OnResultListener;
import com.yanhangtec.faceonlinelibrary.model.baidu.AccessTokenModel;
import com.yanhangtec.faceonlinelibrary.model.FaceError;
import com.yanhangtec.faceonlinelibrary.widget.FaceView;
import com.yanhangtec.faceonlinefactoryapp.R;

import java.io.File;

public class MainActivity extends AppCompatActivity implements OnFaceCallbackListener.OnFaceIdentifyListener ,
        OnFaceCallbackListener.OnFaceRegisterListener {

    private FaceView faceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        faceView = findViewById(R.id.view_face);
        faceView.initConfig(this, FaceView.TYPE_IDENTIFY, UsbCameraHandler.TYPE_JPEG,
                22656, 3016, "11111");
        faceView.setListener(this);

    }

    public void onStartClick(View view) {
        faceView.resume();
    }

    public void onStopClick(View view) {
        faceView.pause();
    }

    public void onDestroyClick(View view) {
        faceView.destroy();
    }

    public void onUpdateClick(View view) {

        FaceClient.getInstance().initConfig(this, new OnResultListener<AccessTokenModel>() {
            @Override
            public void onResult(AccessTokenModel result) {
                Log.v("SXD", "result" + result.toString());
            }

            @Override
            public void onError(FaceError error) {
                Log.v("SXD", "error" + error.toString());
            }
        });


    }

    @Override
    public void onIdentifyState(String desc) {
        Log.v("MainActivity", desc);
    }

    @Override
    public void onIdentifyResult(String result, boolean isSuccess, String userId, double score) {
        Log.v("MainActivity", "result:" + result);
        Log.v("MainActivity", "isSuccess:" + isSuccess);
        Log.v("MainActivity", "userId:" + userId);
        Log.v("MainActivity", "score:" + score);
    }

    @Override
    public void onRegisterResult(File file, boolean isSuccess, String userId, String message) {
        Log.v("MainActivity", "file:" + file);
        Log.v("MainActivity", "isSuccess:" + isSuccess);
        Log.v("MainActivity", "userId:" + userId);
        Log.v("MainActivity", "message:" + message);
    }
}