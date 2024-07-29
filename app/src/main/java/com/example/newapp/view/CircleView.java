package com.example.newapp.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.example.newapp.R;

/**
 * @author xiaoyi
 * @description
 * 自定义环形图
 * @date 2021/2/4
 */
public class CircleView extends View {


    private ValueAnimator valueAnimator;
    private int mViewCenterX;   //view宽的中心点
    private int mViewCenterY;   //view高的中心点

    private int mRadius; //最里面白色圆的半径
    private float mRingWidth; //圆环的宽度
    private int mMinCircleColor;    //最里面圆的颜色
    private int mRingNormalColor;    //默认圆环的颜色
    private Paint mPaint;//默认圆环画笔
    //四个圆环画笔
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;
    //4个圆环角度值
    private float sweepAngle1 = 0;
    private float sweepAngle2 = 0;
    private float sweepAngle3 = 0;
    private float sweepAngle4 = 0;

    private RectF mRectF; //圆环的矩形区域

    //处理点击区域
    //圆周率
    private static final float PI = 3.1415f;
    private static final int PART_ZERO = 0;
    private static final int PART_ONE = 1;
    private static final int PART_TWO = 2;
    private static final int PART_THREE = 3;
    private static final int PART_FOUR = 4;
    // 控件点击监听
    private OnClickListener onClickListener;
    private int mTotalRadio; //总点击区域
    // 圆环文字画笔
    private Paint dialWordsPaint;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularGraphView);
        //最里面白色圆的半径
        mRadius = a.getInteger(R.styleable.CircularGraphView_min_circle_radio, 140);
        //圆环宽度
        mRingWidth = a.getFloat(R.styleable.CircularGraphView_ring_width, 60);
        //最里面的圆的颜色(白色)
        mMinCircleColor = a.getColor(R.styleable.CircularGraphView_circle_color, context.getResources().getColor(R.color.white));
        //圆环的默认颜色(圆环占据的是里面的圆的空间)
        mRingNormalColor = a.getColor(R.styleable.CircularGraphView_ring_normal_color, context.getResources().getColor(R.color.color_EFF1F4));
        a.recycle();
        //默认圆环画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        //抗锯齿画笔1
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint1.setAntiAlias(true);
        //抗锯齿画笔2
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setAntiAlias(true);
        //抗锯齿画笔3
        mPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint3.setAntiAlias(true);
        //抗锯齿画笔4
        mPaint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint4.setAntiAlias(true);
        //需要重写onDraw就得调用此
        this.setWillNotDraw(false);
        //圆环文字画笔
        dialWordsPaint = new Paint();
        dialWordsPaint.setAntiAlias(true);
        dialWordsPaint.setStyle(Paint.Style.FILL);
        dialWordsPaint.setTextSize(30.0f);
        dialWordsPaint.setColor(Color.parseColor("#9E9FAF"));

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //view的宽和高,相对于父布局(用于确定圆心)
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mViewCenterX = viewWidth / 2;
        mViewCenterY = viewHeight / 2;
        //画矩形
        mRectF = new RectF(mViewCenterX - mRadius - mRingWidth / 2, mViewCenterY - mRadius - mRingWidth / 2,
                mViewCenterX + mRadius + mRingWidth / 2, mViewCenterY + mRadius + mRingWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mMinCircleColor);
        canvas.drawCircle(mViewCenterX, mViewCenterY, mRadius, mPaint);

        //画默认圆环
        drawNormalRing(canvas);
        //画彩色圆环
        drawColorRing(canvas, mPaint1, -90f,
                sweepAngle1, Color.parseColor("#2CCAA9"));
        drawColorRing(canvas, mPaint2, sweepAngle1,
                sweepAngle2, Color.parseColor("#EAB537"));
        drawColorRing(canvas, mPaint3, sweepAngle2,
                sweepAngle3, Color.parseColor("#2B6DE6"));
        drawColorRing(canvas, mPaint4, sweepAngle3,
                sweepAngle4, Color.parseColor("#103A87"));

    }

    /**
     * 画默认圆环
     *
     * @param canvas
     */
    private void drawNormalRing(Canvas canvas) {
        Paint ringNormalPaint = new Paint(mPaint);
        ringNormalPaint.setStyle(Paint.Style.STROKE);
        ringNormalPaint.setStrokeWidth(mRingWidth);
        ringNormalPaint.setColor(mRingNormalColor);//圆环默认颜色为灰色
        canvas.drawArc(mRectF, 0, 360, false, ringNormalPaint);
    }

    /**
     * 画彩色圆环
     * degrees 旋转角度
     * startAngle 开始角度
     * sweepAngle 可以理解成结束角度
     * color 圆环颜色值
     *
     * @param canvas
     */
    private void drawColorRing(Canvas canvas, Paint paint, float degrees,
                               float sweepAngle, int color) {
        paint.setColor(color);
        Paint ringColorPaint = new Paint(paint);
        ringColorPaint.setStyle(Paint.Style.STROKE);
        ringColorPaint.setStrokeWidth(mRingWidth);
        //   ringColorPaint.setShader(new SweepGradient(mViewCenterX, mViewCenterX, color, null));
        //逆时针旋转角度
        canvas.rotate(degrees, mViewCenterX, mViewCenterY);
        canvas.drawArc(mRectF, (float) 0.0, sweepAngle, false, ringColorPaint);
        ringColorPaint.setShader(null);
    }

    /**
     * 设置当前圆环值
     *
     * @param
     */
    public void setAnnularData(float sweepAngle1, float sweepAngle2,
                               float sweepAngle3, float sweepAngle4) {
        this.sweepAngle1 = sweepAngle1;
        this.sweepAngle2 = sweepAngle2;
        this.sweepAngle3 = sweepAngle3;
        this.sweepAngle4 = sweepAngle4;
        invalidate();
    }

    /**
     * 设置当前圆环宽度比例
     *
     * @param minRadio  内圆半径
     * @param ringWidth 圆环宽度
     */
    public void setProportionData(int minRadio, float ringWidth) {
        this.mRadius = minRadio;
        this.mRingWidth = ringWidth;
        invalidate();
    }

    /**
     * 饼图touch事件
     * 获取触摸位置计算属于哪个区域
     *
     * @param
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX() - mViewCenterX;
            float y = event.getY() - mViewCenterY;
            whichZone(x,y);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断点击了圆的哪个区域
     * @param x
     * @param y
     */
    private void whichZone(float x, float y) {
        // 简单的象限点处理
        // 第一象限在右下角，第二象限在左下角，代数里面的是逆时针，这里是顺时针
        if(x > 0 && y > 0) {
            // 点击事件
            if (onClickListener != null) {
                onClickListener.onClick(PART_ZERO);
            }
        } else if(x > 0 && y < 0) {
            // 点击事件
            if (onClickListener != null) {
                onClickListener.onClick(PART_THREE);
            }
        } else if(x < 0 && y < 0) {
            // 点击事件
            if (onClickListener != null) {
                onClickListener.onClick(PART_TWO);
            }
        } else if(x < 0 && y > 0) {
            // 点击事件
            if (onClickListener != null) {
                onClickListener.onClick(PART_ONE);
            }
        }
    }

    /**
     * 设置点击监听
     *
     * @param onClickListener 点击回调接口
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * 点击回调接口
     */
    public interface OnClickListener {
        /**
         * 点击回调方法
         *
         * @param region 区域类型
         */
        void onClick(int region);
    }
}
