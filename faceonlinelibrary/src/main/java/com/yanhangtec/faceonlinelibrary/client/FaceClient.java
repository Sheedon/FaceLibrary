package com.yanhangtec.faceonlinelibrary.client;

import android.content.Context;

import com.baidu.aip.FaceEnvironment;
import com.baidu.aip.FaceSDKManager;
import com.baidu.idl.facesdk.FaceTracker;
import com.yanhangtec.faceonlinelibrary.FaceConstance;
import com.yanhangtec.faceonlinelibrary.FaceDispatcher;
import com.yanhangtec.faceonlinelibrary.R;
import com.yanhangtec.faceonlinelibrary.http.FaceHelper;
import com.yanhangtec.faceonlinelibrary.listener.OnResultListener;
import com.yanhangtec.faceonlinelibrary.model.baidu.AccessTokenModel;
import com.yanhangtec.faceonlinelibrary.model.FaceError;
import com.yanhangtec.faceonlinelibrary.model.baidu.UserModel;
import com.yanhangtec.faceonlinelibrary.utils.FormatUtils;

import java.io.File;
import java.util.List;

import retrofit2.Call;

/**
 * 人脸客户端
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/3/24 16:39
 */
public class FaceClient {

    private static volatile FaceClient instance;

    // 人脸识别库组
    private String groupId;

    // accessToken 反馈获取方式
    private Call<AccessTokenModel> accessTokenCall;

    public static FaceClient getInstance() {
        if (instance == null) {
            synchronized (FaceClient.class) {
                if (instance == null) {
                    instance = new FaceClient();
                }
            }
        }
        return instance;
    }

    private FaceClient() {

    }

    /**
     * 初始化配置
     */
    public void initConfig(Context context, OnResultListener<AccessTokenModel> listener) {
        String licenseId = context.getResources().getString(R.string.baidu_license_id);
        String licenseFileName = context.getResources().getString(R.string.baidu_license_file_name);

        initLib(context, licenseId, licenseFileName);
        initAPIFindAccessToken(context, listener);

    }

    /**
     * 初始化SDK
     */
    private void initLib(Context context, String licenseId, String licenseFileName) {
        FaceSDKManager.getInstance().init(context, licenseId, licenseFileName);
        setFaceConfig(context);
    }

    /**
     * 初始化在线人脸识别SDK
     */
    public void initAPIFindAccessToken(Context context, final OnResultListener<AccessTokenModel> listener) {

        String apiKey = context.getResources().getString(R.string.baidu_api_key);
        String secretKey = context.getResources().getString(R.string.baidu_secret_key);
        groupId = context.getResources().getString(R.string.baidu_group_id);
        // 用ak，sk获取token, 调用在线api，如：注册、识别等。为了ak、sk安全，建议放您的服务器，
        if (accessTokenCall != null && !accessTokenCall.isCanceled()) {
            accessTokenCall.cancel();
        }

        accessTokenCall = FaceHelper.initAccessTokenWithAkSk(apiKey, secretKey, new OnResultListener<AccessTokenModel>() {
            @Override
            public void onResult(AccessTokenModel result) {
                FaceDispatcher.setAccessToken(result.getAccessToken());
                if (listener != null) {
                    listener.onResult(result);
                }
            }

            @Override
            public void onError(FaceError error) {
                if (listener != null) {
                    listener.onError(error);
                }
            }
        });
    }

    /**
     * 识别人脸
     *
     * @param file     人脸图片文件
     * @param listener 反馈监听器
     */
    public void identifyFace(File file, final OnResultListener<List<UserModel>> listener) {
        if (file == null || !file.exists()) {
            if (listener != null)
                listener.onError(new FaceError(FaceConstance.DESC.FILE_NOT_FIND));

            return;
        }

        FaceHelper.searchFace(file, groupId, listener);
    }

    /**
     * 人脸注册
     *
     * @param file     人脸图片文件
     * @param userId   用户ID
     * @param listener 监听器
     */
    public void registerFace(File file, String userId, OnResultListener<Void> listener) {
        if (userId == null || userId.isEmpty()) {
            if (listener != null)
                listener.onError(new FaceError(FaceConstance.DESC.USER_ID_MISS));

            return;
        }

        if (file == null || !file.exists()) {
            if (listener != null)
                listener.onError(new FaceError(FaceConstance.DESC.FILE_NOT_FIND));

            return;
        }

        FaceHelper.registerFace(file, groupId, userId, listener);
    }

    /**
     * 设置人脸配置
     */
    private void setFaceConfig(Context context) {
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

    public void updateFaceTracker(Context context) {
        FaceSDKManager.getInstance().getFaceTracker(context).set_min_face_size(80);
        FaceSDKManager.getInstance().getFaceTracker(context).set_isCheckQuality(false);
        // 该角度为商学，左右，偏头的角度的阀值，大于将无法检测出人脸，为了在1：n的时候分数高，注册尽量使用比较正的人脸，可自行条件角度
        FaceSDKManager.getInstance().getFaceTracker(context).set_eulur_angle_thr(45, 45, 45);
        FaceSDKManager.getInstance().getFaceTracker(context).set_isVerifyLive(true);
        FaceSDKManager.getInstance().getFaceTracker(context).set_notFace_thr(0.2f);
        FaceSDKManager.getInstance().getFaceTracker(context).set_occlu_thr(0.1f);
    }
}
