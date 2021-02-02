package com.yanhangtec.faceonlinelibrary.handler;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntRange;

import com.baidu.aip.FaceDetector;
import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.ArgbPool;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.idl.facesdk.FaceInfo;
import com.serenegiant.usb.UVCCamera;
import com.yanhangtec.faceonlinelibrary.FaceConstance;
import com.yanhangtec.faceonlinelibrary.R;
import com.yanhangtec.faceonlinelibrary.client.FaceClient;
import com.yanhangtec.faceonlinelibrary.listener.OnResultListener;
import com.yanhangtec.faceonlinelibrary.model.FaceError;
import com.yanhangtec.faceonlinelibrary.model.baidu.UserModel;
import com.yanhangtec.faceonlinelibrary.utils.FileUtils;
import com.yanhangtec.faceonlinelibrary.widget.FaceView;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 人脸处理器，用于注册，识别等操作
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/28/21 4:48 PM
 */
public class FaceHandler implements FaceDetectManager.OnFaceDetectListener, FaceFilter.OnTrackListener {

    private static final double ANGLE = 45;

    // 人脸检测管理器
    private FaceDetectManager faceDetectManager;
    private ArgbPool argbPool = new ArgbPool();
    private Context context;

    // 是否识别
    private boolean isIdentify = true;
    // 是否上报
    private boolean uploading = false;

    // 是否在读取信息
    private AtomicBoolean isReading = new AtomicBoolean(false);

    // 默认尺寸
    private final static int mScreenW = UVCCamera.DEFAULT_PREVIEW_WIDTH;
    private final static int mScreenH = UVCCamera.DEFAULT_PREVIEW_HEIGHT;

    // 用户ID
    private String userId;
    // 处理类型
    private int handleType;

    // 核实次数
    private int checkIndex = 0;

    private OnFaceListener listener;

    public FaceHandler(Context context) {
        this.context = context;
        faceDetectManager = new FaceDetectManager(context);
    }

    public void initManager() {
        faceDetectManager.setOnFaceDetectListener(this);
        faceDetectManager.setOnTrackListener(this);
    }

    /**
     * 设置处理类型和用户ID
     *
     * @param handleType 处理类型
     * @param userId     用户ID
     */
    public void setHandleTypeAndUserId(@IntRange(from = FaceView.TYPE_IDENTIFY,
            to = FaceView.TYPE_REGISTER_AND_IDENTIFY) int handleType, String userId) {
        this.handleType = handleType;
        this.userId = userId;
    }

    /**
     * 设置监听器
     *
     * @param listener 人脸数据反馈监听器
     */
    public void setListener(OnFaceListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (faceDetectManager != null)
            faceDetectManager.startLocal();
    }

    /**
     * 启动识别
     */
    public void startIdentify() {
        isIdentify = true;
        uploading = false;
    }

    /**
     * 调度预览结果字符数据
     *
     * @param bytes 人脸数据
     */
    public void dealPreviewResult(byte[] bytes) {
        if (!isIdentify)
            return;

        synchronized (FaceHandler.class) {
            if (!isIdentify)
                return;
        }

        if (isReading.get())
            return;

        isReading.set(true);

        int[] argb = argbPool.acquire(mScreenW, mScreenH);

        if (argb == null || argb.length != mScreenW * mScreenH) {
            argb = new int[mScreenW * mScreenH];
        }

        FaceDetector.yuvToARGB(bytes, mScreenW, mScreenH, argb, 0, 0);
        if (faceDetectManager != null) {
            faceDetectManager.check(argb, mScreenW, mScreenH, argbPool);
        }
    }

    /**
     * 暂停识别
     */
    public synchronized void stopIdentify() {
        isIdentify = false;
    }

    public void stop() {
        if (faceDetectManager != null)
            faceDetectManager.stopLocal();
    }

