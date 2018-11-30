package com.smart.musicplayer.listener;

/**
 * @date : 2018/11/29 下午5:00
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface MusicProgressListener {
    /**
     * @param progress 当前播放进度（暂停后再播放可能会有跳动）
     * @param secProgress 当前内存缓冲进度（可能会有0值）
     * @param currentPosition 当前播放位置（暂停后再播放可能会有跳动）
     * @param duration 总时长
     */
    void onProgress(int progress, int secProgress, int currentPosition, int duration);
}
