package com.yanhangtec.faceonlinelibrary.http;

import com.yanhangtec.faceonlinelibrary.listener.OnResultListener;
import com.yanhangtec.faceonlinelibrary.model.baidu.AccessTokenModel;
import com.yanhangtec.faceonlinelibrary.model.FaceError;
import com.yanhangtec.faceonlinelibrary.model.baidu.IdentifyResultModel;
import com.yanhangtec.faceonlinelibrary.model.baidu.RegisterResultModel;
import com.yanhangtec.faceonlinelibrary.model.baidu.UserModel;
import com.yanhangtec.faceonlinelibrary.utils.FileUtils;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 1:15 PM
 */
public class FaceHelper {

    /**
     * 获取AccessToken
     */
    public static Call<AccessTokenModel> initAccessTokenWithAkSk(String ak, String sk,
                                                                 final OnResultListener<AccessTokenModel> listener) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        // 得到一个Call
        Call<AccessTokenModel> call = service.initAccessTokenWithAkSk(ak, sk, "client_credentials");
        // 异步的请求
        call.enqueue(new Callback<AccessTokenModel>() {
            @Override
            public void onResponse(Call<AccessTokenModel> call, Response<AccessTokenModel> response) {
                if (listener == null)
                    return;

                AccessTokenModel body = response.body();
                if (body == null || body.getAccessToken() == null || body.getAccessToken().isEmpty()) {
                    listener.onError(new FaceError(FaceError.ErrorCode.ACCESS_TOKEN_PARSE_ERROR,
                            "token is parse error, please rerequest token"));
                    return;
                }

                listener.onResult(body);
            }

            @Override
            public void onFailure(Call<AccessTokenModel> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new FaceError(FaceError.ErrorCode.ACCESS_TOKEN_PARSE_ERROR,
                            t.getMessage()));
                }
            }
        });
        return call;
    }


    /**
     * 搜索 - 通过人脸信息查找住户信息
     *
     * @param file 人脸图片路径
     */
    public static void searchFace(File file, String groupId,
                                  final OnResultListener<List<UserModel>> listener) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();

        String base64Img = FileUtils.convertStringByFile(file);
        Call<IdentifyResultModel> call = service.searchFace(base64Img, "NORMAL", groupId, "BASE64", "NORMAL");
        call.enqueue(new Callback<IdentifyResultModel>() {
            @Override
            public void onResponse(Call<IdentifyResultModel> call, Response<IdentifyResultModel> response) {
                if (listener == null)
                    return;

                IdentifyResultModel model = response.body();
                if (model == null
                        || model.getErrorCode() != 0
                        || model.getResult() == null
                        || model.getResult().getUserList() == null) {
                    listener.onError(new FaceError(model == null ?
                            FaceError.ErrorCode.SEARCH_ERROR : model.getErrorCode(),
                            model == null ? "住户识别获取数据有误" : model.getErrorMsg()));
                    return;
                }

                listener.onResult(model.getResult().getUserList());
            }

            @Override
            public void onFailure(Call<IdentifyResultModel> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new FaceError(FaceError.ErrorCode.SEARCH_ERROR, t.getMessage()));
                }
            }
        });
    }


    /**
     * 人脸注册
     *
     * @param file 人脸图片路径
     */
    public static void registerFace(File file, String groupId, String userId,
                                    final OnResultListener<Void> listener) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();

        String base64Img = FileUtils.convertStringByFile(file);
        Call<RegisterResultModel> call = service.registerFace(base64Img, "NORMAL",
                groupId, userId, userId, "BASE64", "NORMAL", "APPEND", 0);
        call.enqueue(new Callback<RegisterResultModel>() {
            @Override
            public void onResponse(Call<RegisterResultModel> call, Response<RegisterResultModel> response) {
                if (listener == null)
                    return;

                RegisterResultModel model = response.body();
                if (model == null
                        || model.getErrorCode() != 0
                        || model.getResult() == null) {
                    listener.onError(new FaceError(model == null ?
                            FaceError.ErrorCode.SEARCH_ERROR : model.getErrorCode(),
                            model == null ? "住户识别获取数据有误" : model.getErrorMsg()));
                    return;
                }

                listener.onResult(null);
            }

            @Override
            public void onFailure(Call<RegisterResultModel> call, Throwable t) {
                if (listener != null) {
                    listener.onError(new FaceError(FaceError.ErrorCode.SEARCH_ERROR, t.getMessage()));
                }
            }
        });
    }

}
