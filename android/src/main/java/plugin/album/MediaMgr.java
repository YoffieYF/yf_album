package plugin.album;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import plugin.album.data.MediaItem;

public class MediaMgr {
    private static final MediaMgr sInstance = new MediaMgr();
    private MediaMgr() {}
    public static MediaMgr getInstance() {
        return sInstance;
    }
    private AlbumPlugin mAlbumPlugin;

    public void initAlbumPlugin(AlbumPlugin albumPlugin) {
        mAlbumPlugin = albumPlugin;
    }

    private List<MediaItem> mMediaItems = new ArrayList<>();
    private int mSelectIndex = 0;


    public boolean setMultimediaItems(List<String> urlList, int selectIndex) {
        if (urlList == null || urlList.isEmpty()) return false;

        mSelectIndex = selectIndex;
        mMediaItems.clear();

        for (int i = 0; i < urlList.size(); ++i) {
            MediaItem tempItem = createItem(urlList.get(i));
            if (tempItem != null) {
                mMediaItems.add(tempItem);
            }
        }
        if (!mMediaItems.isEmpty()) {
            return true;
        }
        return false;
    }

    public List<MediaItem> getMultimediaItems() {
        return mMediaItems;
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }

    public void insertData(List<String> urlList) {
        if (urlList == null || urlList.isEmpty()) return;

        List<MediaItem> newList =  new ArrayList<>();
        for (int i = 0; i < urlList.size(); ++i) {
            MediaItem tempItem = createItem(urlList.get(i));
            if (tempItem != null) {
                newList.add(tempItem);
            }
        }

        mSelectIndex = mSelectIndex + newList.size();
        mMediaItems.addAll(0, newList);

        EventBus.getDefault().post(new ListDataUpdateAction());
    }

    public void loadHistoryData(int currentSelect) {
        mSelectIndex = currentSelect;
        if (mAlbumPlugin != null)
            mAlbumPlugin.loadHistoryFile();
    }

    public MediaItem createItem(String url) {
        String filePath = url;
        int pos = filePath.indexOf("?");
        if (pos > 0) {
            filePath = filePath.substring(0, pos);
        }
        int type;
        if (isPicture(filePath)) {
            type = MediaItem.TYPE_IMAGE;
        } else if (isVideo(filePath)) {
            type = MediaItem.TYPE_VIDEO;
            url = filePath;
        } else if (isGif(filePath)) {
            type = MediaItem.TYPE_GIF;
            url = filePath;
        } else {
            return null;
        }
        return new MediaItem(type, url);
    }

    public boolean isPicture(String path) {
        if (path == null || path.isEmpty()) return false;
        return Pattern.compile(".(png|jpe?g|svg|webp|heic|bmp)").matcher(path.toLowerCase()).find();
    }

    public boolean isGif(String path) {
        if (path == null || path.isEmpty()) return false;
        return Pattern.compile(".(gif)").matcher(path.toLowerCase()).find();
    }

    public boolean isVideo(String path) {
        if (path == null || path.isEmpty()) return false;
        return Pattern.compile(".(mp4|video|mov|qlv)").matcher(path.toLowerCase()).find();
    }

    public static class ListDataUpdateAction {}

}
