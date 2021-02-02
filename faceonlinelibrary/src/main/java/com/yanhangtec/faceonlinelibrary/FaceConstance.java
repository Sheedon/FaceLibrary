package com.yanhangtec.faceonlinelibrary;

/**
 * 通用项
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 1:00 PM
 */
public interface FaceConstance {

    String BAI_DU_FACE_URL = "https://aip.baidubce.com";

    interface DESC {
        String IDENTIFY_SUCCESS = "识别成功";
        String REGISTER_SUCCESS = "注册成功";
        String IDENTIFY_FAILURE = "人脸校验不通过,请确认是否已注册";
        String NET_ERROR = "人脸校验不通过,请检查网络后重试";
        String VERIFYING_FACE = "正在核实人脸";

        String FILE_NOT_FIND = "文件不存在";
        String USER_ID_MISS = "缺失用户id";
    }
}
