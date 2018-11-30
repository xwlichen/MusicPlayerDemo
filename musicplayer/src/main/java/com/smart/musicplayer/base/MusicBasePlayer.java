package com.smart.musicplayer.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.smart.musicplayer.MusicManager;

/**
 * @date : 2018/11/30 下午1:54
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public abstract class MusicBasePlayer extends MusicControlView {
    public MusicBasePlayer(@NonNull Context context) {
        super(context);
    }

    public MusicBasePlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicBasePlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /*******************************下面方法为管理器和播放控件交互的方法****************************************/

    @Override
    public MusicViewBridge getMusicManager() {
        MusicManager.instance().initContext(getContext().getApplicationContext());
        return MusicManager.instance();
    }


    @Override
    protected void releaseMusic() {
        MusicManager.releaseAllMusic();
    }



}
