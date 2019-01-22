package com.smart.musicplayer.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.smart.musicplayer.R;
import com.smart.musicplayer.entity.SeekParams;
import com.smart.musicplayer.listener.MusicProgressListener;
import com.smart.musicplayer.listener.SeekChangeListener;
import com.smart.musicplayer.widget.MusicSeekBar;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.shuyu.gsyvideoplayer.utils.CommonUtil.getTextSpeed;


/**
 * @date : 2018/11/29 下午5:00
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */

public abstract class MusicControlView extends MusicView implements View.OnClickListener, View.OnTouchListener, SeekChangeListener {


    //手动改变滑动的位置
    protected int mSeekTimePosition;

    //手动滑动的起始偏移位置
    protected int mSeekEndOffset;
    //触摸的是否进度条
    protected boolean mTouchingProgressBar = false;

    //是否改变音量
    protected boolean mChangeVolume = false;

    //是否改变播放进度
    protected boolean mChangePosition = false;


    //是否首次触摸
    protected boolean mFirstTouch = false;


    //是否需要显示流量提示
    protected boolean mNeedShowWifiTip = true;


    //lazy的setup
    protected boolean mSetUpLazy = false;

    //进度条
    protected MusicSeekBar musicSeekBar;
    //时间显示
    protected TextView tvCurrentTime, tvTotalTime;


    //播放按键
    protected View playtButton;
    //上一首按键
    protected View preButton;
    //下一首按键
    protected View nextButton;
    //切换播放模式按键
    protected View modelButton;
    //列表按键
    protected View listButton;

    //loading view
    protected View mLoadingProgressBar;


    //进度定时器
    protected Timer updateProcessTimer;
    //定时器任务
    protected ProgressTimerTask mProgressTimerTask;

    protected MusicProgressListener musicProgressListener;

    public MusicControlView(@NonNull Context context) {
        super(context);
    }

    public MusicControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicControlView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void init(Context context) {
        super.init(context);

        playtButton = findViewById(R.id.playButton);
        preButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        modelButton = findViewById(R.id.modelButton);
        listButton = findViewById(R.id.listButton);

        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        musicSeekBar = findViewById(R.id.musicSeekBar);

        mLoadingProgressBar = musicSeekBar.getLoadingView();


        if (isInEditMode())
            return;

        if (playtButton != null) {
            playtButton.setOnClickListener(this);
        }

        if (preButton != null) {
            preButton.setOnClickListener(this);
        }

        if (nextButton != null) {
            nextButton.setOnClickListener(this);
        }

        if (modelButton != null) {
            modelButton.setOnClickListener(this);
        }

        if (listButton != null) {
            listButton.setOnClickListener(this);
        }

        if (musicSeekBar != null) {
            musicSeekBar.setOnSeekChangeListener(this);
        }


        mSeekEndOffset = CommonUtil.dip2px(getActivityContext(), 50);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Debuger.printfLog(MusicControlView.this.hashCode() + "------------------------------ dismiss onDetachedFromWindow");
        cancelProgressTimer();
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();

    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }

