package plugin.album.view.alivideo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aliyun.player.nativeclass.MediaInfo;

import java.lang.ref.WeakReference;

import plugin.album.R;
import plugin.album.view.ViewClickListener;

public class AliControlView extends RelativeLayout implements View.OnClickListener{
    private LinearLayout mBottomContainer;
    private ImageView mStateIcon;
    private TextView mCurrTime;
    private TextView mTotalTime;
    private SeekBar mSeekBar;
    private ImageView mCloseBtn;
    private ImageView mMoreBtn;
    private ImageView mPauseIv;
    private WeakReference<AliVideoView> weakReference;
    private MediaInfo mMediaInfo;
    private String mTimeFormat = "%02d:%02d";
    private ViewClickListener mListener;
    private final int DELAY_HIDDEN_CONTROL = 101;
    private int mVideoBufferPosition = 0;
    private int mVideoPosition = 0;
    private boolean mSimpleModel = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DELAY_HIDDEN_CONTROL:
                    showBottomControl(false);
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        setCurrTime(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mHandler.removeMessages(DELAY_HIDDEN_CONTROL); //防止拉进度条的时候被隐藏
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    showBottomControl(true); //在规定时间隐藏
                    AliVideoView aliVideoView = weakReference.get();
                    if (aliVideoView != null) {
                        aliVideoView.onSeekEnd(seekBar.getProgress());
                    }
                }
            };

    public AliControlView(Context context, AliVideoView aliVideoView) {
        super(context);
        weakReference = new WeakReference<>(aliVideoView);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_alicontrol_view, this);

        mBottomContainer = findViewById(R.id.cover_player_controller_bottom_container);
        mStateIcon = findViewById(R.id.alivideo_controller_play_state);
        mPauseIv = findViewById(R.id.iv_pause);
        mCurrTime = findViewById(R.id.cover_player_controller_text_view_curr_time);
        mTotalTime = findViewById(R.id.cover_player_controller_text_view_total_time);
        mSeekBar = findViewById(R.id.cover_player_controller_seek_bar);
        mMoreBtn = findViewById(R.id.cover_player_controller_more);
        mCloseBtn = findViewById(R.id.cover_player_controller_close);

        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mStateIcon.setOnClickListener(this);
        mPauseIv.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);

        showBottomControl(true);
    }

    public void setListener(ViewClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.alivideo_controller_play_state || id == R.id.iv_pause) {
            onClickPlayBtn();
        } else if (id == R.id.cover_player_controller_close) {
            if (mListener != null)
                mListener.onCloseClick();
        } else if (id == R.id.cover_player_controller_more) {
            if (mListener != null)
                mListener.onMoreClick();
        }

        showBottomControl(true); //在规定时间隐藏
    }

    public void onClickPlayBtn() {
        boolean isStop = mStateIcon.isSelected();
        AliVideoView aliVideoView = weakReference.get();
        if (aliVideoView != null) {
            if (isStop) {
                aliVideoView.start();
            } else {
                aliVideoView.pause();
            }
        }
        setPlayState(!isStop);
        if (mSimpleModel && mListener != null)  mListener.onClickPlay(!isStop);
    }

    public void setPlayState(boolean isStop) {
        mStateIcon.setSelected(isStop);
        mPauseIv.setVisibility(isStop ? VISIBLE : GONE);
    }

    public void setSimpleModel(boolean simpleModel) {
        mSimpleModel = simpleModel;
        showBottomControl(!simpleModel);
    }

    public void toggleController() {
        if (mBottomContainer.getVisibility() == View.VISIBLE) {
            showBottomControl(false);
        } else {
            showBottomControl(true);
        }
    }

    public void showBottomControl(boolean show) {
        if (show && !mSimpleModel) {
            mHandler.removeMessages(DELAY_HIDDEN_CONTROL);
            mHandler.sendEmptyMessageDelayed(DELAY_HIDDEN_CONTROL, 4000);
            mBottomContainer.setVisibility(View.VISIBLE);
        } else {
            mHandler.removeMessages(DELAY_HIDDEN_CONTROL);
            mBottomContainer.setVisibility(View.GONE);
        }
    }

    public void setMediaInfo(MediaInfo aliMediaInfo) {
        mMediaInfo = aliMediaInfo;
        if (mMediaInfo == null) return;
        int totalDuration = mMediaInfo.getDuration();

        setTotalTime(totalDuration);
        mSeekBar.setMax(totalDuration);
    }

    public void setVideoPosition(int position) {
        mVideoPosition = position;

        mSeekBar.setProgress(mVideoPosition);
        setCurrTime(mVideoPosition);
    }

    public void setVideoBufferPosition(int videoBufferPosition) {
        mVideoBufferPosition = videoBufferPosition;
        mSeekBar.setSecondaryProgress(mVideoBufferPosition);
    }

    private void setTotalTime(int duration) {
        mTotalTime.setText(getTime(mTimeFormat, duration));
    }
    private void setCurrTime(int curr) {
        mCurrTime.setText(getTime(mTimeFormat, curr));
    }

    public static String getTime(String format, long time){
        if(time <= 0) time = 0;
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;
        return String.format(format, minutes, seconds);
    }
}