    /**
     * 在检测面部
     *
     * @param status     状态
     * @param infos      数据
     * @param imageFrame 图片框
     */
    @Override
    public void onDetectFace(int status, FaceInfo[] infos, ImageFrame imageFrame) {
        isReading.set(false);

        String desc = "";
        if (status == 0) {
            // 识别到标准人脸
            if (infos == null || infos[0] == null) {
                notifyDesc(FaceConstance.DESC.VERIFYING_FACE);
                return;
            }

            FaceInfo info = infos[0];
            drawFaceFrame(info);

            if (info.headPose[0] > ANGLE) {
                desc = context.getResources().getString(R.string.detect_head_up);
            } else if (info.headPose[0] < -ANGLE) {
                desc = context.getResources().getString(R.string.detect_head_down);
            } else if (info.headPose[1] >= ANGLE) {
                desc = context.getResources().getString(R.string.detect_head_left);
            } else if (info.headPose[1] <= -ANGLE) {
                desc = context.getResources().getString(R.string.detect_head_right);
            }
            if (desc.isEmpty()) {
                desc = FaceConstance.DESC.VERIFYING_FACE;
            }
            notifyDesc(desc);
            return;
        }

        switch (status) {
            case 1:
                desc = context.getResources().getString(R.string.detect_head_up);
                break;
            case 2:
                desc = context.getResources().getString(R.string.detect_head_down);
                break;
            case 3:
                desc = context.getResources().getString(R.string.detect_head_left);
                break;
            case 4:
                desc = context.getResources().getString(R.string.detect_head_right);
                break;
            case 5:
                desc = context.getResources().getString(R.string.detect_low_light);
                break;
            case 6:
                desc = context.getResources().getString(R.string.detect_face_in);
                break;
            case 7:
                desc = context.getResources().getString(R.string.detect_face_in);
                break;
            case 10:
                desc = context.getResources().getString(R.string.detect_keep);
                break;
            case 11:
                desc = context.getResources().getString(R.string.detect_occ_right_eye);
                break;
            case 12:
                desc = context.getResources().getString(R.string.detect_occ_left_eye);
                break;
            case 13:
                desc = context.getResources().getString(R.string.detect_occ_nose);
                break;
            case 14:
                desc = context.getResources().getString(R.string.detect_occ_mouth);
                break;
            case 15:
                desc = context.getResources().getString(R.string.detect_right_contour);
                break;
            case 16:
                desc = context.getResources().getString(R.string.detect_left_contour);
                break;
            case 17:
                desc = context.getResources().getString(R.string.detect_chin_contour);
                break;
            case -1:
                desc = context.getResources().getString(R.string.detect_no_face);
                break;
            default:
                desc = context.getResources().getString(R.string.detect_face_doing);
                break;
        }

        if (infos == null) {
            drawFaceFrame(null);
        }
        notifyDesc(desc);
    }

    private void drawFaceFrame(FaceInfo faceInfo) {
        if (listener != null) {
            if (faceInfo == null) {
                listener.onFaceFrame(null);
                return;
            }

            int[] pts = new int[8];
            faceInfo.getRectPoints(pts);
            listener.onFaceFrame(pts);
        }
    }

    /**
     * 识别到人脸信息，根据类型进行识别或注册
     *
     * @param trackedModel 人脸信息
     */
    @Override
    public void onTrack(FaceFilter.TrackedModel trackedModel) {
        if (!isIdentify)
            return;

        synchronized (FaceHandler.class) {
            if (!isIdentify)
                return;
        }

        if (!trackedModel.meetCriteria()) {
            return;
        }

        switch (handleType) {
            case FaceView.TYPE_IDENTIFY:
            case FaceView.TYPE_REGISTER_AND_IDENTIFY:
                // 识别
                identifyFace(trackedModel);
                break;
            case FaceView.TYPE_REGISTER:
                // 注册
                registerFace(trackedModel);
                break;
        }
    }

