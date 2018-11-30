package com.smart.musicplayer.listener;

/**
 * @date : 2018/11/29 下午5:00
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface MusicAllCallBack {

    //开始加载，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onStartPrepared(String url, Object... objects);

    //加载成功，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onPrepared(String url, Object... objects);

    //点击了开始按键播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onClickStartIcon(String url, Object... objects);

    //点击了错误状态下的开始按键，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onClickStartError(String url, Object... objects);

    //点击了播放状态下的开始按键--->停止，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onClickStop(String url, Object... objects);


    //点击了暂停状态下的开始按键--->播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onClickResume(String url, Object... objects);


    //点击了空白弹出seekbar，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onClickSeekbar(String url, Object... objects);



    //播放完了，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onAutoComplete(String url, Object... objects);





    //触摸调整进度，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onTouchScreenSeekPosition(String url, Object... objects);



    //播放错误，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
    void onPlayError(String url, Object... objects);





}
