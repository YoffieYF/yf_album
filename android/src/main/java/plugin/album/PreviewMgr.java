package plugin.album;


import java.util.ArrayList;
import java.util.List;

import plugin.album.data.MediaItem;
import plugin.album.data.PreviewItem;

public class PreviewMgr {
    public static final int INVALID_INDEX = -1;
    public static final String KEY_PREVIEW = "preview";
    public static final int OPEN_SELECT = 1;
    public static final int OPEN_ALBUM = 2;

    private List<MediaItem> mOptionalList = new ArrayList<>();
    private List<PreviewItem> mSelectList = new ArrayList<>();
    private int mOpenMode;
    private int mInitialIndex = INVALID_INDEX;

    public void initData(int openMode, MediaItem openItem, List<MediaItem> optionalList, List<MediaItem> selectList) {
        mOpenMode = openMode;
        mSelectList.clear();
        mOptionalList.clear();
        if (optionalList == null || optionalList.isEmpty()) return;
        mOptionalList.addAll(optionalList);

        if (selectList != null && !selectList.isEmpty()) {
            //去重
            for (int i = 0; i < selectList.size(); ++i) {
                MediaItem item = selectList.get(i);
                int index = mOptionalList.indexOf(item);
                if (index != INVALID_INDEX) {
                    mOptionalList.remove(index);
                }
            }

            //把选中的插入可选项头部
            for (int i = 0; i < selectList.size(); ++i) {
                MediaItem item = selectList.get(i);
                mOptionalList.add(i, item);
                mSelectList.add(new PreviewItem(item, i, true));
            }
        }

        if (openItem != null) {
            mInitialIndex = mOptionalList.indexOf(openItem);
        } else {
            mInitialIndex = 0; //默认选第一项
        }
    }

    public int getFromSelectIndex(int optionalIndex) {
        if (optionalIndex >= 0 && optionalIndex < mOptionalList.size()) {
            MediaItem item = mOptionalList.get(optionalIndex);
            for (int i = 0; i < mSelectList.size(); ++i) {
                if (item.equals(mSelectList.get(i).mediaUir)) {
                    return i;
                }
            }
        }
        return INVALID_INDEX;
    }

    public int setSelectItem(int optionalIndex, boolean isChecked) {
        int selectIndex = optionalIndex;
        MediaItem item = mOptionalList.get(optionalIndex);
        if (isChecked) {
            if (mOpenMode == OPEN_SELECT) {
                mSelectList.get(optionalIndex).isSelect = true;
            } else {
                mSelectList.add(new PreviewItem(item, optionalIndex, true));
                selectIndex = mSelectList.size()-1;
            }
        } else {
            if (mOpenMode == OPEN_SELECT) {
                mSelectList.get(optionalIndex).isSelect = false;
            } else {
                selectIndex = getFromSelectIndex(optionalIndex);
                if (selectIndex != INVALID_INDEX) {
                    mSelectList.remove(selectIndex);
                }
            }
        }

        PickerMgr.getInstance().changeSelectItemState(item);
        return selectIndex;
    }

    public int getInitialIndex() {
        return mInitialIndex;
    }

    public boolean getSelectState(int selectIndex) {
        if (selectIndex >= 0 && selectIndex < mSelectList.size()) {
            return mSelectList.get(selectIndex).isSelect;
        }
        return false;
    }

    public PreviewItem getSelectItem(int selectIndex) {
        if (selectIndex >= 0 && selectIndex < mSelectList.size()) {
            return mSelectList.get(selectIndex);
        }
        return null;
    }

    public MediaItem getOptionalItem(int optionalIndex) {
        if (optionalIndex >= 0 && optionalIndex < mOptionalList.size()) {
            return mOptionalList.get(optionalIndex);
        }
        return null;
    }

    public boolean optionalIsBigVideo(int optionalIndex) {
        if (optionalIndex >= 0 && optionalIndex < mOptionalList.size()) {
            return mOptionalList.get(optionalIndex).isBigVideo();
        }
        return false;
    }

    public boolean optionalIsLimitExceeded(int optionalIndex) {
        if (optionalIndex >= 0 && optionalIndex < mOptionalList.size()) {
            return mOptionalList.get(optionalIndex).isLimitExceeded();
        }
        return false;
    }

    public List<MediaItem> getOptionalList() {
        return mOptionalList;
    }

    public List<PreviewItem> getSelectList() {
        return mSelectList;
    }

    private static final PreviewMgr sInstance = new PreviewMgr();
    private PreviewMgr() {}
    public static PreviewMgr getInstance() {
        return sInstance;
    }
}
