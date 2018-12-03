package com.smart.musicplayer.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * @date : 2018/12/3 下午6:25
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class MusicProgressBar extends SeekBar {
    public MusicProgressBar(Context context) {
        super(context);
    }

    public MusicProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MusicProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
