package com.cj.videoprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleableRes;


public class VolumeProgressView extends View {

    private RectF mLayer;                   // 画布图层大小
    private float mProgress = 0f;

    public Paint mPaint;                   // 画笔

    private float mHaloHeight = 7; //dp
    private float mHaloWidth = 2; //dp

    private int mNumOfHalo = 16;
    private float mOneOFHaleDegrees = 360.0f / mNumOfHalo;
    private float mOneOFHaleProgress = 1.0f / mNumOfHalo;

    private Bitmap volume1, volume2, volume3;
    @StyleableRes
    private int drawable1 = R.drawable.volume_low,
            drawable2 = R.drawable.volume_medium,
            drawable3 = R.drawable.volume_high;
    private int mHaloColor = Color.WHITE;


    public VolumeProgressView(Context context) {
        this(context, null);
    }

    public VolumeProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public VolumeProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VolumeProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }


    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VolumeProgressView);
        mHaloHeight = dp2px(mHaloHeight);
        mHaloWidth = dp2px(mHaloWidth);

        mHaloHeight = ta.getDimension(R.styleable.VolumeProgressView_vpv_halo_height, mHaloHeight);
        mHaloWidth = ta.getDimension(R.styleable.VolumeProgressView_vpv_halo_width, mHaloWidth);
        mNumOfHalo = ta.getInteger(R.styleable.VolumeProgressView_vpv_num_of_halo, mNumOfHalo);
        drawable1 = ta.getResourceId(R.styleable.VolumeProgressView_vpv_volume_low, drawable1);
        drawable2 = ta.getResourceId(R.styleable.VolumeProgressView_vpv_volume_medium, drawable2);
        drawable3 = ta.getResourceId(R.styleable.VolumeProgressView_vpv_volume_high, drawable3);
        mHaloColor = ta.getColor(R.styleable.VolumeProgressView_vpv_halo_color, mHaloColor);
        ta.recycle();
    }


    private void init() {
        mLayer = new RectF();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mHaloColor);
        mPaint.setStyle(Paint.Style.FILL);

        setWillNotDraw(false);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        volume1 = BitmapFactory.decodeResource(getResources(), drawable1);
        volume2 = BitmapFactory.decodeResource(getResources(), drawable2);
        volume3 = BitmapFactory.decodeResource(getResources(), drawable3);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float margin = mHaloHeight + mHaloWidth * 2;
        float paddingLeft = getPaddingLeft();
        float paddingRight = getPaddingRight();
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();
        mLayer.set(margin + paddingLeft, margin + paddingTop,
                w - margin - paddingRight, h - margin - paddingBottom);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPath(canvas);
    }


    private void drawPath(Canvas canvas) {

        float progress = mProgress;

        int type = (int) ((1 - progress) / 0.33f);

        if (type == 0) {
            canvas.drawBitmap(volume1, null, mLayer, mPaint);
        } else if (type == 1) {
            canvas.drawBitmap(volume2, null, mLayer, mPaint);
        } else {
            canvas.drawBitmap(volume3, null, mLayer, mPaint);
        }
        //画光晕
        canvas.save();
        canvas.translate(mLayer.centerX(), mLayer.centerY());
        int count = mNumOfHalo - (int) (progress / mOneOFHaleProgress);
        float mHalfHaloWidth = mHaloWidth / 2;
        for (int i = 0; i < count; i++) {
            canvas.drawRoundRect(new RectF(-mHalfHaloWidth, -mLayer.centerY() + getPaddingTop(),
                    mHalfHaloWidth, mHaloHeight - mLayer.centerY() + getPaddingTop()), mHalfHaloWidth, mHalfHaloWidth, mPaint);
            canvas.rotate(mOneOFHaleDegrees);
        }
        canvas.restore();

    }


    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }


    /**
     * 设置进度
     *
     * @param progress 进度值 0-1.0
     */

    public void setProgress(float progress) {
        this.mProgress = 1.0f - progress;
        postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {


        super.onDetachedFromWindow();
        if (volume1 != null && !volume1.isRecycled()) {
            volume1.recycle();
            volume1 = null;
        }
        if (volume2 != null && !volume2.isRecycled()) {
            volume2.recycle();
            volume2 = null;
        }
        if (volume3 != null && !volume3.isRecycled()) {
            volume3.recycle();
            volume3 = null;
        }
    }


}
