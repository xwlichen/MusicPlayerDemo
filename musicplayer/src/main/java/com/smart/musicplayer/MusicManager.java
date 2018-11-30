package com.smart.musicplayer;


import android.annotation.SuppressLint;

import com.smart.musicplayer.listener.MediaPlayerListener;


/**
 * 视频管理，单例
 * Created by shuyu on 2016/11/11.
 */

public class MusicManager extends MusicBaseManager {

    public static String TAG = "MusicManager";

    @SuppressLint("StaticFieldLeak")
    private static MusicManager musicManager;


    private MusicManager() {
        init();
    }

    /**
     * 单例管理器
     */
    public static synchronized MusicManager instance() {
        if (musicManager == null) {
            musicManager = new MusicManager();
        }
        return musicManager;
    }

    /**
     * 同步创建一个临时管理器
     */
    public static synchronized MusicManager tmpInstance(MediaPlayerListener listener) {
        MusicManager gsyVideoManager = new MusicManager();
        gsyVideoManager.bufferPoint = musicManager.bufferPoint;
        gsyVideoManager.optionModelList = musicManager.optionModelList;
        gsyVideoManager.playTag = musicManager.playTag;
        gsyVideoManager.context = musicManager.context;
        gsyVideoManager.lastState = musicManager.lastState;
        gsyVideoManager.playPosition = musicManager.playPosition;
        gsyVideoManager.timeOut = musicManager.timeOut;
        gsyVideoManager.needMute = musicManager.needMute;
        gsyVideoManager.needTimeOutOther = musicManager.needTimeOutOther;
        gsyVideoManager.setListener(listener);
        return gsyVideoManager;
    }

    /**
     * 替换管理器
     */
    public static synchronized void changeManager(MusicManager gsyVideoManager) {
        musicManager = gsyVideoManager;
    }



    /**
     * 页面销毁了记得调用是否所有的video
     */
    public static void releaseAllMusic() {
        if (MusicManager.instance().listener() != null) {
            MusicManager.instance().listener().onCompletion();
        }
        MusicManager.instance().releaseMediaPlayer();
    }


    /**
     * 暂停播放
     */
    public static void onPause() {
        if (MusicManager.instance().listener() != null) {
            MusicManager.instance().listener().onMediaPause();
        }
    }

    /**
     * 恢复播放
     */
    public static void onResume() {
        if (MusicManager.instance().listener() != null) {
            MusicManager.instance().listener().onMediaResume();
        }
    }


    /**
     * 恢复暂停状态
     *
     * @param seek 是否产生seek动作,直播设置为false
     */
    public static void onResume(boolean seek) {
        if (MusicManager.instance().listener() != null) {
            MusicManager.instance().listener().onMediaResume(seek);
        }
    }



}