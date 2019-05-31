package com.smart.musicplayer.widget.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;

import com.smart.musicplayer.widget.visualizer.model.AnimSpeed;
import com.smart.musicplayer.widget.visualizer.model.PaintStyle;

/**
 * @date : 2019-05-30 17:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class BlobVisualizer extends BaseVisualizer {

    private static final int BLOB_MAX_POINTS = 60;
    private static final int BLOB_MIN_POINTS = 3;


    private int width;
    private int height;


    private int pointNum;
    private int radius;
    private PointF[] mBezierPoints;
    private BezierSpline mBezierSpline;
    private float angleOffset;
    private float changeFactor;
    private double angle = 0;
    private Path blobPath;

    //刷新时间
    private static int refreshTime = 20;
    private boolean isDrawing;


    public BlobVisualizer(Context context) {
        super(context);
    }

    public BlobVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlobVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void init() {
        setSurfaceTextureListener(this);//设置监听

        radius = -1;
        pointNum = (int) (density * BLOB_MAX_POINTS);
        if (pointNum < BLOB_MIN_POINTS)
            pointNum = BLOB_MIN_POINTS;

        angleOffset = (360.0f / pointNum);

        updateChangeFactor(animSpeed, false);

        blobPath = new Path();

        //initialize mBezierPoints, 2 extra for the smoothing first and last point
        mBezierPoints = new PointF[pointNum + 2];
//        mBezierPoints = new PointF[pointNum];

        for (int i = 0; i < mBezierPoints.length; i++) {
            mBezierPoints[i] = new PointF();
        }
        Log.e("xw", "mBezierPoints.length: " + mBezierPoints.length);
        mBezierSpline = new BezierSpline(mBezierPoints.length);


    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        isDrawing = true;
        if (width > 0) {
            this.width = width;
            this.height = height;
            radius = height < width ? height : width;
            Log.e("lichen", "radius: " + radius);

            Log.e("lichen", "getWidth(): " + width);
            Log.e("lichen", "getHeight(): " + height);


            radius = (int) (radius * 0.65 / 2);
            Log.e("lichen", "radiused: " + radius);


            changeFactor = height * changeFactor + 20;

            //initialize bezier points
            for (int i = 0; i < pointNum; i++, angle += angleOffset) {
                float posX = (float) (width / 2
                        + (radius)
                        * Math.cos(Math.toRadians(angle)));

                float posY = (float) (height / 2
                        + (radius)
                        * Math.sin(Math.toRadians(angle)));


                mBezierPoints[i].set(posX, posY);
            }
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isDrawing) {
//                    drawCanvas();
                }
            }
        }).start();
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

    public void drawBlob(Canvas canvas) {
        if (width < 0) {
            return;
        }
        //create the path and draw
        if (isVisualizationEnabled && rawAudioBytes != null) {

            if (rawAudioBytes.length == 0) {
                return;
            }
            blobPath.rewind();

            //find the destination bezier point for a batch
            for (int i = 0; i < pointNum; i++, angle += angleOffset) {

                int x = (int) Math.ceil((i + 1) * (rawAudioBytes.length / pointNum));
                int t = 0;
                if (x < 1024)
//                    t = ((byte) (-Math.abs(rawAudioBytes[x]) + 128)) * (canvas.getHeight() / 4) / 128;
                    t = ((byte) (Math.abs(rawAudioBytes[x]) + 128)) * radius / 128;
//                LogUtils.e("t:  "+t);

//                    t = rawAudioBytes[x];


                float posX = (float) (width / 2
                        + (radius + t)
                        * Math.cos(Math.toRadians(angle)));

                float posY = (float) (height / 2
                        + (radius + t)
                        * Math.sin(Math.toRadians(angle)));

                //calculate the new x based on change
//                if (posX - mBezierPoints[i].x > 0) {
//                    mBezierPoints[i].x += changeFactor;
//                } else {
//                    mBezierPoints[i].x -= changeFactor;
//                }
//
//                //calculate the new y based on change
//                if (posY - mBezierPoints[i].y > 0) {
//                    mBezierPoints[i].y += changeFactor;
//                } else {
//                    mBezierPoints[i].y -= changeFactor;
//                }


//                if (rs > 100)
//                    Log.e("lichen", "cha zhi ：" + (posX - mBezierPoints[i].x));
//                else{
//                    Log.e("lichen", "小于100");
//                }
//                Log.e("lichen","changeFactor ："+ (changeFactor));

                float distanceX = Math.abs(posX - mBezierPoints[i].x);
                float distanceY = Math.abs(posY - mBezierPoints[i].y);

                float percentX = distanceX / 128.0f;
                float percentY = distanceY / 128.0f;

//                Log.e("lichen","distanceX ："+ distanceX);
//                Log.e("lichen", "percentX ：" + percentX);
//                Log.e("lichen", "percentY ：" + percentY);
//                Log.e("lichen","distanceY ："+ distanceY);


                changeFactor = 100;

                if (posX - mBezierPoints[i].x > 0) {
                    mBezierPoints[i].x += changeFactor * percentX;
                } else {
                    mBezierPoints[i].x -= changeFactor * percentX;
                }

                //calculate the new y based on change
                if (posY - mBezierPoints[i].y > 0) {
                    mBezierPoints[i].y += changeFactor * percentY;
                } else {
                    mBezierPoints[i].y -= changeFactor * percentY;
                }


            }
            //set the first and last point as first
            mBezierPoints[pointNum].set(mBezierPoints[0].x, mBezierPoints[0].y);
            mBezierPoints[pointNum + 1].set(mBezierPoints[0].x, mBezierPoints[0].y);

            //update the control points
            mBezierSpline.updateCurveControlPoints(mBezierPoints);
            PointF[] firstCP = mBezierSpline.getFirstControlPoints();
            PointF[] secondCP = mBezierSpline.getSecondControlPoints();

            Log.e("lichen", "firstCP len ：" + firstCP.length);

            //create the path
            blobPath.moveTo(mBezierPoints[0].x, mBezierPoints[0].y);
            for (int i = 0; i < firstCP.length; i++) {
                blobPath.cubicTo(firstCP[i].x, firstCP[i].y,
                        secondCP[i].x, secondCP[i].y,
                        mBezierPoints[i + 1].x, mBezierPoints[i + 1].y);
            }
            //add an extra line to center cover the gap generated by last cubicTo
            if (paintStyle == PaintStyle.FILL)
                blobPath.lineTo(width / 2.0f, height / 2.0f);

            canvas.drawPath(blobPath, paint);

        }
    }

    @Override
    public void setAnimSpeed(AnimSpeed animSpeed) {
        super.setAnimSpeed(animSpeed);
        updateChangeFactor(animSpeed, true);
    }

    @Override
    public void setRawAudioBytes(byte[] rawAudioBytes) {
        super.setRawAudioBytes(rawAudioBytes);
        drawCanvas();
    }

    private void updateChangeFactor(AnimSpeed animSpeed, boolean useHeight) {
        int height = 1;
        if (animSpeed == AnimSpeed.SLOW)
            changeFactor = height * 0.003f;
        else if (animSpeed == AnimSpeed.MEDIUM)
            changeFactor = height * 0.006f;
        else
            changeFactor = height * 0.01f;
    }


}
