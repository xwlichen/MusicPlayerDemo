package com.smart.musicplayer.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.smart.musicplayer.R;

import moe.codeest.enviews.ENPlayView;

/**
 * @date : 2018/11/30 下午1:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class MusicPlayer extends MusicBasePlayer {


    //触摸进度dialog
    protected Dialog mProgressDialog;

    //触摸进度条的progress
    protected ProgressBar mDialogProgressBar;

    public MusicPlayer(@NonNull Context context) {
        super(context);
    }

    public MusicPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void showWifiDialog() {
        if (!NetworkUtils.isAvailable(mContext)) {
            //Toast.makeText(mContext, getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
            startPlayLogic();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setMessage(getResources().getString(com.shuyu.gsyvideoplayer.R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(com.shuyu.gsyvideoplayer.R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startPlayLogic();
            }
        });
        builder.setNegativeButton(getResources().getString(com.shuyu.gsyvideoplayer.R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 触摸显示滑动进度dialog，如需要自定义继承重写即可，记得重写dismissProgressDialog
     */
    @Override
    @SuppressWarnings("ResourceType")
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {


    }

    @Override
    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    /********************************各类UI的状态显示*********************************************/

    @Override
    protected void onClickUiToggle() {
        if (mCurrentState == CURRENT_STATE_PREPAREING) {

            changeUiToPreparingShow();
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {

            changeUiToPlayingShow();
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {

            changeUiToPauseShow();
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {

            changeUiToCompleteShow();
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {

            changeUiToPlayingBufferingShow();
        }
    }

    @Override
    protected void changeUiToNormal() {
        setViewShowState(playtButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        updateStartImage();
        if (mLoadingProgressBar instanceof ImageView) {
            ((ImageView) mLoadingProgressBar).setVisibility(GONE);
        }

    }

    @Override
    protected void changeUiToPreparingShow() {
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(playtButton, INVISIBLE);

        if (mLoadingProgressBar instanceof ImageView) {
            ImageView enDownloadView = (ImageView) mLoadingProgressBar;
                ((ImageView) mLoadingProgressBar).setVisibility(GONE);

        }
    }

    @Override
    protected void changeUiToPlayingShow() {
        setViewShowState(playtButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(playtButton, VISIBLE);

        if (mLoadingProgressBar instanceof ImageView) {
            ((ImageView) mLoadingProgressBar).setVisibility(GONE);
        }

        updateStartImage();

    }

    @Override
    protected void changeUiToPauseShow() {
        setViewShowState(playtButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        if (mLoadingProgressBar instanceof ImageView) {
            ((ImageView) mLoadingProgressBar).setVisibility(GONE);

        }
        updateStartImage();

    }

    @Override
    protected void changeUiToError() {
        setViewShowState(playtButton, VISIBLE);

        setViewShowState(mLoadingProgressBar, INVISIBLE);

        if (mLoadingProgressBar instanceof ImageView) {
            ((ImageView) mLoadingProgressBar).setVisibility(GONE);
        }
        updateStartImage();

    }

    @Override
    protected void changeUiToCompleteShow() {
        setViewShowState(playtButton, VISIBLE);

        setViewShowState(mLoadingProgressBar, INVISIBLE);
        if (mLoadingProgressBar instanceof ImageView) {
            ((ImageView) mLoadingProgressBar).setVisibility(GONE);
        }
        updateStartImage();

    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        setViewShowState(playtButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        if (mLoadingProgressBar instanceof ImageView) {
            ImageView enDownloadView = (ImageView) mLoadingProgressBar;
//            if (enDownloadView.getCurrentState() == ImageView.STATE_PRE) {
                ((ImageView) mLoadingProgressBar).setVisibility(VISIBLE);
//            }
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.layout_music_controlview;
    }

    @Override
    public void startPlayLogic() {

        prepareMusic();
    }


    /**
     * 触摸进度dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogLayoutId() {
        return com.shuyu.gsyvideoplayer.R.layout.video_progress_dialog;
    }

    /**
     * 触摸进度dialog的进度条id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogProgressId() {
        return com.shuyu.gsyvideoplayer.R.id.duration_progressbar;
    }

    /**
     * 触摸进度dialog的当前时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogCurrentDurationTextId() {
        return com.shuyu.gsyvideoplayer.R.id.tv_current;
    }

    /**
     * 触摸进度dialog全部时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogAllDurationTextId() {
        return com.shuyu.gsyvideoplayer.R.id.tv_duration;
    }

    /**
     * 触摸进度dialog的图片id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected int getProgressDialogImageId() {
        return com.shuyu.gsyvideoplayer.R.id.duration_image_tip;
    }

    /**
     * 定义开始按键显示
     */
    protected void updateStartImage() {
        if (playtButton instanceof ENPlayView) {
            ENPlayView enPlayView = (ENPlayView) playtButton;
            enPlayView.setDuration(500);
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                enPlayView.play();
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                enPlayView.pause();
            } else {
                enPlayView.pause();
            }
        } else if (playtButton instanceof ImageView) {
            ImageView imageView = (ImageView) playtButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_pause_selector);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_error_selector);
            } else {
                imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_play_selector);
            }
        }
    }


}
