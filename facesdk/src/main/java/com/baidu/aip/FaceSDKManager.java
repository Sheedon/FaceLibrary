/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip;

import android.content.Context;

import com.baidu.idl.facesdk.FaceRecognize;
import com.baidu.idl.facesdk.FaceSDK;
import com.baidu.idl.facesdk.FaceTracker;

import demo.face.aip.baidu.com.facesdk.R;

public class FaceSDKManager {
    private FaceTracker faceTracker;
    private FaceRecognize faceRecognize;

    private static class HolderClass {
        private static final FaceSDKManager instance = new FaceSDKManager();
    }

    public static FaceSDKManager getInstance() {
        return HolderClass.instance;
    }

    private FaceSDKManager() {
    }

    /**
     * FaceSDK 初始化，用户可以根据自己的需求实例化FaceTracker 和 FaceRecognize ，具体功能参考文档
     *
     * @param context
     */
    public void init(Context context, String licenseId, String licenseFileName) {
      /*  FaceSDK.initLicense(context, "faceexample-face-android",
                "idl-license.faceexample-face-android", true); */
        FaceSDK.initLicense(context, licenseId,
                licenseFileName, true);
        FaceSDK.initModel(context);
        //  FaceSDK.initModel(context,
        //        FaceSDK.AlignMethodType.CDNN,
        //      FaceSDK.ParsMethodType.NOT_USE);
        getFaceTracker(context);
        getFaceRecognize(context);
    }

    /**
     * 初始化FaceTracker，成功之后可以直接使用实例方法
     *
     * @param context
     * @return
     */
    public FaceTracker getFaceTracker(Context context) {
        if (faceTracker == null) {
            faceTracker = new FaceTracker(context);
            faceTracker.set_isFineAlign(false);
            faceTracker.set_isFineAlign(false);
            faceTracker.set_isVerifyLive(true);
            faceTracker.set_DetectMethodType(1);
            faceTracker.set_isCheckQuality(FaceEnvironment.VALUE_IS_CHECK_QUALITY);
            faceTracker.set_notFace_thr(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
            faceTracker.set_min_face_size(FaceEnvironment.VALUE_MIN_FACE_SIZE);
            faceTracker.set_cropFaceSize(FaceEnvironment.VALUE_CROP_FACE_SIZE);
            faceTracker.set_illum_thr(FaceEnvironment.VALUE_BRIGHTNESS);
            faceTracker.set_blur_thr(FaceEnvironment.VALUE_BLURNESS);
            faceTracker.set_occlu_thr(FaceEnvironment.VALUE_OCCLUSION);
            faceTracker.set_max_reg_img_num(FaceEnvironment.VALUE_MAX_CROP_IMAGE_NUM);
            faceTracker.set_eulur_angle_thr(
                    FaceEnvironment.VALUE_HEAD_PITCH,
                    FaceEnvironment.VALUE_HEAD_YAW,
                    FaceEnvironment.VALUE_HEAD_ROLL
            );
            faceTracker.set_track_by_detection_interval(800);
            return faceTracker;
        }
        return faceTracker;
    }

    public void initFaceTrackerConfig(Context context){
        FaceTracker tracker = FaceSDKManager.getInstance().getFaceTracker(context);  //.getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整

        // 模糊度范围 (0-1) 推荐小于0.7
        float blurness = FormatUtils.stringToFloat(FormatUtils.getStringRes(context, R.string.value_blurness),
                FaceEnvironment.VALUE_BLURNESS);
        tracker.set_blur_thr(blurness);
        // 光照范围 (0-1) 推荐大于40
        float brightness = FormatUtils.stringToFloat(FormatUtils.getStringRes(context, R.string.value_brightness),
                FaceEnvironment.VALUE_BRIGHTNESS);
        tracker.set_illum_thr(brightness);
        // 裁剪人脸大小
        tracker.set_cropFaceSize(FormatUtils.getIntegerRes(context, R.integer.value_crop_face_size));
        // 人脸yaw,pitch,row 角度，范围（-45，45），推荐-15-15
        tracker.set_eulur_angle_thr(FormatUtils.getIntegerRes(context, R.integer.value_head_pitch),
                FormatUtils.getIntegerRes(context, R.integer.value_head_roll),
                FormatUtils.getIntegerRes(context, R.integer.value_head_yaw));

        // 最小检测人脸（在图片人脸能够被检测到最小值）80-200， 越小越耗性能，推荐120-200
        tracker.set_min_face_size(FormatUtils.getIntegerRes(context, R.integer.value_min_face_size));
        //

        tracker.set_notFace_thr(FormatUtils.stringToFloat(FormatUtils.getStringRes(context, R.string.value_not_face_threshold),
                FaceEnvironment.VALUE_NOT_FACE_THRESHOLD));
        // 人脸遮挡范围 （0-1） 推荐小于0.5
        tracker.set_occlu_thr(FormatUtils.stringToFloat(FormatUtils.getStringRes(context, R.string.value_occlusion),
                FaceEnvironment.VALUE_OCCLUSION));
        // 是否进行质量检测
        tracker.set_isCheckQuality(FormatUtils.getBoolean(context, R.bool.value_is_check_quality));
        // 是否进行活体校验
        tracker.set_isVerifyLive(FormatUtils.getBoolean(context, R.bool.value_is_verify_live));
    }

    /**
     * 初始化FaceRecognize，成功之后可用直接使用实例方法
     *
     * @param context
     */
    public FaceRecognize getFaceRecognize(Context context) {
        if (faceRecognize == null) {
            faceRecognize = new FaceRecognize(context);
        }
        return faceRecognize;
    }
}
