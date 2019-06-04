package com.smart.musicplayer.widget.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.smart.musicplayer.widget.visualizer.model.PaintStyle;


/**
 * @author maple on 2019/4/25 10:17.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class HiFiVisualizer extends BaseVisualizer {
    private static final int BAR_MAX_POINTS = 240;
    private static final int BAR_MIN_POINTS = 30;
    private static final float PER_RADIUS = .65f;
    private int mRadius;
    private int mPoints;
    private int[] mHeights;
    private Path mPath;//outward path
    private Path mPath1;//inward path
    /**
     * This is the distance from center to bezier control point.
     * We can calculate the bezier control points of each segment this distance and its angle;
     */
    private int mBezierControlPointLen;

    public HiFiVisualizer(Context context) {
        super(context);
    }

    public HiFiVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HiFiVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        mRadius = -1;
        mPath = new Path();
        mPath1 = new Path();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.0f);
        mPoints = (int) (BAR_MAX_POINTS * density);
        if (mPoints < BAR_MIN_POINTS) mPoints = BAR_MIN_POINTS;
        mHeights = new int[mPoints];
    }

    /**
     * you cannot change the style of paint;
     * the paintStyle fixed at Paint.Style.STROKE:
     *
     * @param paintStyle style of the visualizer.
     */
    @Override
    @Deprecated()
    public void setPaintStyle(PaintStyle paintStyle) {

    }


    public void drawCanvas() {
        Canvas canvas = null;
        long t = System.currentTimeMillis();
        try {
            canvas = lockCanvas();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            drawBlob(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                unlockCanvasAndPost(canvas);
            }
//            SystemClock.sleep(Math.max(refreshTime - (System.currentTimeMillis() - t), 0));
        }
    }

    protected void drawBlob(Canvas canvas) {
        if (mRadius == -1) {
            mRadius = (int) (Math.min(getWidth(), getHeight()) / 2 * PER_RADIUS);
            mBezierControlPointLen = (int) (mRadius / Math.cos(Math.PI / mPoints));
        }
        updateData();
        mPath.reset();
        mPath1.reset();
        // start the outward path from the last point
        float cxL = (float) (getWidth() / 2 + Math.cos((360 - 360 / mPoints) * Math.PI / 180) * (mRadius + mHeights[mPoints - 1]));
        float cyL = (float) (getHeight() / 2 - Math.sin((360 - 360 / mPoints) * Math.PI / 180) * (mRadius + mHeights[mPoints - 1]));
        mPath.moveTo(cxL, cyL);
        // start the inward path from the last point
        float cxL1 = (float) (getWidth() / 2 + Math.cos((360 - 360 / mPoints) * Math.PI / 180) * (mRadius - mHeights[mPoints - 1]));
        float cyL1 = (float) (getHeight() / 2 - Math.sin((360 - 360 / mPoints) * Math.PI / 180) * (mRadius - mHeights[mPoints - 1]));
        mPath1.moveTo(cxL1, cyL1);
        for (int i = 0; i < 360; i = i + 360 / mPoints) {
            // outward
            // the next point of path
            float cx = (float) (getWidth() / 2 + Math.cos(i * Math.PI / 180) * (mRadius + mHeights[i * mPoints / 360]));
            float cy = (float) (getHeight() / 2 - Math.sin(i * Math.PI / 180) * (mRadius + mHeights[i * mPoints / 360]));
            //second bezier control point
            float bx = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[i * mPoints / 360]));
            float by = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[i * mPoints / 360]));
            int lastPoint = i == 0 ? mPoints - 1 : i * mPoints / 360 - 1;
            //fist bezier control point
            float ax = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[lastPoint]));
            float ay = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[lastPoint]));
            mPath.cubicTo(ax, ay, bx, by, cx, cy);
            // inward
            float cx1 = (float) (getWidth() / 2 + Math.cos(i * Math.PI / 180) * (mRadius - mHeights[i * mPoints / 360]));
            float cy1 = (float) (getHeight() / 2 - Math.sin(i * Math.PI / 180) * (mRadius - mHeights[i * mPoints / 360]));
            float bx1 = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[i * mPoints / 360]));
            float by1 = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[i * mPoints / 360]));
            float ax1 = (float) (getWidth() / 2 + Math.cos((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[lastPoint]));
            float ay1 = (float) (getHeight() / 2 - Math.sin((i - (180 / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[lastPoint]));
            mPath1.cubicTo(ax1, ay1, bx1, by1, cx1, cy1);
            canvas.drawLine(cx, cy, cx1, cy1, paint);
        }
        canvas.drawPath(mPath, paint);
        canvas.drawPath(mPath1, paint);
    }

    private void updateData() {
        if (isVisualizationEnabled && rawAudioBytes != null) {
            if (rawAudioBytes.length == 0) return;
            for (int i = 0; i < mHeights.length; i++) {
                int x = (int) Math.ceil((i + 1) * (rawAudioBytes.length / mPoints));
                int t = 0;
                if (x < 1024)
                    t = ((byte) (Math.abs(rawAudioBytes[x]) + 128)) * mRadius / 128;
                mHeights[i] = -t;
            }
        }
    }

    @Override
    public void setRawAudioBytes(byte[] rawAudioBytes) {
        super.setRawAudioBytes(rawAudioBytes);
        drawCanvas();
    }
}
