package plugin.album.view.alivideo;

import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;

import java.lang.ref.WeakReference;

public class AliPlayerListener implements IPlayer.OnPreparedListener,
        IPlayer.OnErrorListener,
        IPlayer.OnLoadingStatusListener,
        IPlayer.OnStateChangedListener,
        IPlayer.OnCompletionListener,
        IPlayer.OnInfoListener,
        IPlayer.OnRenderingStartListener,
        IPlayer.OnSeekCompleteListener{
    private WeakReference<AliVideoView> weakReference;

    public AliPlayerListener(AliVideoView aliVideoView) {
        weakReference = new WeakReference<>(aliVideoView);
    }

    @Override
    public void onPrepared() {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoPrepared();
        }
    }

    @Override
    public void onCompletion() {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoCompletion();
        }
    }

    @Override
    public void onError(ErrorInfo errorInfo) {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoError(errorInfo);
        }
    }

    @Override
    public void onInfo(InfoBean infoBean) {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoInfo(infoBean);
        }
    }

    @Override
    public void onLoadingBegin() {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoLoadingBegin();
        }
    }

    @Override
    public void onLoadingProgress(int i, float v) {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoLoadingProgress(i, v);
        }
    }

    @Override
    public void onLoadingEnd() {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoLoadingEnd();
        }
    }

    @Override
    public void onRenderingStart() {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoRenderingStart();
        }
    }

    @Override
    public void onSeekComplete() {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoSeekComplete();
        }
    }

    @Override
    public void onStateChanged(int i) {
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            aliVideoView.videoStateChanged(i);
        }
    }
}
