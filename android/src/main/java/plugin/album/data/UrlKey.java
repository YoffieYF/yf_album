package plugin.album.data;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Key;

import java.security.MessageDigest;

import plugin.album.utils.StringUtils;

public class UrlKey implements Key {
    private String mUrl;
    private  String mSuffix;

    public UrlKey(String url) {
        if (url == null) return;
        mUrl = url;

        int index = url.lastIndexOf("/");
        if (index >= 0) {
            String temName = url.substring(index);
            int tempIndex = temName.lastIndexOf(".");
            if (tempIndex >= 0) {
                mSuffix = temName.substring(tempIndex);
            }
        }
    }

    public String getSuffix() {
        if (StringUtils.isNullOrEmpty(mSuffix)) {
            return "";
        }
        return mSuffix;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UrlKey && mUrl != null) {
            return mUrl.equals(((UrlKey) o).mUrl);
        }
        return false;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        try {
            messageDigest.update(mUrl.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
