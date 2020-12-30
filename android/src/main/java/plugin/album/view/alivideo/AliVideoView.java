package plugin.album.view.alivideo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import plugin.album.CacheMgr;
import plugin.album.utils.FileStorage;
import plugin.album.view.ViewClickListener;

public class AliVideoView extends FrameLayout {
    private SurfaceView mSurfaceView;
    private AliPlayer mAliPlayer;
    private Context mContext;
    private ViewClickListener mListener;
    private AliControlView mAliControlView;
    private AliDefaultView mAliDefaultView;
    private AliLoadingView mLoadingView;
    private AliPlayerListener mAliPlayerListener;
    private boolean isCompleted = false;
    private int mPlayerState = IPlayer.idle;
    private boolean mSimpleModel = false;

    public AliVideoView(@NonNull Context context) {
        this(context, null, 0);
    }

    public AliVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AliVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void setUrl(String url, boolean autoPlay, ViewClickListener listener) {
        if (mAliPlayer == null) return;
        mListener = listener;
        mAliControlView.setListener(mListener);
        mAliControlView.setPlayState(!autoPlay);
        mAliControlView.showBottomControl(true);
        mAliDefaultView.setImagePath(url);

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.setAutoPlay(autoPlay);
        mAliPlayer.prepare();
        if (autoPlay) {
            mLoadingView.show(true);
        }
        CacheMgr.getInstance().setVideoCachePath(url, mAliPlayer.getCacheFilePath(url)); //存起来,下载时可以用
    }

    public void setSimpleModel(boolean simpleModel) {
        mSimpleModel = simpleModel;
        mAliControlView.setSimpleModel(simpleModel);
    }

    public void onDestroy() {
        stop();
        if (mAliPlayer != null) {
            mAliPlayer.release();
            mAliPlayer = null;
        }

        mSurfaceView = null;
        mAliControlView = null;
        mAliDefaultView = null;
        mLoadingView = null;
    }

    public void start() {
        if (mAliPlayer != null) {
            if (isCompleted) { //播完后从头开始播
                mAliPlayer.prepare();
            }
            mAliPlayer.start();
            if (mPlayerState < IPlayer.prepared){
                mLoadingView.show(true);
            }
        }
    }

    public void stop() {
        if (mAliPlayer != null) {
            mAliPlayer.stop();
        }
    }

    private void realySeekTo(int position) {
        if (mAliPlayer == null) return;
        mAliPlayer.seekTo(position, IPlayer.SeekMode.Accurate);

        mAliPlayer.start();
        mAliControlView.setPlayState(false);
    }

    public void resume() {
        mAliControlView.showBottomControl(true);
//        start();
    }

    public void pause() {
        if (mAliPlayer == null) return;

        mAliPlayer.pause();
        mAliControlView.setPlayState(true); //未准备完成，不会产生暂停状态，需在这手动设置
        mLoadingView.show(false);
    }

    public void onSeekEnd(int progress) {
        if (mAliControlView != null) {
            mAliControlView.setVideoPosition(progress);
        }
        realySeekTo(progress);
    }

    public void videoPrepared() {
        if (mAliPlayer == null) return;
        isCompleted = false;

        MediaInfo aliMediaInfo = mAliPlayer.getMediaInfo();
        if (aliMediaInfo == null) return;

        long duration = mAliPlayer.getDuration();
        aliMediaInfo.setDuration((int) duration); //防止服务器信息和实际不一致
        mAliControlView.setMediaInfo(aliMediaInfo);

        mSurfaceView.setVisibility(View.VISIBLE);
    }

    public void videoCompletion() {
        isCompleted = true;
        mAliControlView.setPlayState(true);
    }

    public void videoError(ErrorInfo errorInfo) {
    }

    public void videoInfo(InfoBean infoBean) {
        if (infoBean == null) return;

        InfoCode infoCode = infoBean.getCode();
        if (infoCode == InfoCode.AutoPlayStart) {
            videoPrepared();
        } else if (infoCode == InfoCode.BufferedPosition) {
            long bufPos = infoBean.getExtraValue();
            mAliControlView.setVideoBufferPosition((int) bufPos);
        } else if (infoCode == InfoCode.CurrentPosition) {
            long curPos = infoBean.getExtraValue();
            if (mPlayerState == IPlayer.started) {
                mAliControlView.setVideoPosition((int) curPos);
            }
        } else if (infoCode == InfoCode.CacheSuccess) {
        }
    }

