package com.yanhangtec.faceonlinelibrary.model.baidu;

/**
 * 用户信息
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 4:17 PM
 */
public class UserModel {

    private String group_id;
    private String user_id;
    private String user_info;
    private double score;

    public String getGroupId() {
        return group_id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getUserInfo() {
        return user_info;
    }

    public double getScore() {
        return score;
    }
}
