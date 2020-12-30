package plugin.album;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import plugin.album.data.AlbumItem;
import plugin.album.data.MediaItem;
import plugin.album.utils.FileUtils;
import plugin.album.utils.MediaCompression;
import plugin.album.utils.MediaFileGet;
import plugin.album.utils.MediaInfoExtract;
import plugin.album.utils.Utils;


public class PickerMgr {
    private static final String TAG = "PickerMgr";
    private AlbumPlugin mAlbumPlugin;

    private static final PickerMgr sInstance = new PickerMgr();
    private ContentResolver mContentResolver;
    private ArrayList<MediaItem> mSelectedImages = new ArrayList<>();
    private ArrayList<AlbumItem> mAlbumItemList = new ArrayList<>();
    private List<Map<String, Object>> mSendDataList = new ArrayList<>();
    private boolean mOriginalMode = false;

    private PickerMgr() {
    }

    public void initAlbumPlugin(AlbumPlugin albumPlugin) {
        mAlbumPlugin = albumPlugin;
        mContentResolver = mAlbumPlugin.gContext.getContentResolver();
    }

    public static PickerMgr getInstance() {
        return sInstance;
    }

    public void clear() {
        mOriginalMode = false;
        mSelectedImages.clear();
        mAlbumItemList.clear();
        mSendDataList.clear();
    }

    public boolean getOriginalMode() {
        return mOriginalMode;
    }

    public void setOriginalMode(boolean isOriginal) {
        mOriginalMode = isOriginal;
    }

    public ArrayList<MediaItem> getSelectedImages() {
        return mSelectedImages;
    }

    public void changeSelectItemState(MediaItem item) {
        boolean isContained = mSelectedImages.contains(item);
        if (isContained) {
            mSelectedImages.remove(item);
        } else {
            mSelectedImages.add(item);
        }
    }

    public List<Map<String, Object>> getSelectedPath() {
        return mSendDataList;
    }

    public void setSendDataList(List<Map<String, Object>> dataList) {
        mSendDataList.clear();
        if (dataList != null) {
            mSendDataList.addAll(dataList);
        }
    }

    public void mediaItemCompress(MediaItem item, MediaCompression.OnCompressionListener listener) {
        new MediaCompression().onCompress(item, mContentResolver, listener);
    }

    public void mediaItemCompress(String filePath, MediaCompression.OnCompressionListener listener) {
        Size wh = FileUtils.getImageWidthHeight(filePath);
        if (wh == null || wh.getWidth() == 0 || wh.getHeight() == 0) {
            if (listener != null)
                listener.onCompressionComplete(null);
            return;
        }
        MediaItem item = new MediaItem(MediaItem.TYPE_IMAGE, "0", filePath, filePath,
                0, 0, wh.getWidth(), wh.getHeight());
        mediaItemCompress(item, listener);
    }

    public boolean onMediaFileCompress(Object arguments) {
        if (arguments instanceof List) {
            List<Map<String, Object>> tempList = (List<Map<String, Object>>) arguments;
            if (tempList != null && !tempList.isEmpty()) {
                List<MediaItem> itemList = new ArrayList<>();
                for (Map<String, Object> item : tempList) {
                    itemList.add(new MediaItem(item));
                }

                new MediaCompression().onCompress(itemList, mContentResolver,
                        new MediaCompression.OnCompressionListener() {
                            @Override
                            public void onCompressionComplete(Map<String, Object> item) {
                                if (mAlbumPlugin != null) {
                                    mAlbumPlugin.onSendMediaFile(item);
                                }
                            }
                        });
                return true;
            }
        }
        return false;
    }

    public void getMediaInfoList(MediaInfoExtract.OnGetInfoListener listener) {
        new MediaInfoExtract().getMediaInfoList(mSelectedImages, mOriginalMode,
                mContentResolver, listener);
    }

    public ArrayList<AlbumItem> getAlbumList() {
        return mAlbumItemList;
    }

