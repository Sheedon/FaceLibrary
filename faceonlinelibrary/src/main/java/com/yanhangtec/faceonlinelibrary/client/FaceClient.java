package com.yanhangtec.faceonlinelibrary.client;

import android.content.Context;

import com.baidu.aip.FaceSDKManager;
import com.yanhangtec.faceonlinelibrary.FaceConstance;
import com.yanhangtec.faceonlinelibrary.FaceDispatcher;
import com.yanhangtec.faceonlinelibrary.R;
import com.yanhangtec.faceonlinelibrary.http.FaceHelper;
import com.yanhangtec.faceonlinelibrary.listener.OnResultListener;
import com.yanhangtec.faceonlinelibrary.model.baidu.AccessTokenModel;
import com.yanhangtec.faceonlinelibrary.model.FaceError;
import com.yanhangtec.faceonlinelibrary.model.baidu.UserModel;

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
        FaceSDKManager.getInstance().initFaceTrackerConfig(context);
    }

//    public void updateFaceTracker(Context context) {
//        FaceSDKManager.getInstance().getFaceTracker(context).set_min_face_size(80);
//        FaceSDKManager.getInstance().getFaceTracker(context).set_isCheckQuality(false);
//        // 该角度为商学，左右，偏头的角度的阀值，大于将无法检测出人脸，为了在1：n的时候分数高，注册尽量使用比较正的人脸，可自行条件角度
//        FaceSDKManager.getInstance().getFaceTracker(context).set_eulur_angle_thr(45, 45, 45);
//        FaceSDKManager.getInstance().getFaceTracker(context).set_isVerifyLive(true);
//        FaceSDKManager.getInstance().getFaceTracker(context).set_notFace_thr(0.2f);
//        FaceSDKManager.getInstance().getFaceTracker(context).set_occlu_thr(0.1f);
//    }
}
