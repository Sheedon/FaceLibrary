package com.yanhangtec.faceonlinelibrary.model.baidu;

import java.util.List;

/**
 * 识别结果
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 4:11 PM
 */
public class IdentifyResultModel {


    /**
     * error_code : 0
     * error_msg : SUCCESS
     * log_id : 7510135990599
     * timestamp : 1611907582
     * cached : 0
     * result : {"face_token":"981fa2002aa8db31ba5b5b723173d19e","user_list":[{"group_id":"01","user_id":"048874488cba11e98d9d000c29d5b46b","user_info":"","score":95.790946960449}]}
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

    public static class ResultBean {
        /**
         * face_token : 981fa2002aa8db31ba5b5b723173d19e
         * user_list : [{"group_id":"01","user_id":"048874488cba11e98d9d000c29d5b46b","user_info":"","score":95.790946960449}]
         */

        private String face_token;
        private List<UserModel> user_list;

        public String getFaceToken() {
            return face_token;
        }

        public List<UserModel> getUserList() {
            return user_list;
        }
    }
}
