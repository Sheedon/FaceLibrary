package com.yanhangtec.faceonlinelibrary.handler;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.view.Surface;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.serenegiant.usb.widget.UVCCameraTextureView;

import java.util.List;

/**
 * USB摄像头采集处理器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/27/21 5:19 PM
 */
public class UsbCameraHandler implements CameraViewInterface.Callback {

    public static final String TYPE_JPEG = "FRAME_FORMAT_JPEG";
    public static final String TYPE_YUYV = "FRAME_FORMAT_YUYV";

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;

    private boolean isRequest;
    private boolean isPreview;
    private boolean isDispatch;
    private UsbDevice lastDevice;

    private int productId, vendorId;

    private OnPreviewListener previewListener;

    private boolean needCallback = true;

    public UsbCameraHandler(UVCCameraTextureView mTextureView, Activity activity) {
        this(mTextureView, activity, 0, 0, null);
    }

    public UsbCameraHandler(UVCCameraTextureView mTextureView, Activity activity, OnPreviewListener listener) {
        this(mTextureView, activity, 0, 0, listener);
    }

    public UsbCameraHandler(UVCCameraTextureView mTextureView, Activity activity,
                            int productId, int vendorId, OnPreviewListener listener) {
        this(mTextureView, activity, productId, vendorId, TYPE_YUYV, listener);
    }


    public UsbCameraHandler(UVCCameraTextureView mTextureView, Activity activity,
                            int productId, int vendorId, String format, OnPreviewListener previewListener) {
        this.productId = productId;
        this.vendorId = vendorId;
        this.previewListener = previewListener;
        mUVCCameraView = mTextureView;
        mUVCCameraView.setCallback(this);

        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(format != null && format.equals(TYPE_JPEG)
                ? UVCCameraHelper.FRAME_FORMAT_MJPEG : UVCCameraHelper.FRAME_FORMAT_YUYV);
        mCameraHelper.initUSBMonitor(activity, mUVCCameraView, listener);
        mCameraHelper.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {

                if (!needCallback)
                    return;

                if (UsbCameraHandler.this.previewListener != null) {
                    UsbCameraHandler.this.previewListener.onPreviewResult(nv21Yuv);
                }
            }
        });
    }


    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission
            if (!isRequest) {
                isRequest = true;
                openPreview(device);
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                isPreview = false;
            } else {
                isPreview = true;
                isDispatch = false;
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
        }
    };

    /**
     * 启动
     */
    public void start() {
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
            startAndSelect();
        }
    }

    /**
     * 启动选择usb设备
     */
    private void startAndSelect() {
        List<UsbDevice> deviceList = mCameraHelper.getUSBMonitor().getDeviceList();
        for (UsbDevice usbDevice : deviceList) {
            if (usbDevice != null &&
                    productId != 0 && usbDevice.getProductId() == productId
                    && vendorId != 0 && usbDevice.getVendorId() == vendorId) {
                openPreview(usbDevice);
                return;
            }
        }
    }

    /**
     * 开启预览,需求解决主动选择指定ID的设备
     *
     * @param device usb设备
     */
    private void openPreview(final UsbDevice device) {
        if (isDispatch)
            return;

        if (device == null
                || (vendorId != 0 && device.getVendorId() != vendorId)
                || (productId != 0 && device.getProductId() != productId))
            return;


        synchronized (UsbCameraHandler.class) {

            if (isDispatch)
                return;

            isDispatch = true;

            if (lastDevice == device ||
                    (lastDevice != null
                            && lastDevice.getVendorId() == device.getVendorId()
                            && lastDevice.getProductId() == device.getProductId())) {
                return;
            }

            lastDevice = device;

            if (mCameraHelper != null) {
                mCameraHelper.getUSBMonitor().requestPermission(device);
            }
        }

    }

    /**
     * 设置预览消息监听器
     *
     * @param previewListener 监听器
     */
    public void setPreviewListener(OnPreviewListener previewListener) {
        this.previewListener = previewListener;
    }

    /**
     * 设置是否需要预览反馈
     *
     * @param needCallback 是否需要反馈
     */
    public void setNeedCallback(boolean needCallback) {
        this.needCallback = needCallback;
    }

    /**
     * 停止预览
     */
    public void stop() {
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
            mCameraHelper.stopPreview();
        }
        isDispatch = false;
        previewListener = null;
        lastDevice = null;
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        FileUtils.releaseFile();
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
        mCameraHelper = null;

        if (mUVCCameraView != null) {
            mUVCCameraView.setCallback(null);
        }
        mUVCCameraView = null;
        previewListener = null;
        lastDevice = null;
    }

    public interface OnPreviewListener {
        void onPreviewResult(byte[] bytes);
    }
}
