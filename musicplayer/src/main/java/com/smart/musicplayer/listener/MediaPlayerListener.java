package com.smart.musicplayer.listener;

/**
 * @date : 2018/11/29 下午5:00
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface MediaPlayerListener {
    void onPrepared();

    void onAutoCompletion();

    void onCompletion();

    void onBufferingUpdate(int percent);

    void onSeekComplete();

    void onError(int what, int extra);

    void onInfo(int what, int extra);


    void onMediaPause();

    void onMediaResume();

    void onMediaResume(boolean seek);
}