    /**
     * 在线识别人脸
     * 实现两个单独功能
     * 1. 单一识别
     * 2. 识别失败结果超过规定次数，进行调度注册人脸
     */
    private void identifyFace(final FaceFilter.TrackedModel model) {
        Log.v("FACEINFO","uploading:"+uploading);
        if(updateUploadToDoing()){
            return;
        }
        Log.v("FACEINFO","uploading111:"+uploading);
        if (model.getEvent() == FaceFilter.Event.OnLeave) {
            uploading = false;
            checkIndex = 0;
            return;
        }

        checkIndex++;

        final File file = FileUtils.saveFaceBmp(context, userId, model);

        if (file == null) {
            uploading = false;
            return;
        }

        FaceClient.getInstance().identifyFace(file, new OnResultListener<List<UserModel>>() {
            @Override
            public void onResult(List<UserModel> result) {
                FileUtils.deleteFace(file);
                if (result.isEmpty()) {
                    uploading = false;

                    if (handleType == FaceView.TYPE_REGISTER_AND_IDENTIFY && checkIndex >= 3) {
                        // 注册
                        registerFace(model);
                    }
                    return;
                }


                for (UserModel userModel : result) {
                    if (userModel.getScore() > 60) {
                        onIdentifyResult(FaceConstance.DESC.IDENTIFY_SUCCESS,
                                true, userModel.getUserId(), userModel.getScore());
                        return;
                    }
                }

                UserModel userModel = result.get(0);
                onIdentifyResult(FaceConstance.DESC.IDENTIFY_FAILURE,
                        false, userModel.getUserId(), userModel.getScore());

                uploading = false;
            }

            @Override
            public void onError(FaceError error) {
                error.printStackTrace();
                FileUtils.deleteFace(file);

                uploading = false;
                if (error.getErrorCode() == 216611) {
                    onIdentifyResult(error.getErrorMessage(), false, "", 0);
                    return;
                }

                if (error.getErrorCode() == 10000) {
                    onIdentifyResult(FaceConstance.DESC.NET_ERROR, false, "", 0);
                    return;
                }

                if (handleType == FaceView.TYPE_REGISTER_AND_IDENTIFY) {
                    if (checkIndex >= 3) {
                        // 注册
                        registerFace(model);
                    }
                    return;
                }

                onIdentifyResult(error.getErrorMessage(), false, "", 0);
            }
        });
    }

    /**
     * 在线注册人脸
     */
    private void registerFace(FaceFilter.TrackedModel model) {
        if(updateUploadToDoing()){
            return;
        }

        final File file = FileUtils.saveFaceBmp(context, userId, model);

        if (file == null) {
            uploading = false;
            return;
        }

        FaceClient.getInstance().registerFace(file, userId, new OnResultListener<Void>() {
            @Override
            public void onResult(Void result) {
                onRegisterResult(file, true, FaceConstance.DESC.REGISTER_SUCCESS);
            }

            @Override
            public void onError(FaceError error) {
                FileUtils.deleteFace(file);
                onRegisterResult(null, false, error.getErrorCode() + error.getErrorMessage());
            }
        });
    }

    /**
     * 更新上传为进行中
     */
    private boolean updateUploadToDoing() {
        if (uploading)
            return true;

        synchronized (FaceHandler.class) {
            if (uploading)
                return true;

            uploading = true;
        }

        return false;
    }

    private void notifyDesc(String desc) {
        if (listener != null) {
            listener.onFaceDescResult(desc);
        }
    }

    private void onIdentifyResult(String result, boolean isSuccess, String userId, double score) {
        if (listener != null) {
            listener.onIdentifyResult(result, isSuccess, userId, score);
        }
    }

    private void onRegisterResult(File file, boolean isSuccess, String message) {
        if (listener != null) {
            listener.onRegisterResult(file, isSuccess, userId, message);
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (faceDetectManager != null) {
            faceDetectManager.setOnFaceDetectListener(null);
            faceDetectManager.setOnTrackListener(null);
        }
        listener = null;
        faceDetectManager = null;
    }

    public interface OnFaceListener {
        void onFaceDescResult(String message);

        void onIdentifyResult(String result, boolean isSuccess, String userId, double score);

        void onRegisterResult(File file, boolean isSuccess, String userId, String message);

        void onFaceFrame(int[] pts);
    }
}
