package com.yanhangtec.faceonlinelibrary.http;

import com.google.gson.Gson;
import com.yanhangtec.faceonlinelibrary.FaceConstance;
import com.yanhangtec.faceonlinelibrary.FaceDispatcher;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * http网络请求
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/4/27 17:01
 */
public class Network {

    private static Network instance;
    private Retrofit retrofit;
    private OkHttpClient client;

    static {
        instance = new Network();
    }

    private Network() {
    }

    public static OkHttpClient getClient() {
        if (instance.client != null)
            return instance.client;

        // 存储起来
        instance.client = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                HttpUrl url = request.url().newBuilder()
                                        .addQueryParameter("access_token", FaceDispatcher.getAccessToken())
                                        .build();
                                request = request.newBuilder().url(url).build();
                                return chain.proceed(request);
                            }
                        })
                .addInterceptor(
                        new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        return instance.client;
    }

    // 构建一个Retrofit
    public static Retrofit getRetrofit() {
        if (instance.retrofit != null)
            return instance.retrofit;

        // 得到一个OK Client
        OkHttpClient client = getClient();

        // Retrofit
        Retrofit.Builder builder = new Retrofit.Builder();

        // 设置电脑链接
        instance.retrofit = builder.baseUrl(FaceConstance.BAI_DU_FACE_URL)
                // 设置client
                .client(client)
                // 设置Json解析器
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        return instance.retrofit;

    }

    /**
     * 返回一个请求代理
     *
     * @return RemoteService
     */
    public static RemoteService remote() {
        return Network.getRetrofit().create(RemoteService.class);
    }


}
