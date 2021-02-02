package com.yanhangtec.faceonlinelibrary.model.baidu;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 6:39 PM
 */
public class RegisterResultModel {


    /**
     * error_code : 0
     * error_msg : SUCCESS
     * log_id : 9955356520120
     * timestamp : 1611916731
     * cached : 0
     * result : {"face_token":"c2227eb29fd5c9af41847c5f39acac3f","location":{"left":44.53,"top":101.18,"width":206,"height":203,"rotation":-1}}
     */

    private int error_code;
    private String error_msg;
    private long log_id;
    private int timestamp;
    private int cached;
    private ResultBean result;

    public int getErrorCode() {
        return error_code;
    }

    public String getErrorMsg() {
        return error_msg;
    }


    public long getLogId() {
        return log_id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getCached() {
        return cached;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * face_token : c2227eb29fd5c9af41847c5f39acac3f
         * location : {"left":44.53,"top":101.18,"width":206,"height":203,"rotation":-1}
         */

        private String face_token;

        public String getFaceToken() {
            return face_token;
        }


    }
}
