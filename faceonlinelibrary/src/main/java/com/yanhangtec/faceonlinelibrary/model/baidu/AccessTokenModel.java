package com.yanhangtec.faceonlinelibrary.model.baidu;

/**
 * 百度人脸识别初始化获取AccessToken
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 1:40 PM
 */
public class AccessTokenModel {

    private String refresh_token;
    private int expires_in;
    private String session_key;
    private String access_token;
    private String scope;
    private String session_secret;

    public String getRefreshToken() {
        return refresh_token;
    }

    public int getExpiresIn() {
        return expires_in;
    }

    public String getSessionKey() {
        return session_key;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getScope() {
        return scope;
    }

    public String getSessionSecret() {
        return session_secret;
    }
}
