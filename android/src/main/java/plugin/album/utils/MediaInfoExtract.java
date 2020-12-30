package plugin.album.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugin.album.data.MediaItem;


public class MediaInfoExtract extends AsyncTask<Void, Void, List<Map<String, Object>>> {
    private final boolean isAndroidQ = Build.VERSION.SDK_INT >= 29;
    private OnGetInfoListener mListener;
    private ContentResolver mContentResolver;
    private boolean mIsOriginal;
    private ArrayList<MediaItem> mDataList = new ArrayList<>();

    public void getMediaInfoList(List<MediaItem> dataList, boolean isOriginal,
                                 ContentResolver contentResolver, OnGetInfoListener listener) {
        if (dataList == null || dataList.isEmpty()) {
            if (listener != null)
                listener.onGetInfoComplete(null);
            return;
        }
        mIsOriginal = isOriginal;
        mListener = listener;
        mContentResolver = contentResolver;
        mDataList.addAll(dataList);
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected List<Map<String, Object>> doInBackground(Void... voids) {
        FileStorage.clearTempDir();
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (MediaItem item : mDataList) {
            String path = item.getPath();
            if (!new File(path).exists()) continue;
            String thumbPath = item.getThumbPath();
            int width = item.getWidth();
            int height = item.getHeight();
            if (item.getType() == MediaItem.TYPE_VIDEO) {
                if (path.equals(thumbPath)) { //缩略图与视频路径一样，肯定是因为没查询到
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    if (bitmap != null) {
                        thumbPath = FileUtils.storeVideoThumbBitmap(bitmap, item.getId());
                        width = bitmap.getWidth();
                        height = bitmap.getHeight();
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
            }

            Map<String, Object> entity = new HashMap<>();
            entity.put("fileName", item.getPath());
            entity.put("type", item.getEntityType());
            entity.put("isOriginal", mIsOriginal ? 1 : 0);
            entity.put("path", path);
            entity.put("videoDuration", item.getDuration() / 1000);
            entity.put("coverPath", thumbPath);
            entity.put("w", width);
            entity.put("h", height);
            list.add(entity);
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<Map<String, Object>> maps) {
        if (mListener != null) {
            mListener.onGetInfoComplete(maps);
        }
    }

    public interface OnGetInfoListener {
        void onGetInfoComplete(List<Map<String, Object>> dataList);
    }
}
