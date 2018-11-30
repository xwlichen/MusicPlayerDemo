package com.smart.musicplayer.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.smart.musicplayer.R;

import moe.codeest.enviews.ENDownloadView;

/**
 * @date : 2018/11/30 下午1:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class MusicPlayer extends  MusicControlView
{


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
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(getActivityContext()).inflate(getProgressDialogLayoutId(), null);
            if (localView.findViewById(getProgressDialogProgressId()) instanceof ProgressBar) {
                mDialogProgressBar = ((ProgressBar) localView.findViewById(getProgressDialogProgressId()));
                if (mDialogProgressBarDrawable != null) {
                    mDialogProgressBar.setProgressDrawable(mDialogProgressBarDrawable);
                }
            }
            if (localView.findViewById(getProgressDialogCurrentDurationTextId()) instanceof TextView) {
                mDialogSeekTime = ((TextView) localView.findViewById(getProgressDialogCurrentDurationTextId()));
            }
            if (localView.findViewById(getProgressDialogAllDurationTextId()) instanceof TextView) {
                mDialogTotalTime = ((TextView) localView.findViewById(getProgressDialogAllDurationTextId()));
            }
            if (localView.findViewById(getProgressDialogImageId()) instanceof ImageView) {
                mDialogIcon = ((ImageView) localView.findViewById(getProgressDialogImageId()));
            }
            mProgressDialog = new Dialog(getActivityContext(), com.shuyu.gsyvideoplayer.R.style.video_style_dialog_progress);
            mProgressDialog.setContentView(localView);
            mProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
            mProgressDialog.getWindow().addFlags(32);
            mProgressDialog.getWindow().addFlags(16);
            mProgressDialog.getWindow().setLayout(getWidth(), getHeight());
            if (mDialogProgressNormalColor != -11 && mDialogTotalTime != null) {
                mDialogTotalTime.setTextColor(mDialogProgressNormalColor);
            }
            if (mDialogProgressHighLightColor != -11 && mDialogSeekTime != null) {
                mDialogSeekTime.setTextColor(mDialogProgressHighLightColor);
            }
            WindowManager.LayoutParams localLayoutParams = mProgressDialog.getWindow().getAttributes();
            localLayoutParams.gravity = Gravity.TOP;
            localLayoutParams.width = getWidth();
            localLayoutParams.height = getHeight();
            int location[] = new int[2];
            getLocationOnScreen(location);
            localLayoutParams.x = location[0];
            localLayoutParams.y = location[1];
            mProgressDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        if (mDialogSeekTime != null) {
            mDialogSeekTime.setText(seekTime);
        }
        if (mDialogTotalTime != null) {
            mDialogTotalTime.setText(" / " + totalTime);
        }
        if (totalTimeDuration > 0)
            if (mDialogProgressBar != null) {
                mDialogProgressBar.setProgress(seekTimePosition * 100 / totalTimeDuration);
            }
        if (deltaX > 0) {
            if (mDialogIcon != null) {
                mDialogIcon.setBackgroundResource(com.shuyu.gsyvideoplayer.R.drawable.video_forward_icon);
            }
        } else {
            if (mDialogIcon != null) {
                mDialogIcon.setBackgroundResource(com.shuyu.gsyvideoplayer.R.drawable.video_backward_icon);
            }
        }

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

    }

    @Override
    protected void changeUiToNormal() {

    }

    @Override
    protected void changeUiToPreparingShow() {

    }

    @Override
    protected void changeUiToPlayingShow() {

    }

    @Override
    protected void changeUiToPauseShow() {

    }

    @Override
    protected void changeUiToError() {

    }

    @Override
    protected void changeUiToCompleteShow() {

    }

    @Override
    protected void changeUiToPlayingBufferingShow() {

    }






    @Override
    public int getLayoutId() {
        return R.layout.layout_music_controlview;
    }

    @Override
    public void startPlayLogic() {

        prepareMusic();
    }

    @Override
    public MusicViewBridge getMusicManager() {
        return null;
    }

    @Override
    protected void releaseMusic() {

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
}
