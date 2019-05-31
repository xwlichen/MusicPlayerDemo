package com.smart.musicplayer.widget.visualizer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

import com.smart.musicplayer.R;
import com.smart.musicplayer.widget.visualizer.model.AnimSpeed;
import com.smart.musicplayer.widget.visualizer.model.PaintStyle;

/**
 * @date : 2019-05-30 17:08
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public abstract class BaseVisualizer extends TextureView implements TextureView.SurfaceTextureListener {

    public static final float DEFAULT_DENSITY = 0.25f;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final float DEFAULT_STROKE_WIDTH = 6.0f;
    public static final int MAX_ANIM_BATCH_COUNT = 4;

    protected Context context;

    protected byte[] rawAudioBytes;
    protected Paint paint;
    protected int paintColor = DEFAULT_COLOR;

    protected PaintStyle paintStyle = PaintStyle.FILL;

    protected float strokeWidth = DEFAULT_STROKE_WIDTH;
    protected float density = DEFAULT_DENSITY;

    protected AnimSpeed animSpeed = AnimSpeed.MEDIUM;
    protected boolean isVisualizationEnabled = true;


    public BaseVisualizer(Context context) {
        super(context);
        this.context = context;
        init();

    }

    public BaseVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(context, attrs);
        init();
    }

    public BaseVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(context, attrs);
        init();
    }


    protected void initAttrs(Context context, AttributeSet attrs) {
        //get the attributes specified in attrs.xml using the name we included
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BaseVisualizer, 0, 0);
        if (typedArray != null && typedArray.length() > 0) {
            try {
                //get the text and colors specified using the names in attrs.xml
                this.density = typedArray.getFloat(R.styleable.BaseVisualizer_density, DEFAULT_DENSITY);
                this.paintColor = typedArray.getColor(R.styleable.BaseVisualizer_color, DEFAULT_COLOR);
                this.strokeWidth = typedArray.getDimension(R.styleable.BaseVisualizer_width, DEFAULT_STROKE_WIDTH);

                String paintType = typedArray.getString(R.styleable.BaseVisualizer_type);
                if (paintType != null && !paintType.equals(""))
                    this.paintStyle = paintType.toLowerCase().equals("outline") ? PaintStyle.OUTLINE : PaintStyle.FILL;

                String speedType = typedArray.getString(R.styleable.BaseVisualizer_speed);
                if (speedType != null && !speedType.equals("")) {
                    this.animSpeed = AnimSpeed.MEDIUM;
                    if (speedType.toLowerCase().equals("slow"))
                        this.animSpeed = AnimSpeed.SLOW;
                    else if (speedType.toLowerCase().equals("fast"))
                        this.animSpeed = AnimSpeed.FAST;
                }

            } finally {
                typedArray.recycle();
            }
        }

        paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        if (paintStyle == PaintStyle.FILL)
            paint.setStyle(Paint.Style.FILL);
        else {
            paint.setStyle(Paint.Style.STROKE);
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    public byte[] getRawAudioBytes() {
        return rawAudioBytes;
    }

    public void setRawAudioBytes(byte[] rawAudioBytes) {
        this.rawAudioBytes = rawAudioBytes;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public PaintStyle getPaintStyle() {
        return paintStyle;
    }

    public void setPaintStyle(PaintStyle paintStyle) {
        this.paintStyle = paintStyle;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public AnimSpeed getAnimSpeed() {
        return animSpeed;
    }

    public void setAnimSpeed(AnimSpeed animSpeed) {
        this.animSpeed = animSpeed;
    }

    public boolean isVisualizationEnabled() {
        return isVisualizationEnabled;
    }

    public void setVisualizationEnabled(boolean visualizationEnabled) {
        isVisualizationEnabled = visualizationEnabled;
    }

    protected abstract void init();
}
