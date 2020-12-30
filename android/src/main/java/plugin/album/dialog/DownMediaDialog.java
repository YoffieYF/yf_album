package plugin.album.dialog;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;

import plugin.album.AlbumPlugin;
import plugin.album.CacheMgr;
import plugin.album.R;
import plugin.album.data.MediaItem;
import plugin.album.utils.FileStorage;
import plugin.album.utils.StringUtils;
import plugin.album.utils.downloader.AsyncDownLoader;
import plugin.album.utils.downloader.DownLoadTask;
import plugin.album.view.RingProgressView;

public class DownMediaDialog extends Dialog {
    private static final int MSG_DOWN_SUCCESS = 101;
    private static final int MSG_DOWN_FAILED = 102;
    private static final int MSG_DOWN_PROGRESS = 103;

    public static void showDialog(Context context, MediaItem item) {
        new DownMediaDialog(context, item).show();
    }

    private MediaItem mItem;
    private RingProgressView mProgressView;
    private DownLoadTask mDownLoadTask;
    private MediaScannerConnection mMediaScannerConnection;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DOWN_SUCCESS:
                    notification((String) msg.obj);
                    mDownLoadTask = null;
                    dismiss();
                    break;
                case MSG_DOWN_FAILED:
                    String hintStr = (String) msg.obj;
                    if (StringUtils.isNotNullOrEmpty(hintStr)) {
                        Toast.makeText(AlbumPlugin.gContext, hintStr, Toast.LENGTH_LONG).show(); //采用系统默认主题
                    }
                    mDownLoadTask = null;
                    dismiss();
                    break;
                case MSG_DOWN_PROGRESS:
                    if (mProgressView != null) {
                        mProgressView.setCurrentProgress((Integer) msg.obj);
                    }
                    break;
            }
        }
    };

    public DownMediaDialog(@NonNull Context context, MediaItem item) {
        super(context, R.style.DownLoader_Dialog);
        mItem = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.down_loader_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mProgressView = findViewById(R.id.progress_view);

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        startDown();
    }

    public void startDown() {
        String url = mItem.getPath();
        if (StringUtils.isNullOrEmpty(url)) {
            dismiss();
            return;
        }

        int pos = url.indexOf("?");
        if (pos > 0) {
            url = url.substring(0, pos);
        }
        String suffix = "";
        pos = url.lastIndexOf(".");
        if (pos > 0) {
            suffix = url.substring(pos);
        }
        String fileName = "meet"+System.currentTimeMillis()+suffix;
        final String outPath = FileStorage.getMediaDownloadPath(fileName, true);
        url = getUrlCachePath(mItem, url);

        mDownLoadTask = AsyncDownLoader.downLoad(url, new File(outPath+".temp"), new AsyncDownLoader.DownLoaderListener() {
            @Override
            public void onSuccess(File file) {
                file.renameTo(new File(outPath));
                Message msg = mHandler.obtainMessage(MSG_DOWN_SUCCESS);
                msg.obj = outPath;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailed(int code, File file) {
                if (file != null) {
                    file.delete();
                }
                Message msg = mHandler.obtainMessage(MSG_DOWN_FAILED);
                if (code != DownLoadTask.USER_CANCEL_CODE) {
                    msg.obj = "下载失败，请稍后重试";
                } else {
                    msg.obj = "";
                }
                mHandler.sendMessage(msg);
            }

            @Override
            public void onProgress(long current, long total) {
                Message msg = mHandler.obtainMessage(MSG_DOWN_PROGRESS);
                int progress = (int) (current * 100/ total);
                if (progress < 1) progress = 1;
                msg.obj = progress;
                mHandler.sendMessage(msg);
            }
        });
    }

    public String getUrlCachePath(MediaItem item, String url) {
        if (StringUtils.isNullOrEmpty(url))
            return url;

        if (item.getType() == MediaItem.TYPE_VIDEO) {
            String cachePath = CacheMgr.getInstance().getVideoCachePath(item.getPath());
            if (!StringUtils.isNullOrEmpty(cachePath)) {
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(cachePath);
                    int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    if (height > 0) {
                        url = cachePath;
                    }
                } catch (Exception e) {
                }
            }
        }
        return url;
    }

    public void notification(final String savePath) {
        if (StringUtils.isNullOrEmpty(savePath)) {
            Toast.makeText(AlbumPlugin.gContext, "下载失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AlbumPlugin.gContext, "保存在 " + savePath, Toast.LENGTH_SHORT).show();

            //第一种通知更新方法
            ContentResolver localContentResolver = AlbumPlugin.gContext.getContentResolver();
            ContentValues localContentValues = getContentValues(savePath);
            if (mItem.getType() == MediaItem.TYPE_VIDEO) {
                localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
            } else {
                localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
            }

            //第二种通知更新
            mMediaScannerConnection = new MediaScannerConnection(AlbumPlugin.gContext, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    MimeTypeMap mtm = MimeTypeMap.getSingleton();
                    String extension = mtm.getMimeTypeFromExtension(mtm.getFileExtensionFromUrl(savePath));
                    mMediaScannerConnection.scanFile(savePath, extension);
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(uri);
                    AlbumPlugin.gContext.sendBroadcast(mediaScanIntent); //第三种保障
                    mMediaScannerConnection.disconnect();
                }
            });
            mMediaScannerConnection.connect();
        }
    }

    public ContentValues getContentValues(String filePath) {
        File paramFile = new File(filePath);
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        long paramLong=  Long.valueOf(System.currentTimeMillis());
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", mtm.getMimeTypeFromExtension(mtm.getFileExtensionFromUrl(filePath)));
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void dismiss() {
        if (mDownLoadTask != null) {
            mDownLoadTask.cancel(true);
        } else {
            super.dismiss();
        }
    }
}
