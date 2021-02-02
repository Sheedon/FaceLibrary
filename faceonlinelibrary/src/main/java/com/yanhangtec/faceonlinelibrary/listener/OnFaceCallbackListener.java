package com.yanhangtec.faceonlinelibrary.listener;

import java.io.File;

/**
 * 人脸数据统一反馈
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/3/29 8:28
 */
public interface OnFaceCallbackListener {

    interface OnBaseListener {
        void onIdentifyState(String desc);
    }

    interface OnFaceIdentifyListener extends OnBaseListener {
        void onIdentifyResult(String result, boolean isSuccess, String userId, double score);
    }

    interface OnFaceRegisterListener extends OnBaseListener {
        void onRegisterResult(File file, boolean isSuccess, String userId, String message);
    }

}
