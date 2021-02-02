package com.yanhangtec.faceonlinelibrary.http;

import com.yanhangtec.faceonlinelibrary.model.baidu.AccessTokenModel;
import com.yanhangtec.faceonlinelibrary.model.baidu.IdentifyResultModel;
import com.yanhangtec.faceonlinelibrary.model.baidu.RegisterResultModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * http请求接口
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/4/27 17:18
 */
public interface RemoteService {

    /**
     * 获取AccessToken
     */
    @FormUrlEncoded
    @POST("/oauth/2.0/token")
    @Headers({"Content-Type: text/plain; charset=UTF-8"})
    Call<AccessTokenModel> initAccessTokenWithAkSk(@Field("client_id") String ak,
                                                   @Field("client_secret") String sk,
                                                   @Field("grant_type") String grantType);

    /**
     * 通过人脸信息搜索用户
     */
    @FormUrlEncoded
    @POST("/rest/2.0/face/v3/search")
    Call<IdentifyResultModel> searchFace(@Field("image") String image,
                                         @Field("liveness_control") String livenessControl,
                                         @Field("group_id_list") String groupIdList,
                                         @Field("image_type") String imgType,
                                         @Field("quality_control") String qualityControl);


    /**
     * 通过人脸信息搜索用户
     */
    @FormUrlEncoded
    @POST("/rest/2.0/face/v3/faceset/user/add")
    Call<RegisterResultModel> registerFace(@Field("image") String image,
                                           @Field("liveness_control") String livenessControl,
                                           @Field("group_id") String groupId,
                                           @Field("user_id") String userId,
                                           @Field("user_info") String userInfo,
                                           @Field("image_type") String imgType,
                                           @Field("quality_control") String qualityControl,
                                           @Field("action_type") String actionType,
                                           @Field("face_sort_type") int faceSortType);
}
