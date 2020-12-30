package plugin.album.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import plugin.album.Constants;
import plugin.album.data.MediaItem;

public class MediaFileGet extends AsyncTask<Void, Void, List<MediaItem>> {
    private long mBucketId;
    private int mDisplayType;
    private int mMaxCount;
    private  OnGetListener mListener;
    private  ContentResolver mContentResolver;

    /**
     * bucketId 目录ID
     * displayType 显示类型 0：显示所有多媒体文件，1：显示非Gif图片文件，2：显示图片文件与GIF
     * getCount 获取最多的个数
     */
    public void getMediaFile(long bucketId, int displayType, int maxCount,
                             ContentResolver contentResolver, OnGetListener listener) {
        mMaxCount = maxCount;
        mBucketId = bucketId;
        mDisplayType = displayType;
        mListener = listener;
        mContentResolver = contentResolver;

        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected List<MediaItem> doInBackground(Void... params) {
        return getAllMediaThumbnailsPath(mBucketId, mDisplayType, mMaxCount);
    }

    @Override
    protected void onPostExecute(List<MediaItem> result) {
        if (mListener != null) {
            mListener.onGetComplete(result);
        }
    }

    @androidx.annotation.NonNull
    private List<MediaItem> getAllMediaThumbnailsPath(long bucketId, int displayType, int maxCount) {
        String sort = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"; //按时间排序
        Uri external_uri = MediaStore.Files.getContentUri("external"); //SD卡
        String[] selectionArgs = {String.valueOf(bucketId)};
        String[] projections = {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.Images.Media.ORIENTATION,
        };
        String selection;
        if (bucketId == Constants.TYPE_ALL_MEDIA) {
            selection = "bucket_id IS NOT NULL AND (media_type = 3 or (media_type = 1 AND width > 0 AND height > 0))";
        } else if (bucketId == Constants.TYPE_ALL_VIDEO) {
            selection = "bucket_id IS NOT NULL AND media_type = 3";
        } else {
            selection = "bucket_id = ? AND (media_type = 3 or (media_type = 1 AND width > 0 AND height > 0))";
        }

        Cursor cursor;
        if (bucketId == Constants.TYPE_ALL_MEDIA || bucketId == Constants.TYPE_ALL_VIDEO) {
            cursor = mContentResolver.query(external_uri, projections, selection, null, sort);
        } else {
            cursor = mContentResolver.query(external_uri, projections, selection, selectionArgs, sort);
        }
        List<MediaItem> itemList = new ArrayList<>();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        int type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                        long duration = 0;
                        int width = 0;
                        int height = 0;
                        String thumbPath = path;

                        if (displayType != 0) {
                            if (type != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) continue;
                            if (displayType == 1 && Utils.isGif(path)) continue;
                        }

                        if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                            if (Utils.isGif(path)) {
                                type = MediaItem.TYPE_GIF;
                            } else {
                                type = MediaItem.TYPE_IMAGE;
                            }
                            width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH));
                            height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT));
                            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
                            if (orientation == 90 || orientation == 270) {
                                width = height + (height = width) * 0; //交换数值
                            }
                        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                            if (!Utils.isAndroidQ) {  //安卓10无法直接得到缩略图
                                String[] thumbProjections = {
                                        MediaStore.MediaColumns.DATA,
                                        MediaStore.MediaColumns.WIDTH,
                                        MediaStore.MediaColumns.HEIGHT
                                };
                                Cursor thumbCursor = mContentResolver.query(
                                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                        thumbProjections, "video_id=" + id, null, null);
                                if (thumbCursor != null && thumbCursor.moveToFirst()) {
                                    thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                                    width = thumbCursor.getInt(thumbCursor.getColumnIndex(MediaStore.MediaColumns.WIDTH));
                                    height = thumbCursor.getInt(thumbCursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT));
                                }
                            }
                            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
                            type = MediaItem.TYPE_VIDEO;
                        }
                        itemList.add(new MediaItem(type, id, path, thumbPath, duration, size, width, height));

                        maxCount --;
                        if (maxCount <= 0) break;
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {
                if (!cursor.isClosed()) cursor.close();
            }
        }
        return itemList;
    }

    public interface OnGetListener {
        void onGetComplete(List<MediaItem> dataList);
    }
}
