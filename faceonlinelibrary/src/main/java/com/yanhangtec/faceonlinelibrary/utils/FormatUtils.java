package com.yanhangtec.faceonlinelibrary.utils;

import android.content.Context;

import androidx.annotation.BoolRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;

/**
 * 格式化实用程序
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/3/25 8:21
 */
public class FormatUtils {

    public static float stringToFloat(String num, float defaultNum) {
        try {
            return Float.parseFloat(num);
        } catch (NumberFormatException e) {
            return defaultNum;
        }
    }

    public static String getStringRes(Context context, @StringRes int res){
        return context.getResources().getString(res);
    }

    public static int getIntegerRes(Context context, @IntegerRes int res){
        return context.getResources().getInteger(res);
    }

    public static boolean getBoolean(Context context, @BoolRes int res){
        return context.getResources().getBoolean(res);
    }
}
