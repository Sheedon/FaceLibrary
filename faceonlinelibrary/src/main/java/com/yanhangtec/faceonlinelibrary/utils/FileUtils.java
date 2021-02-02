package com.yanhangtec.faceonlinelibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.baidu.aip.face.FaceFilter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/29/21 3:56 PM
 */
public class FileUtils {

    /**
     * 保存人脸图片
     */
    public static File saveFaceBmp(Context context, String userId, FaceFilter.TrackedModel model) {

        final Bitmap face = model.cropFace();
        userId = userId == null || userId.isEmpty() ? UUID.randomUUID().toString().replace("-", "_") : userId;
        String fileName = userId + ".jpg";
        if (face != null) {
            ImageSaveUtil.saveCameraBitmap(context, face, fileName);
        }
        String filePath = ImageSaveUtil.loadCameraBitmapPath(context, fileName);
        File currentFile = new File(filePath);
        if (!currentFile.exists()) {
            return null;
        }
        try {
            byte[] buf = readFile(currentFile);
            if (buf.length > 0) {
                return currentFile;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除人脸文件
     * @param file 人脸文件
     */
    public static void deleteFace(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * 文件转化为String
     *
     * @param file 图片文件
     */
    public static String convertStringByFile(File file) {
        try {
            byte[] buf = FileUtils.readFile(file);
            return new String(android.util.Base64.encode(buf, Base64.NO_WRAP));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取文件
     */
    public static byte[] readFile(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;

            if (length != longlength) {
                throw new IOException("File size >= 2 GB");
            }
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