    /**
     * 设置播放显示状态
     *
     * @param state
     */
    @Override
    protected void setStateAndUi(int state) {
        mCurrentState = state;
        if ((state == CURRENT_STATE_NORMAL && isCurrentMediaListener())
                || state == CURRENT_STATE_AUTO_COMPLETE || state == CURRENT_STATE_ERROR) {
            mHadPrepared = false;
        }

        switch (mCurrentState) {
            case CURRENT_STATE_NORMAL:
                if (isCurrentMediaListener()) {
                    Debuger.printfLog(MusicControlView.this.hashCode() + "------------------------------ dismiss CURRENT_STATE_NORMAL");
                    cancelProgressTimer();
                    getMusicManager().releaseMediaPlayer();
                    mBufferPoint = 0;
                    mSaveChangeViewTIme = 0;
                }
                if (mAudioManager != null) {
                    mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
                }
                releaseNetWorkState();
                break;
            case CURRENT_STATE_PREPAREING:
                resetProgressAndTime();
                break;
            case CURRENT_STATE_PLAYING:
                if (isCurrentMediaListener()) {
                    Debuger.printfLog(MusicControlView.this.hashCode() + "------------------------------ CURRENT_STATE_PLAYING");
                    startProgressTimer();
                }
                break;
            case CURRENT_STATE_PAUSE:
                Debuger.printfLog(MusicControlView.this.hashCode() + "------------------------------ CURRENT_STATE_PAUSE");
                startProgressTimer();
                break;
            case CURRENT_STATE_ERROR:
                if (isCurrentMediaListener()) {
                    getMusicManager().releaseMediaPlayer();
                }
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                Debuger.printfLog(MusicControlView.this.hashCode() + "------------------------------ dismiss CURRENT_STATE_AUTO_COMPLETE");
                cancelProgressTimer();
                if (musicSeekBar != null) {
                    musicSeekBar.setMax(100);
                }
                if (tvCurrentTime != null && tvTotalTime != null) {
                    tvCurrentTime.setText(tvTotalTime.getText());
                }

                break;
        }
        resolveUIState(state);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.start) {
            clickStartIcon();
        } else if (i == R.id.surface_container && mCurrentState == CURRENT_STATE_ERROR) {
            if (mMusicAllCallBack != null) {
                Debuger.printfLog("onClickStartError");
                mMusicAllCallBack.onClickStartError(mOriginUrl, mTitle, this);
            }
            prepareMusic();
        }
    }

    /**
     * 双击
     */
    protected GestureDetector gestureDetector = new GestureDetector(getContext().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            touchDoubleUp();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!mChangePosition && !mChangeVolume) {
                onClickUiToggle();
            }
            return super.onSingleTapConfirmed(e);
        }
    });

    /**
     * 进度
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int id = v.getId();
        float x = event.getX();
        float y = event.getY();


        if (id == R.id.progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    cancelProgressTimer();
                    ViewParent vpdown = getParent();
                    while (vpdown != null) {
                        vpdown.requestDisallowInterceptTouchEvent(true);
                        vpdown = vpdown.getParent();
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    Debuger.printfLog(MusicControlView.this.hashCode() + "------------------------------ progress ACTION_UP");
                    startProgressTimer();
                    ViewParent vpup = getParent();
                    while (vpup != null) {
                        vpup.requestDisallowInterceptTouchEvent(false);
                        vpup = vpup.getParent();
                    }
                    break;
            }
        }

        return false;
    }


    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param title         title
     * @return
     */
    @Override
    public boolean setUp(String url, boolean cacheWithPlay, String title) {
        return setUp(url, cacheWithPlay, (File) null, title);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param title         title
     * @return
     */
    @Override
    public boolean setUp(String url, boolean cacheWithPlay, File cachePath, String title) {
        if (super.setUp(url, cacheWithPlay, cachePath, title)) {
            return true;
        }
        return false;
    }


    @Override
    public void onSeeking(SeekParams seekParams) {

    }

    @Override
    public void onStartTrackingTouch(MusicSeekBar seekBar) {
        mTouchingProgressBar = true;

    }

    /***
     * 拖动进度条
     */
    @Override
    public void onStopTrackingTouch(MusicSeekBar seekBar) {
        mTouchingProgressBar = false;

        if (mMusicAllCallBack != null && isCurrentMediaListener()) {

            Debuger.printfLog("onClickSeekbar");
            mMusicAllCallBack.onClickSeekbar(mOriginUrl, mTitle, this);
        }
        if (getMusicManager() != null && mHadPlay) {
            try {
                int time = seekBar.getProgress() * getDuration() / 100;
                getMusicManager().seekTo(time);
                getMusicManager().start();
                setStateAndUi(CURRENT_STATE_PLAYING);
            } catch (Exception e) {
                Debuger.printfWarning(e.toString());
            }
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        if (mCurrentState != CURRENT_STATE_PREPAREING) return;
        startProgressTimer();

        Debuger.printfLog(MusicControlView.this.hashCode() + "------------------------------ surface_container onPrepared");
    }


    @Override
    public void onBufferingUpdate(final int percent) {
        post(new Runnable() {
            @Override
            public void run() {
                Log.e("xw", "onBufferingUpdate:" + percent);
                if (mCurrentState != CURRENT_STATE_NORMAL && mCurrentState != CURRENT_STATE_PREPAREING) {
                    Log.e("xw", "percent:" + percent);

                    if (percent != 0) {
                        setTextAndProgress(percent);
                        mBufferPoint = percent;
                        Debuger.printfLog("Net speed: " + getNetSpeedText() + " percent " + percent);
                    }
                    if (musicSeekBar == null) {
                        return;
                    }
                    //循环清除进度
                    if (mLooping && mHadPlay && percent == 0 && musicSeekBar.getProgress() >= (musicSeekBar.getMax() - 1)) {
                        loopSetProgressAndTime();
                    }
                }
            }
        });
    }

    /**
     * 增对列表优化，在播放前的时候才进行setup
     */
    @Override
    protected void prepareMusic() {
        if (mSetUpLazy) {
            super.setUp(mOriginUrl,
                    mCache,
                    mCachePath,
                    mMapHeadData,
                    mTitle);
        }
        super.prepareMusic();
    }


    /**
     * 双击暂停/播放
     * 如果不需要，重载为空方法即可
     */
    protected void touchDoubleUp() {
        if (!mHadPlay) {
            return;
        }
        clickStartIcon();
    }

    /**
     * 处理控制显示
     *
     * @param state
     */
    protected void resolveUIState(int state) {
        switch (state) {
            case CURRENT_STATE_NORMAL:
                changeUiToNormal();
                break;
            case CURRENT_STATE_PREPAREING:
                changeUiToPreparingShow();
                break;
            case CURRENT_STATE_PLAYING:
                changeUiToPlayingShow();
                break;
            case CURRENT_STATE_PAUSE:
                changeUiToPauseShow();
                break;
            case CURRENT_STATE_ERROR:
                changeUiToError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                changeUiToCompleteShow();
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_START:
                changeUiToPlayingBufferingShow();
                break;
        }
    }


    /**
     * 播放按键点击
     */
    protected void clickStartIcon() {
        if (TextUtils.isEmpty(mUrl)) {
            Debuger.printfError("********" + getResources().getString(R.string.no_url));
            //Toast.makeText(getActivityContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR) {
            if (isShowNetConfirm()) {
                showWifiDialog();
                return;
            }
            startButtonLogic();
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            try {
                onMediaPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setStateAndUi(CURRENT_STATE_PAUSE);
            if (mMusicAllCallBack != null && isCurrentMediaListener()) {

                mMusicAllCallBack.onClickStop(mOriginUrl, mTitle, this);
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mMusicAllCallBack != null && isCurrentMediaListener()) {

                Debuger.printfLog("onClickResume");
                mMusicAllCallBack.onClickResume(mOriginUrl, mTitle, this);
            }
            try {
                getMusicManager().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setStateAndUi(CURRENT_STATE_PLAYING);
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButtonLogic();
        }
    }


    protected void startProgressTimer() {
        cancelProgressTimer();
        updateProcessTimer = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        updateProcessTimer.schedule(mProgressTimerTask, 0, 300);
    }

    protected void cancelProgressTimer() {
        if (updateProcessTimer != null) {
            updateProcessTimer.cancel();
            updateProcessTimer = null;
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
            mProgressTimerTask = null;
        }

    }

    protected void setTextAndProgress(int secProgress) {
        int position = getCurrentPositionWhenPlaying();
        int duration = getDuration();
        int progress = position * 100 / (duration == 0 ? 1 : duration);
        setProgressAndTime(progress, secProgress, position, duration);
    }

    protected void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {

        if (musicProgressListener != null && mCurrentState == CURRENT_STATE_PLAYING) {
            musicProgressListener.onProgress(progress, secProgress, currentTime, totalTime);
        }

        if (musicSeekBar == null || tvTotalTime == null || tvCurrentTime == null) {
            return;
        }

        if (!mTouchingProgressBar) {
            if (progress != 0) musicSeekBar.setCurrentProgress(progress);
        }
        if (getMusicManager().getBufferedPercentage() > 0) {
            secProgress = getMusicManager().getBufferedPercentage();
        }
        if (secProgress > 94) secProgress = 100;
//        setSecondaryProgress(secProgress);
        tvTotalTime.setText(CommonUtil.stringForTime(totalTime));
        if (currentTime > 0)
            tvCurrentTime.setText(CommonUtil.stringForTime(currentTime));

        if (secProgress > 0) {
            Log.e("xw", "setSecondProgress:" + secProgress);
            musicSeekBar.setSecondProgress(secProgress);
        }
    }

//    protected void setSecondaryProgress(int secProgress) {
//        if (musicSeekBar != null) {
//            if (secProgress != 0 && !getMusicManager().isCacheFile()) {
//                musicSeekBar.setSecondProgress(secProgress);
//            }
//        }
//
//    }

    protected void resetProgressAndTime() {
        if (musicSeekBar == null || tvTotalTime == null || tvCurrentTime == null) {
            return;
        }
        musicSeekBar.setCurrentProgress(0);
        musicSeekBar.setSecondProgress(0);
        tvCurrentTime.setText(CommonUtil.stringForTime(0));
        tvTotalTime.setText(CommonUtil.stringForTime(0));


    }


    protected void loopSetProgressAndTime() {
        if (musicSeekBar == null || tvTotalTime == null || tvCurrentTime == null) {
            return;
        }
        musicSeekBar.setCurrentProgress(0);
        musicSeekBar.setSecondProgress(0);
        tvCurrentTime.setText(CommonUtil.stringForTime(0));

    }


    protected void setViewShowState(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }


    protected boolean isShowNetConfirm() {
        return !mOriginUrl.startsWith("file") && !mOriginUrl.startsWith("android.resource") && !CommonUtil.isWifiConnected(getContext())
                && mNeedShowWifiTip && !getMusicManager().cachePreview(mContext.getApplicationContext(), mCachePath, mOriginUrl);
    }

    private class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE) {
                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                setTextAndProgress(0);
                            }
                        }
                );
            }
        }
    }


    /************************* 继承之后可自定义ui与显示隐藏 *************************/

    protected abstract void showWifiDialog();

    protected abstract void showProgressDialog(float deltaX,
                                               String seekTime, int seekTimePosition,
                                               String totalTime, int totalTimeDuration);

    protected abstract void dismissProgressDialog();

    protected abstract void onClickUiToggle();

    protected abstract void changeUiToNormal();

    protected abstract void changeUiToPreparingShow();

    protected abstract void changeUiToPlayingShow();

    protected abstract void changeUiToPauseShow();

    protected abstract void changeUiToError();

    protected abstract void changeUiToCompleteShow();

    protected abstract void changeUiToPlayingBufferingShow();


    /************************* 开放接口 *************************/


    /**
     * 在点击播放的时候才进行真正setup
     */
    public boolean setUpLazy(String url, boolean cacheWithPlay, File cachePath, Map<String, String> mapHeadData, String title) {
        mOriginUrl = url;
        mCache = cacheWithPlay;
        mCachePath = cachePath;
        mSetUpLazy = true;
        mTitle = title;
        mMapHeadData = mapHeadData;
        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - mSaveChangeViewTIme) < CHANGE_DELAY_TIME)
            return false;
        mUrl = "waiting";
        mCurrentState = CURRENT_STATE_NORMAL;
        return true;
    }

    /**
     * 初始化为正常状态
     */
    public void initUIState() {
        setStateAndUi(CURRENT_STATE_NORMAL);
    }


    /**
     * 获取播放按键
     */
    public View getPlayButton() {
        return playtButton;
    }


    public boolean isNeedShowWifiTip() {
        return mNeedShowWifiTip;
    }


    /**
     * 是否需要显示流量提示,默认true
     */
    public void setNeedShowWifiTip(boolean needShowWifiTip) {
        this.mNeedShowWifiTip = needShowWifiTip;
    }


    /**
     * 进度回调
     */
    public void setMusicProgressListener(MusicProgressListener videoProgressListener) {
        this.musicProgressListener = videoProgressListener;
    }

    /**
     * 网络速度
     * 注意，这里如果是开启了缓存，因为读取本地代理，缓存成功后还是存在速度的
     * 再打开已经缓存的本地文件，网络速度才会回0.因为是播放本地文件了
     */
    public long getNetSpeed() {
        return getMusicManager().getNetSpeed();
    }

    /**
     * 网络速度
     * 注意，这里如果是开启了缓存，因为读取本地代理，缓存成功后还是存在速度的
     * 再打开已经缓存的本地文件，网络速度才会回0.因为是播放本地文件了
     */
    public String getNetSpeedText() {
        long speed = getNetSpeed();
        return getTextSpeed(speed);
    }
}
