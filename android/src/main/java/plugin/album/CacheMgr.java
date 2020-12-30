package plugin.album;

import android.content.Context;

import com.bumptech.glide.load.engine.cache.SafeKeyGenerator;
import com.bumptech.glide.util.LruCache;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import plugin.album.data.UrlKey;
import plugin.album.utils.FileStorage;
import plugin.album.utils.StringUtils;

public class CacheMgr {
    private static final CacheMgr sInstance = new CacheMgr();
    public static CacheMgr getInstance() {
        return sInstance;
    }
    private SVGAParser mSvgaParser;
    private final LruCache<String, SVGAVideoEntity> mSvgaCache = new LruCache<>(10);
    private final Map<String, String> mVideoCachePaths = new HashMap<>();

    private String mCacheDir;
    private final SafeKeyGenerator safeKeyGenerator;
    private CacheMgr() {
        safeKeyGenerator = new SafeKeyGenerator();
    }

    public void loadLoadingRes(Context context, final SVGAParser.ParseCompletion callback) {
        loadSource(context, "svga/loading.svga", callback);
    }

    public void loadSource(Context context, final String assetsPath, final SVGAParser.ParseCompletion callback) {
        if (mSvgaCache.get(assetsPath) != null) {
            if (callback != null) {
                callback.onComplete(mSvgaCache.get(assetsPath));
            }
            return;
        }

        if (mSvgaParser == null) {
            mSvgaParser = new SVGAParser(context);
        }

        mSvgaParser.parse(assetsPath, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (callback != null) {
                    callback.onComplete(videoItem);
                }
                mSvgaCache.put(assetsPath, videoItem);
            }
            @Override
            public void onError() {
                if (callback != null) {
                    callback.onError();
                }
            }
        });
    }

    public String getCacheFilePath(UrlKey key) {
        String safeKey = safeKeyGenerator.getSafeKey(key);
        if (StringUtils.isNullOrEmpty(safeKey)) {
            return null;
        }

        if (mCacheDir == null) {
            mCacheDir = FileStorage.getOriginalCacheDir();
        }
        return mCacheDir + safeKey + key.getSuffix();
    }

    public String getSafeFileName(String filePath) {
        if (StringUtils.isNullOrEmpty(filePath)) {
            return null;
        }
        UrlKey key = new UrlKey(filePath);
        String safeKey = safeKeyGenerator.getSafeKey(key);
        if (StringUtils.isNullOrEmpty(safeKey)) {
            return null;
        }

        return safeKey + key.getSuffix();
    }

    public void setVideoCachePath(String url, String path) {
        if (StringUtils.isNullOrEmpty(url) || StringUtils.isNullOrEmpty(path)) {
            return;
        }
        mVideoCachePaths.put(url, path);
    }

    public String getVideoCachePath(String url) {
        if (url != null && mVideoCachePaths.containsKey(url)) {
            return mVideoCachePaths.get(url);
        }
        return "";
    }

    public void clearVideoCachePath() {
        mVideoCachePaths.clear();
    }
}
