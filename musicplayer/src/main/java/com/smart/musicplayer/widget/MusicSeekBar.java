package com.smart.musicplayer.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.smart.musicplayer.R;
import com.smart.musicplayer.entity.SeekParams;
import com.smart.musicplayer.listener.OnSeekChangeListener;

import java.math.BigDecimal;


/**
 * @date : 2018/12/11 下午3:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class MusicSeekBar extends ViewGroup {


    int colorFTrack = Color.GRAY;
    int colorSTrack = Color.BLUE;
    int colorTTrack = Color.RED;

    //    Paint paintTrack;
    Paint paintFTrack;
    Paint paintSTrack;
    Paint paintTTrack;

    Path pathFTrack;
    Path pathSTrack;
    Path pathTTrack;


    int thumbWidth;
    int thumbHeight;
    float thumbCenterX;
    float thumbLeft;
    float thumbTop;
    Bitmap thumbBitmap;

    int loadingSize;
    float loadingTop;
    float loadingLeft;


    int trackHeight;
    int trackWidth;
    float mLeft;
    float mRight;
    float mSectionOffset;

    float currentProgress = 200;
    float lastProgress;
    float max = 800;
    float min = 0;


    boolean isTouchToSeek;
    boolean isThumbOnDragging;


    int width;
    int height;

    int paddingLeft, paddingRigtht, paddingTop, paddingBottom;

    float mFaultTolerance = -1;//the tolerance for user seek bar touching
    Context mContext;

    boolean isTouching;
    OnSeekChangeListener mSeekChangeListener;
    int mScale = 1;

    ImageView ivLoading;


    public MusicSeekBar(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public MusicSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        init();
    }

    public MusicSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        init();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int count = getChildCount();
        if (count == 0) {
            addView(ivLoading);
        }
        int aftercount = getChildCount();

        Log.e("xw", "count:" + aftercount);

        ivLoading.layout((int) (thumbLeft+loadingLeft), (int) loadingTop, (int) (thumbLeft+loadingLeft + loadingSize), (int) loadingTop + loadingSize);


    }

    public void init() {
        setWillNotDraw(false);
        thumbBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.classics_seek_bar_point);
        Bitmap loadingBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.loading);
        ivLoading = new ImageView(mContext);
        ivLoading.setImageBitmap(loadingBitmap);

        trackHeight = 20;
        trackWidth = 700;


        mSectionOffset = trackWidth;


        thumbWidth = thumbBitmap.getWidth();
        thumbHeight = thumbBitmap.getHeight();
        loadingSize = loadingBitmap.getWidth();

        initPaint();

//        addView(ivLoading);


    }


    public void initPaint() {
        paintFTrack = new Paint();
        paintFTrack.setAntiAlias(true);
        paintFTrack.setStrokeCap(Paint.Cap.ROUND);
        paintFTrack.setStrokeWidth(trackHeight);
        paintFTrack.setColor(colorFTrack);

        paintSTrack = new Paint();
        paintSTrack.setAntiAlias(true);
        paintSTrack.setStrokeCap(Paint.Cap.ROUND);
        paintSTrack.setStrokeWidth(trackHeight);
        paintSTrack.setColor(colorSTrack);


        paintTTrack = new Paint();
        paintTTrack.setAntiAlias(true);
        paintTTrack.setStrokeCap(Paint.Cap.ROUND);
        paintTTrack.setStrokeWidth(trackHeight);
        paintTTrack.setColor(colorTTrack);


//        paintTrack = new Paint();
//        paintTrack.setAntiAlias(true);
//        paintTrack.setStrokeCap(Paint.Cap.ROUND);
//        paintTrack.setStrokeWidth(trackHeight);


        pathFTrack = new Path();
        pathSTrack = new Path();
        pathTTrack = new Path();


    }

    /**
     * 根据Model返回值
     *
     * @param value
     * @return
     */
    private int measure(int value) {
        int result = 0;
        int specMode = MeasureSpec.getMode(value);
        int specSize = MeasureSpec.getSize(value);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measure(widthMeasureSpec);
        height = measure(heightMeasureSpec);
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        paddingLeft = getPaddingLeft()+thumbWidth/2;
        paddingTop = getPaddingTop();
        paddingRigtht = getPaddingRight()+thumbWidth/2;
        paddingBottom = getPaddingBottom();

        trackLeft = paddingLeft;
        trackTop = (height) / 2;
        trackRight = paddingRigtht;
        trackBottom = trackTop;

        thumbTop = (height - thumbHeight) / 2;

        loadingTop = (height - loadingSize) / 2;

        loadingLeft = (thumbWidth - loadingSize) / 2;


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (thumbBitmap == null) {
            return;
        }

        drawFirstTrack(canvas);


        drawSecondTrack(canvas);

        drawThirdTrack(canvas);


    }


    float trackLeft, trackTop, trackRight, trackBottom;

    public void drawFirstTrack(Canvas canvas) {
        canvas.drawLine(trackLeft, trackTop, trackWidth, trackBottom, paintFTrack);
    }

    public void drawSecondTrack(Canvas canvas) {
        canvas.drawLine(trackLeft, trackTop, 500, trackBottom, paintSTrack);
    }

    public void drawThirdTrack(Canvas canvas) {
        if (thumbLeft-5<=trackLeft){
            canvas.drawLine(trackLeft, trackTop, trackLeft, trackBottom, paintTTrack);

        }else{

            canvas.drawLine(trackLeft, trackTop, thumbLeft-5, trackBottom, paintTTrack);
        }
        Log.e("xw", "thumbLeft:"+thumbLeft);
        Log.e("xw", "trackLeft:"+trackLeft);


        // draw thumb
        Rect src = new Rect(0, 0, thumbBitmap.getWidth(), thumbBitmap.getHeight());
        Rect dst = new Rect((int) (thumbLeft), (int) (thumbTop), (int) (thumbLeft +10), (int) (thumbTop + thumbBitmap.getHeight()));
        canvas.drawBitmap(thumbBitmap, src, dst, null);


    }


    boolean mOnlyThumbDraggable;//only drag the seek bar's thumb can be change the progress

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                float mX = event.getX();
                if (isTouchSeekBar(mX, event.getY())) {
                    if ((mOnlyThumbDraggable && !isTouchThumb(mX))) {
                        return false;
                    }
                    isTouching = true;
                    if (mSeekChangeListener != null) {
                        mSeekChangeListener.onStartTrackingTouch(this);
                    }
                    refreshSeekBar(event);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                refreshSeekBar(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouching = false;
                if (mSeekChangeListener != null) {
                    mSeekChangeListener.onStopTrackingTouch(this);
                }
//                if (!autoAdjustThumb()) {
                refreshLoading();
                invalidate();

//                }

                break;
        }
        return super.onTouchEvent(event);
    }


    private boolean isTouchThumb(float mX) {
        float rawTouchX;
        refreshThumbCenterXByProgress(currentProgress);

        rawTouchX = thumbLeft;

        return rawTouchX - thumbWidth / 2f <= mX && mX <= rawTouchX + thumbWidth / 2f;
    }

    private void refreshThumbCenterXByProgress(float progress) {
        //ThumbCenterX

        thumbLeft = (int) ((progress / max) * ((trackWidth+thumbWidth) * 1.0f));

    }


    private boolean isTouchSeekBar(float mX, float mY) {
        if (mFaultTolerance == -1) {
            mFaultTolerance = CommonUtil.dip2px(mContext, 5);
        }
        boolean inWidthRange = mX >= (paddingLeft - 2 * mFaultTolerance) && mX <= (width - paddingRigtht + 2 * mFaultTolerance);
        boolean inHeightRange = mY >= (trackTop - thumbHeight / 2 - mFaultTolerance) && mY <= (trackTop + thumbHeight / 2 + mFaultTolerance);
        return inWidthRange && inHeightRange;
    }


    public void setOnSeekChangeListener(@NonNull OnSeekChangeListener listener) {
        this.mSeekChangeListener = listener;
    }

    private void setSeekListener(boolean formUser) {
        if (mSeekChangeListener == null) {
            return;
        }
        if (progressChange()) {
            mSeekChangeListener.onSeeking(collectParams(formUser));
        }
    }

    private void refreshSeekBar(MotionEvent event) {
        refreshThumbCenterXByProgress(calculateProgress(calculateTouchX(adjustTouchX(event))));
        setSeekListener(true);
        requestLayout();
        refreshLoading();
        invalidate();
        Log.e("xw", "currentProgress:" + currentProgress);
//        updateIndicator();
    }

    public void refreshLoading(){
        ObjectAnimator rotate = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 359f).setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(ObjectAnimator.INFINITE);
        rotate.start();
        requestLayout();

    }

    private float adjustTouchX(MotionEvent event) {
        float mTouchXCache;
        if (event.getX() < paddingLeft) {
            mTouchXCache = 0;
        } else if (event.getX() > trackWidth ) {
            mTouchXCache = trackWidth;
        } else {
            mTouchXCache = event.getX();
        }
        return mTouchXCache;
    }

    private float calculateProgress(float touchX) {
        lastProgress = currentProgress;
        currentProgress = (min + (getAmplitude())) * (touchX) / (trackWidth * 1.0f);
        return currentProgress;
    }


    private float calculateTouchX(float touchX) {
        float touchXTemp = touchX;
        //make sure the seek bar to seek smoothly always
        // while the tick's count is less than 3(tick's count is 1 or 2.).

        return touchXTemp;
    }

    private float getAmplitude() {
        return (max - min) > 0 ? (max - min) : 1;
    }


    private boolean progressChange() {
        return lastProgress != currentProgress;

    }

    SeekParams mSeekParams;

    private SeekParams collectParams(boolean formUser) {
        if (mSeekParams == null) {
            mSeekParams = new SeekParams(this);
        }
        mSeekParams.progress = getProgress();
        mSeekParams.progressFloat = getProgressFloat();
        mSeekParams.fromUser = formUser;
        //for discrete series seek bar

        return mSeekParams;
    }

    public int getProgress() {
        return Math.round(currentProgress);
    }

    public synchronized float getProgressFloat() {
        BigDecimal bigDecimal = BigDecimal.valueOf(currentProgress);
        return bigDecimal.setScale(mScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
