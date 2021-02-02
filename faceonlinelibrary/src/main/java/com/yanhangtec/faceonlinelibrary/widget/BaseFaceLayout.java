package com.yanhangtec.faceonlinelibrary.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.serenegiant.usb.UVCCamera;
import com.yanhangtec.faceonlinelibrary.utils.DensityUtils;

/**
 * 基础人脸框绘制
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 1/27/21 4:48 PM
 */
public class BaseFaceLayout extends RelativeLayout {

    protected ImageView faceFrameImg = null;
    private Paint mPaint;
    private Paint mPaintHorn;
    private int mSizeFour;
    private int mSizeThirty;

    protected int topHeight;
    private int sHeight;
    private int sWidth;
    private double widthScale;
    private double heightScale;

    public BaseFaceLayout(Context context) {
        this(context, null);
    }

    public BaseFaceLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            sWidth = outMetrics.widthPixels;
            sHeight = outMetrics.heightPixels;

            widthScale = outMetrics.widthPixels * 1.0 / UVCCamera.DEFAULT_PREVIEW_WIDTH;
            heightScale = outMetrics.heightPixels * 1.0 / UVCCamera.DEFAULT_PREVIEW_HEIGHT;
        }
//        int widthPixels = outMetrics.widthPixels;

        // 绘制四个角时所需的长宽尺寸
        mSizeFour = DensityUtils.dip2px(context, 4);
        mSizeThirty = DensityUtils.dip2px(context, 14);
        // 绘制人脸框四个角的画笔
        mPaintHorn = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintHorn.setColor(Color.argb(255, 232, 78, 64));
        mPaintHorn.setStrokeWidth(DensityUtils.dip2px(context, 2));
        // 绘制人脸框四条边的画笔
        mPaint = new Paint();
        mPaint.setStrokeWidth(DensityUtils.dip2px(context, 1));
        mPaint.setTextSize(10);
        mPaint.setColor(Color.argb(155, 232, 78, 64));
    }

    /**
     * 绘制人脸边框
     *
     * @param pts 边框位置
     *            0：左下x
     *            1：左下y
     *            2：左上x
     *            3：左上y
     *            4：右上x
     *            5：右上y
     *            6：右下x
     *            7：右下y
     */
    public void onDrawFaceFrame(int[] pts) {
        if (pts == null) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (faceFrameImg != null)
                        faceFrameImg.setImageBitmap(null);
                }
            });

            return;
        }
        final Bitmap canvasImg = Bitmap.createBitmap(sWidth,
                sHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasImg);


        int topLeftX = (int) (pts[2] / widthScale);
        int topLeftY = (int) (pts[3]* heightScale / 1.65);
        int topRightX = (int) (pts[4] * widthScale / 1.125);
        int topRightY = (int) (pts[5]* heightScale / 1.65);
        int bottomLeftX = (int) (pts[0] / widthScale);
        int bottomLeftY = (int) (pts[1] * heightScale / 1.65);
        int bottomRightX = (int) (pts[6] * widthScale / 1.125);
        int bottomRightY = (int) (pts[7] * heightScale / 1.65);

        // 上
        canvas.drawLine(topLeftX, topLeftY, topRightX, topRightY, mPaint);
        // 下
        canvas.drawLine(bottomLeftX, bottomLeftY, bottomRightX, bottomRightY, mPaint);
        // 左
        canvas.drawLine(topLeftX, topLeftY, bottomLeftX, bottomLeftY, mPaint);
        // 右
        canvas.drawLine(topRightX, topRightY, bottomRightX, bottomRightY, mPaint);

        // 绘制左上竖
        canvas.drawRoundRect(topLeftX - mSizeFour, topLeftY - mSizeFour, topLeftX,
                topLeftY + mSizeThirty, 10, 10, mPaintHorn);
        // 绘制左上横
        canvas.drawRoundRect(topLeftX - mSizeFour, topLeftY - mSizeFour,
                topLeftX + mSizeThirty, topLeftY, 10, 10, mPaintHorn);
        // 绘制左下竖
        canvas.drawRoundRect(bottomLeftX - mSizeFour, bottomLeftY - mSizeThirty,
                bottomLeftX,
                bottomLeftY + mSizeFour, 10, 10, mPaintHorn);
        // 绘制左下横
        canvas.drawRoundRect(bottomLeftX - mSizeFour, bottomLeftY,
                bottomLeftX + mSizeThirty,
                bottomLeftY + mSizeFour, 10, 10, mPaintHorn);
        // 绘制右上竖
        canvas.drawRoundRect(topRightX, topRightY - mSizeFour, topRightX + mSizeFour,
                topRightY + mSizeThirty, 10, 10, mPaintHorn);
        // 绘制右上横
        canvas.drawRoundRect(topRightX - mSizeThirty, topRightY - mSizeFour,
                topRightX + mSizeFour, topRightY, 10, 10, mPaintHorn);
        // 绘制右下竖
        canvas.drawRoundRect(bottomRightX, bottomRightY - mSizeThirty,
                bottomRightX + mSizeFour,
                bottomRightY + mSizeFour, 10, 10, mPaintHorn);
        // 绘制右下横
        canvas.drawRoundRect(bottomRightX - mSizeThirty, bottomRightY,
                bottomRightX + mSizeFour,
                bottomRightY + mSizeFour, 10, 10, mPaintHorn);

        post(new Runnable() {
            @Override
            public void run() {
                if (faceFrameImg != null)
                    faceFrameImg.setImageBitmap(canvasImg);
            }
        });
    }

    public void initFaceFrame(Context context) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        faceFrameImg = new ImageView(context);
        addView(faceFrameImg, lp);
        faceFrameImg.setVisibility(VISIBLE);
    }
}