    public void videoLoadingBegin() {
        mLoadingView.show(true);
    }

    public void videoLoadingProgress(int percent, float value) {
        //percent 加载进度
        if (mPlayerState != IPlayer.paused && !mLoadingView.isShow()) {
            mLoadingView.show(true);
        }
    }

    public void videoLoadingEnd() {
        mLoadingView.show(false);
    }

    public void videoRenderingStart() {
        mAliDefaultView.setVisibility(GONE);
        mLoadingView.show(false);
    }

    public void videoSeekComplete() {
    }

    public void videoStateChanged(int newState) {
        mPlayerState = newState;
        if (newState == IPlayer.started) {
            mAliControlView.setPlayState(false);
        } else if (newState == IPlayer.paused) {
            mAliControlView.setPlayState(true);
        }
    }

    private void initView() {
        initAliPlayer();
        initSurfaceView();
        initDefaultView();
        initControlView();
        initLoadingView();
    }

    private void initLoadingView() {
        mLoadingView = new AliLoadingView(mContext);
        addSubView(mLoadingView);
        mLoadingView.show(false);
    }

    private void initDefaultView() {
        mAliDefaultView = new AliDefaultView(mContext);
        addSubView(mAliDefaultView);
    }

    private void initControlView() {
        mAliControlView = new AliControlView(mContext, this);
        addSubView(mAliControlView);
    }

    private void initAliPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(mContext.getApplicationContext());
//        mAliPlayer.enableLog(false);
        mAliPlayerListener = new AliPlayerListener(this);
        mAliPlayer.setOnPreparedListener(mAliPlayerListener); //准备完成回调
        mAliPlayer.setOnErrorListener(mAliPlayerListener); //播放器出错监听
        mAliPlayer.setOnLoadingStatusListener(mAliPlayerListener); //播放器加载回调
        mAliPlayer.setOnStateChangedListener(mAliPlayerListener);//播放器状态
        mAliPlayer.setOnCompletionListener(mAliPlayerListener); //播放结束
        mAliPlayer.setOnInfoListener(mAliPlayerListener); //播放信息监听
        mAliPlayer.setOnRenderingStartListener(mAliPlayerListener);//第一帧显示
        mAliPlayer.setOnSeekCompleteListener(mAliPlayerListener); //seek结束事件

        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.mEnable = true;
        cacheConfig.mMaxDurationS = 3600;   //全部缓存
        cacheConfig.mMaxSizeMB = 500;          //最大缓存500M
        cacheConfig.mDir = FileStorage.getVideoCacheDir();
        mAliPlayer.setCacheConfig(cacheConfig);

        PlayerConfig playerConfig = mAliPlayer.getConfig();
        playerConfig.mMaxBufferDuration = 300000;
        playerConfig.mHighBufferDuration = 200000;
        playerConfig.mNetworkRetryCount = 5;
        mAliPlayer.setConfig(playerConfig);

    }

    private void initSurfaceView() {
        mSurfaceView = new SurfaceView(getContext().getApplicationContext());
        addSubView(mSurfaceView);

        mSurfaceView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null)
                    mListener.onLongClick();
                return true;
            }
        });
        mSurfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSimpleModel) {
                    if (mListener != null) {
                        mListener.onClickListener();
                    }
                    if (mAliControlView != null) {
                        mAliControlView.toggleController();
                    }
                } else {
                    if (mAliControlView != null) {
                        mAliControlView.onClickPlayBtn();
                    }
                }
            }
        });

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mAliPlayer != null) {
                    mAliPlayer.setDisplay(holder);
                    mAliPlayer.redraw();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mAliPlayer != null) {
                    mAliPlayer.redraw();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mAliPlayer != null) {
                    mAliPlayer.setDisplay(null);
                }
            }
        });
    }

    private void addSubView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        //添加到布局中
        addView(view, params);
    }
}
