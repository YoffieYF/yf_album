package plugin.album.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Size;

import com.hw.videoprocessor.VideoProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugin.album.AlbumPlugin;
import plugin.album.CacheMgr;
import plugin.album.data.MediaItem;
import top.zibin.luban.Luban;
import top.zibin.luban.OnRenameListener;

public class MediaCompression extends AsyncTask<Void, Void, Void> {
    private final boolean isAndroidQ = Build.VERSION.SDK_INT >= 29;
    private final int MEDIA_COMPRESSION_COMPLETE = 101;
    private ContentResolver mContentResolver;
    private OnCompressionListener mListener;
    private ArrayList<MediaItem> mDataList = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_COMPRESSION_COMPLETE:
                    if (msg.obj != null) {
                        mListener.onCompressionComplete((Map<String, Object>) msg.obj);
                    }
                    break;
            }
        }
    };

    public void onCompress(List<MediaItem> dataList, ContentResolver contentResolver, OnCompressionListener listener) {
        if (dataList == null || dataList.isEmpty()) {
            if (listener != null)
                listener.onCompressionComplete(null);
            return;
        }
        mListener = listener;
        mContentResolver = contentResolver;
        mDataList.addAll(dataList);
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onCompress(MediaItem item, ContentResolver contentResolver, OnCompressionListener listener) {
        mListener = listener;
        mContentResolver = contentResolver;
        mDataList.add(item);
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Void doInBackground(Void... voids) {
            String saveDir = FileStorage.tempDir(false);
            try {
                for (MediaItem item : mDataList) {
                    String path = item.getPath();
                    if (!new File(path).exists()) continue;
                    int type = item.getType();

                    String thumbPath = item.getThumbPath();
                    int width = item.getWidth();
                    int height = item.getHeight();
                    final String fileName = CacheMgr.getInstance().getSafeFileName(path);

                    if (type == MediaItem.TYPE_IMAGE) {
                        List<File> fileList = Luban.with(AlbumPlugin.gContext)
                                .load(item.getPath())
                                .ignoreBy(300)
                                .setTargetDir(saveDir)
                                .setRenameListener(new OnRenameListener() {
                                    @Override
                                    public String rename(String filePath) {
                                        return fileName;
                                    }
                                }).get();
                        if (fileList != null && !fileList.isEmpty()) {
                            String newPath = fileList.get(0).getPath();
                            Size wh = FileUtils.getImageWidthHeight(newPath);
                            if (wh != null && wh.getWidth() != 0 && wh.getHeight() != 0) {
                                width = wh.getWidth();
                                height = wh.getHeight();
                                path = newPath;
                                thumbPath = newPath;
                            }
                        }
                    } else if (type == MediaItem.TYPE_VIDEO) {
                        MediaItem newItem = videoCompress(item, saveDir, fileName);
                        if (newItem != item || path.equals(thumbPath)) { //缩略图与视频路径一样，肯定是因为没查询到
                            path = newItem.getPath();
                            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(newItem.getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                            if (bitmap != null) {
                                thumbPath = FileUtils.storeVideoThumbBitmap(bitmap, item.getId());
                                width = bitmap.getWidth();
                                height = bitmap.getHeight();
                            } else {
                                width = newItem.getWidth();
                                height = newItem.getHeight();
                            }
                        } else if (isAndroidQ) {
                            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver,
                                    Long.valueOf(item.getId()), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND, new BitmapFactory.Options());
                            if (bitmap == null) {
                                bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                            }
                            if (bitmap != null) {
                                thumbPath = FileUtils.storeVideoThumbBitmap(bitmap, item.getId());
                                width = bitmap.getWidth();
                                height = bitmap.getHeight();
                            }
                        }
                    } else if (type == MediaItem.TYPE_GIF) {
                        //无需补充
                    }

                    Map<String, Object> entity = new HashMap<>();
                    entity.put("fileName", item.getPath());
                    entity.put("type", item.getEntityType());
                    entity.put("isOriginal",  0);
                    entity.put("videoDuration", item.getDuration() / 1000);
                    entity.put("path", path);
                    entity.put("coverPath", thumbPath);
                    entity.put("w", width);
                    entity.put("h", height);
                    entity.put("isCompressSuccess", true);
                    onPostCompressionContent(entity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
    }


    private void onPostCompressionContent(Map<String, Object> entity) {
        Message message = mHandler.obtainMessage(MEDIA_COMPRESSION_COMPLETE, entity);
        mHandler.sendMessage(message);
    }

    public MediaItem videoCompress(MediaItem item, String tempDir, String fileName) {
        if (item == null || item.getType() != MediaItem.TYPE_VIDEO) return item;

        File file = new File(item.getPath());
        if (file == null || file.length() < 5 * 1024 * 1024) return item; //少于5M的直接发送

        int originWidth;
        int originHeight;
        String cacheVideoDir = FileStorage.getCacheSubDir(".cacheVideo");
        FileUtils.ensureFileDirExists(cacheVideoDir);
        String cachePath = cacheVideoDir + fileName;
        if (new File(cachePath).exists()) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(cachePath);
                originWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); //宽度
                originHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); //高度
                if (originWidth != 0 && originWidth != 0) {
                    return new MediaItem(item.getType(), item.getId(), cachePath, cachePath, item.getDuration(), item.getSize(), originWidth, originHeight);
                }
            } catch (Exception e) {
            }
        } else {
            try {
                String tempFilePath = tempDir + "temp-" + fileName;
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(item.getPath());
                originWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); //宽度
                originHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); //高度
                int bitrate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)); //码率（比特率）
                int short_edge = Math.min(originWidth, originHeight);
                float ratio = short_edge / 544.0f; //短边设为544P
                if (ratio < 1 && bitrate < 1185000) return item; //少于544P不进行压缩
                int outBitrate = 1085000;
                Size outSize = getNewWidthHeight(originWidth, originHeight, ratio);
                VideoProcessor.processor(AlbumPlugin.gContext)
                        .input(item.getPath())
                        .output(tempFilePath)
                        .frameRate(25)
                        .outWidth(outSize.getWidth())
                        .outHeight(outSize.getHeight())
                        .bitrate(outBitrate)
                        .process();
                String filePath = tempDir + fileName;
                File tempFile = new File(tempFilePath);
                QtFastStart.fastStart(tempFile, new File(filePath));
                FileUtils.saveNetFile(filePath, cachePath);
                tempFile.delete();
                return new MediaItem(item.getType(), item.getId(), filePath, filePath, item.getDuration(), item.getSize(), outSize.getWidth(), outSize.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    private Size getNewWidthHeight(int originWidth, int originHeight, float ratio) {
        int outWidth = originWidth;
        int outHeight = originHeight;

        int max = Math.max(originWidth, originHeight);
        int min = Math.min(originWidth, originHeight);
        if (max * 9 == min * 16) {
            if (originWidth < originHeight) {
                outWidth = 544;
                outHeight = 960;
            } else {
                outWidth = 960;
                outHeight = 544;
            }
        } else if (ratio > 1) {
            outWidth = (int) (originWidth / ratio);
            outHeight = (int) (originHeight / ratio);
        }

        return new Size(outWidth, outHeight);
    }

    public interface OnCompressionListener {
        void onCompressionComplete(Map<String, Object> item);
    }
}

