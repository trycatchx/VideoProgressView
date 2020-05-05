package com.cj.videoprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * create by jiong 2019/07/17
 */

public class LightProgressView extends View {
    private RectF mLayer;                   // 画布图层大小
    private float mProgress = 0f;
    private PathMeasure mMeasure;
    private Path mQuadPath;
    public Path mCirclePath;
    public float circleR = 0f;
    public Paint mPaint;                   // 画笔
    public Paint mOpPaint;                   // 画笔

    private float mHaloHeight = 7; //dp
    private float mHaloWidth = 2; //dp

    private int mNumOfHalo = 16;
    private float mOneOFHaleDegrees = 360.0f / mNumOfHalo;
    private float mOneOFHaleProgress = 1.0f / mNumOfHalo;
    private float magicNum = 0.43f;
    private int mMoonColor = Color.WHITE;
    private int mHaloColor = Color.WHITE;

    public LightProgressView(Context context) {
        super(context);
    }

    public LightProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LightProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LightProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context,attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LightProgressView);
        mHaloHeight = dp2px(mHaloHeight);
        mHaloWidth = dp2px(mHaloWidth);
        mHaloHeight = ta.getDimension(R.styleable.LightProgressView_lpv_halo_height, mHaloHeight);
        mHaloWidth = ta.getDimension(R.styleable.LightProgressView_lpv_halo_width, mHaloWidth);
        mNumOfHalo = ta.getInteger(R.styleable.LightProgressView_lpv_num_of_halo, mNumOfHalo);
        magicNum = ta.getFloat(R.styleable.LightProgressView_lpv_magicnum, magicNum);
        mMoonColor = ta.getColor(R.styleable.LightProgressView_lpv_moon_color, mMoonColor);
        mHaloColor = ta.getColor(R.styleable.LightProgressView_lpv_halo_color, mHaloColor);
        ta.recycle();
    }

    private void init() {
        mMeasure = new PathMeasure();
        mQuadPath = new Path();
        mCirclePath = new Path();
        mLayer = new RectF();

        mPaint = new Paint();
        mPaint.setColor(mMoonColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mOpPaint = new Paint();
        mOpPaint.setColor(mMoonColor);
        mOpPaint.setAntiAlias(true);
        mOpPaint.setStyle(Paint.Style.FILL);
        mOpPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        setWillNotDraw(false);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float paddingLeft = getPaddingLeft();
        float paddingRight = getPaddingRight();
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();
        // 太阳/月亮 到光晕的间隔是两倍光晕的宽度
        float margin = mHaloHeight + mHaloWidth * 2;
        //实际上太阳/月亮 具体宽高
        mLayer.set(margin + paddingLeft, margin + paddingTop,
                w - margin - paddingRight, h - margin - paddingBottom);
        mCirclePath.reset();
        //取宽高中 最短的最为太阳的半径
        circleR = w > h ? (h - 2 * margin - paddingTop - paddingBottom) / 2.0f :
                (w - 2 * margin - paddingLeft - paddingRight) / 2.0f;
        //顺时钟画圆，圆的起始位置在右侧中间
        mCirclePath.addCircle(mLayer.centerX(), mLayer.centerY(), circleR, Path.Direction.CW);
        //把画好圆的 path 添加到 PathMeasure，待会可以被 getPosTan 使用
        mMeasure.setPath(mCirclePath, false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPath(canvas);
    }

    private void drawPath(Canvas canvas) {

        float progress = mProgress;

        float[] mBeginPoint = {0f, 0f};//贝赛尔曲线的第一个定点
        float[] mSecondPoint = {0f, 0f}; //贝赛尔曲线的第二个定点

        //找到第一个定点
        getBeginPoint(progress, mBeginPoint);
        //找到第二个定点
        getSecondPoint(progress, mSecondPoint);


        mQuadPath.reset();
        mQuadPath.moveTo(mBeginPoint[0], mBeginPoint[1]);
        float[] begin = {mBeginPoint[0], mBeginPoint[1]};
        //找到拟合圆的贝赛尔曲线控制点
        float[] contrlPoint = getContrlPoint(begin, mSecondPoint);
        //画贝赛尔曲线
        mQuadPath.quadTo(contrlPoint[0], contrlPoint[1], mSecondPoint[0], mSecondPoint[1]);

        //找到圆弧的起始角度和扫过的角度
        Pair<Float, Float> degrees = getAngle(mBeginPoint, mSecondPoint);
        //圆弧画大一点。边界才不会有遗漏
        RectF curRecf = new RectF(mLayer);
        curRecf.left -= 2;
        curRecf.top -= 2;
        curRecf.right += 2;
        curRecf.bottom += 2;
        //画圆弧
        mQuadPath.arcTo(curRecf, degrees.first, degrees.second);
        mQuadPath.moveTo(mBeginPoint[0], mBeginPoint[1]);
        mQuadPath.close();

        //新建画布层
        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
        //画大圆
        mPaint.setColor(mMoonColor);
        canvas.drawPath(mCirclePath, mPaint);
        //画op操作后的月亮
        canvas.drawPath(mQuadPath, mOpPaint);
        canvas.restore();

        //画光晕
        canvas.save();
        //画布平移到中间，为了等下旋转使用
        canvas.translate(mLayer.centerX(), mLayer.centerY());
        //计算出当前进度需要画多少个光晕
        int count = mNumOfHalo - (int) (progress / mOneOFHaleProgress);
        float mHalfHaloWidth = mHaloWidth / 2;
        //开始画光晕
        mPaint.setColor(mHaloColor);
        for (int i = 0; i < count; i++) {
            canvas.drawRoundRect(new RectF(-mHalfHaloWidth, -mLayer.centerY() + getPaddingTop(), mHalfHaloWidth,
                    mHaloHeight - mLayer.centerY() + getPaddingTop()), mHalfHaloWidth, mHalfHaloWidth, mPaint);
            canvas.rotate(mOneOFHaleDegrees);
        }
        canvas.restore();

    }

    /**
     * 分段函数
     *
     * @param progress
     * @param point
     */
    private void getBeginPoint(float progress, float[] point) {
        if (progress <= 0.5) {
            //A 定点 在0.5 progress 之前都是在第四
            mMeasure.getPosTan(mMeasure.getLength() * (-0.2f * progress + 0.1f), point, null);
        } else {
            mMeasure.getPosTan(mMeasure.getLength() * (-0.2f * progress + 1.1f), point, null);
        }
    }

    /**
     * 分段函数
     *
     * @param progress
     * @param point
     */
    private void getSecondPoint(float progress, float[] point) {
        if (progress <= 0.1) {
            mMeasure.getPosTan(mMeasure.getLength() * (-1.0f * progress + 0.1f), point, null);
        } else {
            mMeasure.getPosTan(mMeasure.getLength() * (-7.0f / 9.0f * progress + 9.7f / 9f), point, null);

        }
    }


    private Pair<Float, Float> getAngle(float[] point1, float[] point2) {
        float centerX = mLayer.centerX();
        float centerY = mLayer.centerY();
        float diffY;
        float degrees1 = 0;
        float degrees2;

        float startAngle;
        float sweepAngle;
        if (point2[0] > centerX && point2[1] > centerY) {
            degrees1 = (float) Math.toDegrees(Math.asin((point2[1] - centerY) / circleR));
            degrees2 = (float) Math.toDegrees(Math.asin((point1[1] - centerY) / circleR));
            startAngle = degrees1;
            sweepAngle = degrees2 - degrees1;
        } else {
            if (point2[0] > centerX) { //一 象限
                if (point2[1] < centerY) {
                    diffY = centerY - point2[1];
                    degrees1 = (float) Math.toDegrees(Math.asin(diffY / circleR));
                }
            } else { // 2 3 象限
                if (point2[1] < centerY) {
                    diffY = centerY - point2[1];
                    degrees1 = 180 - (float) Math.toDegrees(Math.asin(diffY / circleR));
                } else {
                    diffY = point2[1] - centerY;
                    degrees1 = (float) Math.toDegrees(Math.asin(diffY / circleR)) + 180;
                }
            }
            degrees2 = (float) Math.toDegrees(Math.asin((centerY - point1[1]) / circleR));

            startAngle = 360 - degrees1;
            sweepAngle = degrees1 - degrees2;
        }

        return new Pair<>(startAngle, sweepAngle);
    }

    /**
     * 0.35228475f 是一个 魔数， 可以让贝塞尔曲线拟合圆的弧度
     *
     * @param point1
     * @param point2
     * @return
     */


    private float[] getContrlPoint(float[] point1, float[] point2) {
        float centerX = mLayer.centerX();
        float centerY = mLayer.centerY();
        float diffDis = (float) Math.sqrt((point1[0] - point2[0]) * (point1[0] - point2[0]) + (point1[1] - point2[1]) * (point1[1] - point2[1]));
        //中垂线函数 y = kx+b 中的 k
        float k = (point1[0] - point2[0]) / (point2[1] - point1[1]);
        //中垂线函数 y = kx+b 中的 b
        float b = (point1[1] + point2[1]) / 2.0f - (point1[0] * point1[0] - point2[0] * point2[0]) / 2.0f / (point2[1] - point1[1]);
        float[] point = {0f, 0f};
        // cosα 的值
        float cosDegrees = (float) (1 / Math.sqrt(1 + k * k));
        if (k < 0) {
            //magicNum 为0.43
            point[0] = (point1[0] + point2[0]) / 2.0f - (cosDegrees * diffDis * magicNum);
        } else if (k > 0) {
            if (point1[0] > centerX && point1[1] > centerY && point2[0] > centerX) {
                point[0] = (point1[0] + point2[0]) / 2.0f - (cosDegrees * diffDis * magicNum);
            } else {
                point[0] = (point1[0] + point2[0]) / 2.0f + (cosDegrees * diffDis * magicNum);
            }
        } else {
            point[0] = (point1[0] + point2[0]) / 2.0f;
        }

        point[1] = k * point[0] + b;
        return point;
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


}
