package plugin.album.data;

import androidx.annotation.Nullable;

import plugin.album.CacheMgr;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

public class MediaItem implements Serializable {
    public static final int TYPE_IMAGE = 1; //
    public static final int TYPE_GIF = 2;
    public static final int TYPE_VIDEO = 3; //
    private int type;
    private String mId;
    private String mPath = "";
    private String mThumbPath;
    private long mDuration;
    private int mWidth;
    private int mHeight;
    private long mSize;

    //网络图片的属性
    private boolean mShowOriginal = false;
    private String mOriginalPath = "";
    private String mCachePath;
    private String mSizeStr;

    //flutter原始传递数据
    Map<String, Object> mFlutterOriginalData;

    //用于网络初始化
    public MediaItem(int type, String path) {
        this.type = type;
        urlDataInit(type, path);
        if (mShowOriginal) {
            mCachePath = CacheMgr.getInstance().getCacheFilePath(new UrlKey(mOriginalPath));
            if (new File(mCachePath).exists()) { //显示本地图片
                mPath = mCachePath;
                mOriginalPath = mCachePath;
                mThumbPath = mCachePath;
                mShowOriginal = false;
            } else {    //没有缓存，显示网络图片
                mPath = path;
                mThumbPath = path;
                mShowOriginal = true;
            }
        } else {
            mPath = path;
            mThumbPath = path;
        }
    }

    //用于本地初始化
    public MediaItem(int type, String id, String path, String thumbPath,
                      long duration, long size, int width, int height) {
        this.type = type;
        mId = id;
        mPath = path;
        mThumbPath = thumbPath;
        mDuration = duration;
        mSize = size;
        mWidth = width;
        mHeight = height;
    }

    public MediaItem(Map<String, Object> attribute) {
        if (attribute == null) return;
        if (attribute.containsKey("path")) {
            mPath = (String) attribute.get("path");
        }

        if (attribute.containsKey("coverPath")) {
            mThumbPath = (String) attribute.get("coverPath");;
        } else {
            mThumbPath = mPath;
        }
        if (attribute.containsKey("type")) {
            String tempType = (String) attribute.get("type");
            if (tempType.equals("VIDEO")) {
                type = TYPE_VIDEO;
            } else if (tempType.equals("IMAGE")) {
                if (isGif(mPath)) {
                    type = TYPE_GIF;
                } else {
                    type = TYPE_IMAGE;
                }
            }
        }
        if (attribute.containsKey("w")) {
            mWidth = (int) attribute.get("w");
        }
        if (attribute.containsKey("h")) {
            mHeight = (int) attribute.get("h");
        }

        mFlutterOriginalData = attribute;
    }

    public void updateCacheImage() {
        if (!mShowOriginal) return;
        mCachePath = CacheMgr.getInstance().getCacheFilePath(new UrlKey(mOriginalPath));
        if (new File(mCachePath).exists()) { //显示本地图片
            mPath = mCachePath;
            mOriginalPath = mCachePath;
            mThumbPath = mCachePath;
            mShowOriginal = false;
        }
    }

    public int getType() {
        return type;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public String getId() {
        return mId;
    }

    public String getPath() {
        return mPath;
    }

    public String getThumbPath() {
        return mThumbPath;
    }

    public long getDuration() {
        return mDuration;
    }

    public long getSize() {return mSize;}

    public boolean isBigVideo() {
        if (mDuration > 300000) { //5分钟
            return true;
        }
        return false;
    }

    public boolean isLimitExceeded() {
        if (type == TYPE_VIDEO && mSize > 100 * 1024 * 1024) {
            return true;
        }
        return false;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean isShowOriginal() {
        return mShowOriginal;
    }

    public String getOriginalPath() {
        return mOriginalPath;
    }

    public String getCachePath() {
        return mCachePath;
    }

    public String getSizeStr() {
        return mSizeStr;
    }

    public String getEntityType() {
        if (type == TYPE_VIDEO) {
            return "VIDEO";
        }
        return "IMAGE";
    }

    public Map<String, Object> getFlutterOriginalData() {
        return mFlutterOriginalData;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof MediaItem) {
            return this.mPath.equals(((MediaItem) obj).mPath);
        }
        return super.equals(obj);
    }

    public boolean equalsOld(Object obj) {
        return super.equals(obj);
    }

    private void urlDataInit(int type, String url) {
        if (type == TYPE_VIDEO || url == null) return;
        int questionIndex = url.lastIndexOf("?");
        if (url.indexOf("full=1") > 0) {
            if (questionIndex < 0) return;
            mOriginalPath = url.substring(0, questionIndex);
            mShowOriginal = true;
        }
        int sizeIndex = url.indexOf("size=");
        if (sizeIndex > 0) {
            String tempStr = url.substring(sizeIndex+5);
            int tempIndex = tempStr.indexOf("&");
            if (tempIndex>0){
                tempStr = tempStr.substring(0, tempIndex);
            }
            mSizeStr = tempStr;
        }
    }

    private boolean isGif(String path) {
        if (path == null || path.isEmpty()) return false;
        path = path.toLowerCase();
        return path.indexOf(".gif") >= 0;
    }
}
