package com.smart.musicplayer.base;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.NetInfoModule;
import com.smart.musicplayer.listener.MediaPlayerListener;
import com.smart.musicplayer.listener.MusicAllCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @date : 2018/11/29 下午5:00
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public abstract class MusicView extends FrameLayout implements MediaPlayerListener {

    //正常
    public static final int CURRENT_STATE_NORMAL = 0;
    //准备中
    public static final int CURRENT_STATE_PREPAREING = 1;
    //播放中
    public static final int CURRENT_STATE_PLAYING = 2;
    //开始缓冲
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3;
    //暂停
    public static final int CURRENT_STATE_PAUSE = 5;
    //自动播放结束
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    //错误状态
    public static final int CURRENT_STATE_ERROR = 7;

    //避免切换时频繁setup
    public static final int CHANGE_DELAY_TIME = 2000;

    //当前的播放状态
    protected int mCurrentState = -1;

    //播放的tag，防止错误，因为普通的url也可能重复
    protected int mPlayPosition = -22;



    //缓存进度
    protected int mBufferPoint;

    //备份缓存前的播放状态
    protected int mBackUpPlayingBufferState = -1;

    //从哪个开始播放
    protected long mSeekOnStart = -1;

    //当前的播放位置
    protected long mCurrentPosition;

    //保存切换时的时间，避免频繁契合
    protected long mSaveChangeViewTIme = 0;

    //播放速度
    protected float mSpeed = 1;

    //是否播边边缓冲
    protected boolean mCache = false;


    //循环
    protected boolean mLooping = false;

    //是否播放过
    protected boolean mHadPlay = false;

    //是否发送了网络改变
    protected boolean mNetChanged = false;

    //是否不变调
    protected boolean mSoundTouch = false;

    //是否需要显示暂停锁定效果
    protected boolean mShowPauseCover = false;

    //是否准备完成前调用了暂停
    protected boolean mPauseBeforePrepared = false;

    //Prepared之后是否自动开始播放
    protected boolean mStartAfterPrepared = true;

    //Prepared
    protected boolean mHadPrepared = false;

    //是否播放器当失去音频焦点
    protected boolean mReleaseWhenLossAudio = true;

    //音频焦点的监听
    protected AudioManager mAudioManager;

    //播放的tag，防止错误，因为普通的url也可能重复
    protected String mPlayTag = "";

    //上下文
    protected Context mContext;

    //原来的url
    protected String mOriginUrl;

    //转化后的URL
    protected String mUrl;

    //标题
    protected String mTitle;

    //网络状态
    protected String mNetSate = "NORMAL";

    //缓存路径，可不设置
    protected File mCachePath;

    //音频回调
    protected MusicAllCallBack mMusicAllCallBack;

    //http request header
    protected Map<String, String> mMapHeadData = new HashMap<>();

    //网络监听
    protected NetInfoModule mNetInfoModule;



    /**
     * 当前UI
     */
    public abstract int getLayoutId();


    /**
     * 开始播放
     */
    public abstract void startPlayLogic();

    /**
     * 获取管理器桥接的实现
     */
    public abstract MusicViewBridge getMusicManager();

    /**
     * 设置播放显示状态
     *
     * @param state
     */
    protected abstract void setStateAndUi(int state);

    /**
     * 释放播放器
     */
    protected abstract void releaseMusic();


    /**
     * 从哪里开始播放
     * 目前有时候前几秒有跳动问题，毫秒
     * 需要在startPlayLogic之前，即播放开始之前
     */
    public void setSeekOnStart(long seekOnStart) {
        this.mSeekOnStart = seekOnStart;
    }




    public MusicView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MusicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    protected void init(Context context) {

        if (getActivityContext() != null) {
            this.mContext = getActivityContext();
        } else {
            this.mContext = context;
        }

        initInflate(mContext);

        if (isInEditMode())
            return;
        mAudioManager = (AudioManager) getActivityContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

    }


    protected Context getActivityContext() {
        return CommonUtil.getActivityContext(getContext());
    }

    protected void initInflate(Context context) {
        try {
            View.inflate(context, getLayoutId(), this);
        } catch (InflateException e) {
            if (e.toString().contains("GSYImageCover")) {
                Debuger.printfError("********************\n" +
                        "*****   注意   *****" +
                        "********************\n" +
                        "*该版本需要清除布局文件中的GSYImageCover\n" +
                        "****  Attention  ***\n" +
                        "*Please remove GSYImageCover from Layout in this Version\n" +
                        "********************\n");
                e.printStackTrace();
                throw new InflateException("该版本需要清除布局文件中的GSYImageCover，please remove GSYImageCover from your layout");
            } else {
                e.printStackTrace();
            }
        }
    }




    /**
     * 开始播放逻辑
     */
    protected void startButtonLogic() {
        if (mMusicAllCallBack != null && mCurrentState == CURRENT_STATE_NORMAL) {
            Debuger.printfLog("onClickStartIcon");
            mMusicAllCallBack.onClickStartIcon(mOriginUrl, mTitle, this);
        } else if (mMusicAllCallBack != null) {
            Debuger.printfLog("onClickStartError");
            mMusicAllCallBack.onClickStartError(mOriginUrl, mTitle, this);
        }
        prepareMusic();
    }

    /**
     * 开始状态视频播放
     */
    protected void prepareMusic() {
        startPrepare();
    }

    protected void startPrepare() {
        if (getMusicManager().listener() != null) {
            getMusicManager().listener().onCompletion();
        }
        if (mMusicAllCallBack != null) {
            Debuger.printfLog("onStartPrepared");
            mMusicAllCallBack.onStartPrepared(mOriginUrl, mTitle, this);
        }
        getMusicManager().setListener(this);
        getMusicManager().setPlayTag(mPlayTag);
        getMusicManager().setPlayPosition(mPlayPosition);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        ((Activity) getActivityContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBackUpPlayingBufferState = -1;
        getMusicManager().prepare(mUrl, (mMapHeadData == null) ? new HashMap<String, String>() : mMapHeadData, mLooping, mSpeed, mCache, mCachePath);
        setStateAndUi(CURRENT_STATE_PREPAREING);
    }




    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param title         title
     * @return
     */
    public boolean setUp(String url, boolean cacheWithPlay, String title) {
        return setUp(url, cacheWithPlay, ((File) null), title);
    }


    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   头部信息
     * @param title         title
     * @return
     */
    public boolean setUp(String url, boolean cacheWithPlay, File cachePath, Map<String, String> mapHeadData, String title) {
        if (setUp(url, cacheWithPlay, cachePath, title)) {
            if (this.mMapHeadData != null) {
                this.mMapHeadData.clear();
            } else {
                this.mMapHeadData = new HashMap<>();
            }
            if (mapHeadData != null) {
                this.mMapHeadData.putAll(mapHeadData);
            }
            return true;
        }
        return false;
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
    public boolean setUp(String url, boolean cacheWithPlay, File cachePath, String title) {
        return setUp(url, cacheWithPlay, cachePath, title, true);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param title         title
     * @param changeState   是否修改状态
     * @return
     */
    protected boolean setUp(String url, boolean cacheWithPlay, File cachePath, String title, boolean changeState) {
        mCache = cacheWithPlay;
        mCachePath = cachePath;
        mOriginUrl = url;
        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - mSaveChangeViewTIme) < CHANGE_DELAY_TIME)
            return false;
        mCurrentState = CURRENT_STATE_NORMAL;
        this.mUrl = url;
        this.mTitle = title;
        if (changeState)
            setStateAndUi(CURRENT_STATE_NORMAL);
        return true;
    }


    /**
     * 重置
     */
    public void onMusicReset() {
        setStateAndUi(CURRENT_STATE_NORMAL);
    }
















    /**
     * 监听是否有外部其他多媒体开始播放
     */
    protected AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    onGankAudio();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    onLossAudio();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    onLossTransientAudio();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    onLossTransientCanDuck();
                    break;
            }
        }
    };



    protected void onGankAudio() {
    }

    /**
     * 失去了Audio Focus，并将会持续很长的时间
     */
    protected void onLossAudio() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (MusicView.this.mReleaseWhenLossAudio) {
                    MusicView.this.releaseMusic();
                } else {
                    MusicView.this.onMediaPause();
                }

            }
        });
    }

    /**
     * 暂时失去Audio Focus，并会很快再次获得
     */
    protected void onLossTransientAudio() {
        try {
            this.onMediaPause();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    /**
     * 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量
     */
    protected void onLossTransientCanDuck() {
    }


    @Override
    public void onMediaPause() {
        if (mCurrentState == CURRENT_STATE_PREPAREING) {
            mPauseBeforePrepared = true;
        }
        try {
            if (getMusicManager() != null &&
                    getMusicManager().isPlaying()) {
                setStateAndUi(CURRENT_STATE_PAUSE);
                mCurrentPosition = getMusicManager().getCurrentPosition();
                if (getMusicManager() != null)
                    getMusicManager().pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复暂停状态
     */
    @Override
    public void onMediaResume() {
        onMediaResume(true);
    }

    /**
     * 恢复暂停状态
     *
     * @param seek 是否产生seek动作
     */
    @Override
    public void onMediaResume(boolean seek) {
        mPauseBeforePrepared = false;
        if (mCurrentState == CURRENT_STATE_PAUSE) {
            try {
                if (mCurrentPosition > 0 && getMusicManager() != null) {
                    if (seek) {
                        getMusicManager().seekTo(mCurrentPosition);
                    }
                    getMusicManager().start();
                    setStateAndUi(CURRENT_STATE_PLAYING);
                    if (mAudioManager != null && !mReleaseWhenLossAudio) {
                        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    }
                    mCurrentPosition = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理因切换网络而导致的问题
     */
    protected void netWorkErrorLogic() {
        final long currentPosition = getCurrentPositionWhenPlaying();
        Debuger.printfError("******* Net State Changed. renew player to connect *******" + currentPosition);
        getMusicManager().releaseMediaPlayer();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setSeekOnStart(currentPosition);
                startPlayLogic();
            }
        }, 500);
    }


    /**
     * 播放错误的时候，删除缓存文件
     */
    protected void deleteCacheFileWhenError() {
        clearCurrentCache();
        Debuger.printfError("Link Or mCache Error, Please Try Again " + mOriginUrl);
        if (mCache) {
            Debuger.printfError("mCache Link " + mUrl);
        }
        mUrl = mOriginUrl;
    }

    @Override
    public void onPrepared() {

        if (mCurrentState != CURRENT_STATE_PREPAREING) return;

        mHadPrepared = true;

        if (mMusicAllCallBack != null && isCurrentMediaListener()) {
            Debuger.printfLog("onPrepared");
            mMusicAllCallBack.onPrepared(mOriginUrl, mTitle, this);
        }

        if (!mStartAfterPrepared) {
            setStateAndUi(CURRENT_STATE_PAUSE);
            return;
        }

        startAfterPrepared();
    }

    @Override
    public void onAutoCompletion() {
        setStateAndUi(CURRENT_STATE_AUTO_COMPLETE);

        mSaveChangeViewTIme = 0;
        mCurrentPosition = 0;

        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        ((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        releaseNetWorkState();

        if (mMusicAllCallBack != null && isCurrentMediaListener()) {
            Debuger.printfLog("onAutoComplete");
            mMusicAllCallBack.onAutoComplete(mOriginUrl, mTitle, this);
        }
    }

    @Override
    public void onCompletion() {
        //make me normal first
        setStateAndUi(CURRENT_STATE_NORMAL);

        mSaveChangeViewTIme = 0;
        mCurrentPosition = 0;


        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        ((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        releaseNetWorkState();

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onError(int what, int extra) {

        if (mNetChanged) {
            mNetChanged = false;
            netWorkErrorLogic();
            if (mMusicAllCallBack != null) {
                mMusicAllCallBack.onPlayError(mOriginUrl, mTitle, this);
            }
            return;
        }

        if (what != 38 && what != -38) {
            setStateAndUi(CURRENT_STATE_ERROR);
            deleteCacheFileWhenError();
            if (mMusicAllCallBack != null) {
                mMusicAllCallBack.onPlayError(mOriginUrl, mTitle, this);
            }
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mBackUpPlayingBufferState = mCurrentState;
            //避免在onPrepared之前就进入了buffering，导致一只loading
            if (mHadPlay && mCurrentState != CURRENT_STATE_PREPAREING && mCurrentState > 0)
                setStateAndUi(CURRENT_STATE_PLAYING_BUFFERING_START);

        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (mBackUpPlayingBufferState != -1) {
                if (mBackUpPlayingBufferState == CURRENT_STATE_PLAYING_BUFFERING_START) {
                    mBackUpPlayingBufferState = CURRENT_STATE_PLAYING;
                }
                if (mHadPlay && mCurrentState != CURRENT_STATE_PREPAREING && mCurrentState > 0)
                    setStateAndUi(mBackUpPlayingBufferState);

                mBackUpPlayingBufferState = -1;
            }
        }
    }





    /**
     * 获取当前播放进度
     */
    public int getCurrentPositionWhenPlaying() {
        int position = 0;
        if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE) {
            try {
                position = (int) getMusicManager().getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
                return position;
            }
        }
        if (position == 0 && mCurrentPosition > 0) {
            return (int) mCurrentPosition;
        }
        return position;
    }

    /**
     * 获取当前总时长
     */
    public int getDuration() {
        int duration = 0;
        try {
            duration = (int) getMusicManager().getDuration();
        } catch (Exception e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    /**
     * 释放吧
     */
    public void release() {
        mSaveChangeViewTIme = 0;
        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - mSaveChangeViewTIme) > CHANGE_DELAY_TIME) {
            releaseMusic();
        }
    }

    /**
     * prepared成功之后会开始播放
     */
    public void startAfterPrepared() {

        if (!mHadPrepared) {
            prepareMusic();
        }

        try {
            if (getMusicManager() != null) {
                getMusicManager().start();
            }

            setStateAndUi(CURRENT_STATE_PLAYING);

            if (getMusicManager() != null && mSeekOnStart > 0) {
                getMusicManager().seekTo(mSeekOnStart);
                mSeekOnStart = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        createNetWorkState();

        listenerNetWorkState();

        mHadPlay = true;



        if (mPauseBeforePrepared) {
            onMediaPause();
            mPauseBeforePrepared = false;
        }
    }

    protected boolean isCurrentMediaListener() {
        return getMusicManager().listener() != null
                && getMusicManager().listener() == this;
    }

    /**
     * 创建网络监听
     */
    protected void createNetWorkState() {
        if (mNetInfoModule == null) {
            mNetInfoModule = new NetInfoModule(getActivityContext().getApplicationContext(), new NetInfoModule.NetChangeListener() {
                @Override
                public void changed(String state) {
                    if (!mNetSate.equals(state)) {
                        Debuger.printfError("******* change network state ******* " + state);
                        mNetChanged = true;
                    }
                    mNetSate = state;
                }
            });
            mNetSate = mNetInfoModule.getCurrentConnectionType();
        }
    }

    /**
     * 监听网络状态
     */
    protected void listenerNetWorkState() {
        if (mNetInfoModule != null) {
            mNetInfoModule.onHostResume();
        }
    }

    /**
     * 取消网络监听
     */
    protected void unListenerNetWorkState() {
        if (mNetInfoModule != null) {
            mNetInfoModule.onHostPause();
        }
    }

    /**
     * 释放网络监听
     */
    protected void releaseNetWorkState() {
        if (mNetInfoModule != null) {
            mNetInfoModule.onHostPause();
            mNetInfoModule = null;
        }
    }


    /**
     * 清除当前缓存
     */
    public void clearCurrentCache() {
        if (getMusicManager().isCacheFile() && mCache) {
            //是否为缓存文件
            Debuger.printfError("Play Error " + mUrl);
            mUrl = mOriginUrl;
            getMusicManager().clearCache(mContext, mCachePath, mOriginUrl);
        } else if (mUrl.contains("127.0.0.1")) {
            getMusicManager().clearCache(getContext(), mCachePath, mOriginUrl);
        }

    }





}