    public void getAlbumList(int type) {
        new LoadAlbumList(type).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getImageList(long bucketId, int type, MediaFileGet.OnGetListener listener) {
        new MediaFileGet().getMediaFile(bucketId, type, 99999, mContentResolver, listener);
    }

    public void getLatestMediaFile(int maxCount, final MediaInfoExtract.OnGetInfoListener listener) {
        if (maxCount <= 0) maxCount = 100;
        new MediaFileGet().getMediaFile(0, 0, maxCount,
                mContentResolver, new MediaFileGet.OnGetListener() {
                    @Override
                    public void onGetComplete(List<MediaItem> dataList) {
                        if (dataList == null || dataList.isEmpty()) {
                            listener.onGetInfoComplete(null);
                        } else {
                            new MediaInfoExtract().getMediaInfoList(dataList, false,
                                    mContentResolver, listener);
                        }
                    }
                });
    }

    private class LoadAlbumList extends AsyncTask<Void, Void, ArrayList<AlbumItem>> {
        private int mType;

        LoadAlbumList(int type) {
            this.mType = type;
        }

        @Override
        protected ArrayList<AlbumItem> doInBackground(Void... params) {
            HashMap<Long, AlbumItem> albumHashMap = new HashMap<>();
            final String orderBy = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
            String[] projection = new String[]{
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Files.FileColumns.MEDIA_TYPE};
            String selection = "bucket_id IS NOT NULL AND (media_type = 3 or (media_type = 1 AND width > 0 AND height > 0))";

            Cursor cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"),
                    projection, selection, null, orderBy);

            int totalCount = 0;
            int totalVideoCount = 0;
            String allItemThumbnailPath = null;
            String allVideoThumbnailPath = null;
            if (cursor != null) {
                int bucketData = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                int albumNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int bucketIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                int typeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

                while (cursor.moveToNext()) {
                    int type = cursor.getInt(typeIndex);
                    String path = cursor.getString(bucketData);
                    if (mType != 0) {
                        if (type != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) continue;
                        if (mType == 1 && Utils.isGif(path)) continue;
                    }

                    totalCount++;
                    long bucketId = cursor.getInt(bucketIndex);
                    AlbumItem albumItem = albumHashMap.get(bucketId);
                    if (albumItem == null) {
                        String albumName = cursor.getString(albumNameIndex);
                        albumHashMap.put(bucketId, new AlbumItem(bucketId, albumName, path, 1));
                        if (allItemThumbnailPath == null) {
                            allItemThumbnailPath = path;
                        }
                    } else {
                        albumItem.counter++;
                    }

                    if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                        totalVideoCount++;
                        if (allVideoThumbnailPath == null) {
                            allVideoThumbnailPath = cursor.getString(bucketData);
                        }
                    }
                }
                cursor.close();
            }

            ArrayList<AlbumItem> albumItemList = new ArrayList<>();
            for (AlbumItem albumItem : albumHashMap.values()) {
                albumItemList.add(albumItem);
            }
            Collections.sort(albumItemList, new Comparator<AlbumItem>() {
                @Override
                public int compare(AlbumItem o1, AlbumItem o2) {
                    return o2.counter - o1.counter; //降序
                }
            });
            if (allItemThumbnailPath != null && totalCount > 0) {
                albumItemList.add(0, new AlbumItem(Constants.TYPE_ALL_MEDIA, mType != 0 ? "所有图片" : "图片和视频", allItemThumbnailPath, totalCount));

                if (allVideoThumbnailPath != null && totalVideoCount > 0) {
                    albumItemList.add(1, new AlbumItem(Constants.TYPE_ALL_VIDEO, "全部视频", allVideoThumbnailPath, totalVideoCount));
                }
            }
            return albumItemList;
        }

        @Override
        protected void onPostExecute(ArrayList<AlbumItem> albumItemList) {
            super.onPostExecute(albumItemList);
            mAlbumItemList = albumItemList;
        }
    }
}
