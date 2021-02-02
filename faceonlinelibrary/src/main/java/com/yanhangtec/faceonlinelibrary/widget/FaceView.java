package com.yanhangtec.faceonlinelibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.IntRange;

import com.serenegiant.usb.widget.UVCCameraTextureView;
import com.yanhangtec.faceonlinelibrary.R;
import com.yanhangtec.faceonlinelibrary.handler.FaceHandler;
import com.yanhangtec.faceonlinelibrary.handler.UsbCameraHandler;
import com.yanhangtec.faceonlinelibrary.listener.OnFaceCallbackListener;

import java.io.File;
import java.util.UUID;


/**
 * 人脸识别采集View
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/27/21 4:54 PM
 */
public class FaceView extends BaseFaceLayout
        implements UsbCameraHandler.OnPreviewListener, FaceHandler.OnFaceListener {
    // 识别
    public final static int TYPE_IDENTIFY = 2001;
    // 注册
    public final static int TYPE_REGISTER = 2002;
    // 注册+识别
    public final static int TYPE_REGISTER_AND_IDENTIFY = 2003;

    private View mRoot;
    // 预览View
    private UVCCameraTextureView mPreviewView;
    // 原型切片
    private FaceRoundView mFaceRoundView;
    // usb连接处理器
    private UsbCameraHandler handler;

    // 人脸检测处理工具
    private FaceHandler faceHandler;
    // 绑定的Activity
    private Activity activity;

    private OnFaceCallbackListener.OnBaseListener listener;

    public FaceView(Context context) {
        this(context, null);
    }

    public FaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        faceHandler = new FaceHandler(context);
        initView();
        initFaceFrame();
    }

    /**
     * 初始化配置
     *
     * @param activity 绑定的Activity
     * @param type     处理类型，识别/注册/识别+注册
     */
    public void initConfig(Activity activity,
                           @IntRange(from = TYPE_IDENTIFY, to = TYPE_REGISTER_AND_IDENTIFY) int type) {
        initConfig(activity, type, UsbCameraHandler.TYPE_JPEG);
    }

    public void initConfig(Activity activity,
                           @IntRange(from = TYPE_IDENTIFY, to = TYPE_REGISTER_AND_IDENTIFY) int type,
                           String format) {
        initConfig(activity, type, format, 0, 0);
    }

    /**
     * 初始化配置
     *
     * @param activity  绑定的Activity
     * @param type      处理类型，识别/注册/识别+注册
     * @param format    摄像头格式 {UsbCameraHandler.TYPE_JPEG,UsbCameraHandler.TYPE_YUYV}
     * @param productId 产品编号
     * @param vendorId  供应商ID
     */
    public void initConfig(Activity activity,
                           @IntRange(from = TYPE_IDENTIFY, to = TYPE_REGISTER_AND_IDENTIFY) int type,
                           String format, int productId, int vendorId) {
        initConfig(activity, type, format, productId, vendorId,
                UUID.randomUUID().toString().replace("-", "_"));
    }

    /**
     * 初始化配置
     *
     * @param activity 绑定的Activity
     * @param type     处理类型，识别/注册/识别+注册
     * @param format   摄像头格式 {UsbCameraHandler.TYPE_JPEG,UsbCameraHandler.TYPE_YUYV}
     * @param userId   用户ID
     */
    public void initConfig(Activity activity,
                           @IntRange(from = TYPE_IDENTIFY, to = TYPE_REGISTER_AND_IDENTIFY) int type,
                           String format, String userId) {
        initConfig(activity, type, format, 0, 0, userId);
    }

    /**
     * 初始化配置
     *
     * @param activity  绑定的Activity
     * @param type      处理类型，识别/注册/识别+注册
     * @param format    摄像头格式 {UsbCameraHandler.TYPE_JPEG,UsbCameraHandler.TYPE_YUYV}
     * @param productId 产品编号
     * @param vendorId  供应商ID
     * @param userId    用户ID
     */
    public void initConfig(Activity activity,
                           @IntRange(from = TYPE_IDENTIFY, to = TYPE_REGISTER_AND_IDENTIFY) int type,
                           String format, int productId, int vendorId, String userId) {
        this.activity = activity;
        bindHandle(format, productId, vendorId);
        faceHandler.initManager();
        faceHandler.setHandleTypeAndUserId(type, userId);
        faceHandler.setListener(this);
    }

    public void setListener(OnFaceCallbackListener.OnBaseListener listener) {
        this.listener = listener;
    }

    /**
     * 初始化View
     */
    private void initView() {
        mRoot = inflate(getContext(), R.layout.layout_face_view, this);

        mPreviewView = mRoot.findViewById(R.id.preview_view);
        mFaceRoundView = mRoot.findViewById(R.id.rect_view);
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ViewGroup.LayoutParams params = mFaceRoundView.getLayoutParams();
                params.height = mPreviewView.getMeasuredHeight();
                topHeight = mPreviewView.getTop();
                mFaceRoundView.setLayoutParams(params);

            }
        });

        initFaceFrame(getContext());

    }

    /**
     * 绑定处理器，用于预览
     */
    private void bindHandle(String format, int productId, int vendorId) {
        if (mPreviewView == null)
            return;

        if (activity == null)
            return;


        handler = new UsbCameraHandler(mPreviewView, activity,
                productId, vendorId, format, this);
    }

    /**
     * 初始化人脸框
     */
    public void initFaceFrame() {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        faceFrameImg = new ImageView(getContext());
        addView(faceFrameImg, lp);
        faceFrameImg.setVisibility(VISIBLE);
    }

    /**
     * 启动恢复摄像头
     */
    public void resume() {
        if (handler != null) {
            handler.start();
        }

        if (faceHandler != null) {
            faceHandler.start();
        }
    }

    /**
     * 开启识别
     */
    public void startIdentify() {
        if (handler != null) {
            handler.setNeedCallback(true);
        }

        if (faceHandler != null) {
            faceHandler.startIdentify();
        }
    }

    /**
     * 停止识别
     */
    public void stopIdentify() {
        if (handler != null) {
            handler.setNeedCallback(false);
        }

        if (faceHandler != null) {
            faceHandler.stopIdentify();
        }
    }

    /**
     * 停止摄像头
     */
    public void pause() {
        if (faceHandler != null) {
            faceHandler.stop();
        }

        if (handler != null) {
            handler.stop();
        }

    }

    /**
     * 预览数据转发,将摄像头数据转发到人脸识别上
     *
     * @param bytes 预览数据
     */
    @Override
    public void onPreviewResult(byte[] bytes) {
        if (faceHandler != null) {
            faceHandler.dealPreviewResult(bytes);
        }
    }

    @Override
    public void onFaceDescResult(String message) {
        if (listener != null) {
            listener.onIdentifyState(message);
        }
    }

    @Override
    public void onIdentifyResult(String result, boolean isSuccess, String userId, double score) {
        if(isSuccess){
            stopIdentify();
        }
        if (listener != null && listener instanceof OnFaceCallbackListener.OnFaceIdentifyListener) {
            ((OnFaceCallbackListener.OnFaceIdentifyListener) listener).
                    onIdentifyResult(result, isSuccess, userId, score);
        }
    }

    @Override
    public void onRegisterResult(File file, boolean isSuccess, String userId, String message) {
        if(isSuccess){
            stopIdentify();
        }
        if (listener != null && listener instanceof OnFaceCallbackListener.OnFaceRegisterListener) {
            ((OnFaceCallbackListener.OnFaceRegisterListener) listener).
                    onRegisterResult(file, isSuccess, userId, message);
        }
    }

    @Override
    public void onFaceFrame(int[] pts) {
        onDrawFaceFrame(pts);
    }

    /**
     * 销毁
     */
    public void destroy() {
        listener = null;
        if (faceHandler != null) {
            faceHandler.destroy();
        }

        if (handler != null) {
            handler.setPreviewListener(null);
            handler.destroy();
        }
        handler = null;
        activity = null;
    }
}
