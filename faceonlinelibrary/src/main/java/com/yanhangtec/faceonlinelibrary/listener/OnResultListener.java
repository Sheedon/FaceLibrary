/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.yanhangtec.faceonlinelibrary.listener;


import com.yanhangtec.faceonlinelibrary.model.FaceError;

public interface OnResultListener<T> {
    void onResult(T result);

    void onError(FaceError error);
}
