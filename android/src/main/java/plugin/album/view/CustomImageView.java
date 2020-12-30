package plugin.album.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import plugin.album.AlbumPlugin;
import plugin.album.R;
import plugin.album.data.MediaItem;
import plugin.album.utils.FileUtils;
import plugin.album.utils.ImageLoader;
import plugin.album.utils.StringUtils;
import plugin.album.utils.downloader.AsyncDownLoader;
import plugin.album.view.photoview.PhotoView;

public class CustomImageView extends FrameLayout implements ImageLoader.ImageLoadListener {
    private static final int MSG_DOWN_SUCCESS = 101;
    private static final int MSG_DOWN_FAILED = 102;
    private static final int MSG_DOWN_PROGRESS = 103;
    private static final int MSG_HIDE_ORIGINAL_BTN = 104;
    private PhotoView mPhotoView;
    private ImageView mMoreBtn;
    private ProgressView mProgressBar;
    private ViewClickListener mListener;
    private TextView mTvOriginal;
    private MediaItem mMediaItem;
    private boolean mIsDownload = false;
    private View mBottomController;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DOWN_SUCCESS:
                    mMediaItem.updateCacheImage();
                    ImageLoader.loadImage(getContext(), mMediaItem.getThumbPath(), mPhotoView, CustomImageView.this);
                    mTvOriginal.setText("已完成");
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE_ORIGINAL_BTN, 1000);
                    break;
                case MSG_DOWN_FAILED:
                    Toast.makeText(AlbumPlugin.gContext, getContext().getString(R.string.err_hint_look_fail), Toast.LENGTH_LONG).show(); //采用系统默认主题
                    mIsDownload = false;
                    mTvOriginal.setText(getOriginalText());
                    break;
                case MSG_DOWN_PROGRESS:
                    mTvOriginal.setText((String) msg.obj);
                    break;
                case MSG_HIDE_ORIGINAL_BTN:
                    mTvOriginal.setVisibility(GONE);
                    break;
            }
        }
    };

    public CustomImageView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_image, this);
        mProgressBar = findViewById(R.id.progressBar);
        mPhotoView = findViewById(R.id.image_view);
        mMoreBtn = findViewById(R.id.iv_more);
        mTvOriginal = findViewById(R.id.tv_show_original);
        mBottomController = findViewById(R.id.bottom_control_area);

        mMoreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMoreClick();
                }
            }
        });

        mPhotoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickListener();
                }
            }
        });

        mPhotoView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    return mListener.onLongClick();
                }
                return false;
            }
        });

        mTvOriginal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsDownload) return; //下载中不让点击

                final String cachePath = mMediaItem.getCachePath();
                if (!StringUtils.isNullOrEmpty(cachePath)) {
                    mIsDownload = true;
                    AsyncDownLoader.downLoad(mMediaItem.getOriginalPath(), new File(cachePath + ".temp"), new AsyncDownLoader.DownLoaderListener() {
                        @Override
                        public void onSuccess(File file) {
                            file.renameTo(new File(cachePath));
                            Message msg = mHandler.obtainMessage(MSG_DOWN_SUCCESS);
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void onFailed(int code, File file) {
                            if (file != null) {
                                file.deleteOnExit();
                            }
                            Message msg = mHandler.obtainMessage(MSG_DOWN_FAILED);
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void onProgress(long current, long total) {
                            Message msg = mHandler.obtainMessage(MSG_DOWN_PROGRESS);
                            long progress = current * 100 / total;
                            if (progress < 1) progress = 1;
                            msg.obj = progress + "%";
                            mHandler.sendMessage(msg);
                        }
                    });
                }
            }
        });
    }

    public void resetMatrix() {
        mPhotoView.resetMatrix();
    }

    public void setData(MediaItem mediaItem, ViewClickListener listener) {
        if (mediaItem == null) return;

        mMediaItem = mediaItem;
        mProgressBar.setVisibility(VISIBLE);
        ImageLoader.loadImage(getContext(), mMediaItem.getThumbPath(), mPhotoView, this);
        mListener = listener;

        if (mediaItem.isShowOriginal()) {
            mTvOriginal.setVisibility(VISIBLE);
            mTvOriginal.setText(getOriginalText());
        } else {
            mTvOriginal.setVisibility(GONE);
        }
    }

    public void hideBottomController() {
        mBottomController.setVisibility(GONE);
    }

    @Override
    public void onLoadFailed() {
        mProgressBar.setVisibility(GONE);
    }

    @Override
    public void onLoadSuccess() {
        mProgressBar.setVisibility(GONE);
    }

    public String getOriginalText() {
        if (!StringUtils.isNullOrEmpty(mMediaItem.getSizeStr())) {
            return "查看原图 (" + FileUtils.BtoKBMB(mMediaItem.getSizeStr()) + ")";
        }
        return "查看原图";
    }
}
