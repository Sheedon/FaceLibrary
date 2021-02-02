package com.yanhangtec.faceonlinelibrary;

import android.content.Context;

import com.yanhangtec.faceonlinelibrary.client.FaceClient;
import com.yanhangtec.faceonlinelibrary.listener.OnResultListener;
import com.yanhangtec.faceonlinelibrary.model.baidu.AccessTokenModel;

/**
 * 人脸中心调度器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 10:18 AM
 */
public class FaceDispatcher {

    private static String accessToken = "";

    /**
     * 初始化配置
     *
     * @param context  上下文
     * @param listener 初始化监听器
     */
    public static void setUp(Context context, OnResultListener<AccessTokenModel> listener) {
        FaceClient.getInstance().initConfig(context, listener);
    }

    /**
     * 更新AccessToken
     *
     * @param context  上下文
     * @param listener 更新监听器
     */
    public static void updateAccessToken(Context context, OnResultListener<AccessTokenModel> listener) {
        FaceClient.getInstance().initAPIFindAccessToken(context, listener);
    }

    public static void setAccessToken(String accessToken) {
        FaceDispatcher.accessToken = accessToken;
    }

    public static String getAccessToken() {
        return accessToken;
    }
}
