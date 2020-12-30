package plugin.album.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import plugin.album.AlbumPlugin;
import plugin.album.MediaMgr;
import plugin.album.R;
import plugin.album.adapter.MediaPagerAdapter;
import plugin.album.data.MediaItem;
import plugin.album.dialog.DownMediaDialog;
import plugin.album.dialog.MoreDialogFragment;
import plugin.album.utils.Utils;
import plugin.album.view.EasyPullLayout;
import plugin.album.view.ViewClickListener;
import plugin.album.view.ViewPagePageChange;
import plugin.album.view.ViewPagerFixed;

public class MediaActivity extends FragmentActivity {
    private static final int STORAGE_PERMISSION_CODE = 1001;
    private ViewPagerFixed mViewPager;
    private ViewPagePageChange mPageChangeListener;
    private List<MediaItem> mItemList = new ArrayList<>();
    private MediaPagerAdapter mViewPagerAdapter;
    private MediaMgr sMediaMgr;
    private int mInitIndex = 0;
    private int mCurrentSelect = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_multimedia);

        initData();
        initView();
    }

    private void initView() {
        mViewPagerAdapter = new MediaPagerAdapter(getSupportFragmentManager(), mInitIndex, new ViewClickListener() {
            @Override
            public boolean onLongClick() {
                MoreDialogFragment.show(MediaActivity.this, mMenuItemClick);
                return true;
            }

            @Override
            public void onClickListener() {
                if (mCurrentSelect >= 0 && mCurrentSelect < mItemList.size()) {
                    MediaItem item = mItemList.get(mCurrentSelect);
                    if (item.getType() == MediaItem.TYPE_IMAGE
                            || item.getType() == MediaItem.TYPE_GIF) {
                        finish();
                    }
                }
            }

            @Override
            public void onCloseClick() {
                finish();
            }

            @Override
            public void onMoreClick() {
                MoreDialogFragment.show(MediaActivity.this, mMenuItemClick);
            }
        });
        mViewPagerAdapter.setData(mItemList, false);

        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(mInitIndex, false);
        mPageChangeListener = new ViewPagePageChange(new ViewPagePageChange.LoadDataListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentSelect = position;
            }
        });
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        final EasyPullLayout pullView = findViewById(R.id.easy_pull_layout);
        pullView.addOnPullListenerAdapter(new EasyPullLayout.OnPullListenerAdapter() {
            @Override
            public void onTriggered(int type) {
                pullView.stop();
                if (type == EasyPullLayout.TYPE_EDGE_LEFT) {
                    sMediaMgr.loadHistoryData(mCurrentSelect);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdate(MediaMgr.ListDataUpdateAction action) {
        mItemList = sMediaMgr.getMultimediaItems();
        mCurrentSelect = sMediaMgr.getSelectIndex();
        mViewPagerAdapter.setData(mItemList, true);
        mViewPager.setCurrentItem(mCurrentSelect, false);
    }

    private void initData() {
        sMediaMgr = MediaMgr.getInstance();
        mItemList = sMediaMgr.getMultimediaItems();
//        mItemList = sMediaMgr.getTestData(this);
        mInitIndex = sMediaMgr.getSelectIndex();
        mCurrentSelect = mInitIndex;
    }

    private MoreDialogFragment.OnMenuItemClick mMenuItemClick = new MoreDialogFragment.OnMenuItemClick() {
        @Override
        public void onDown() {
            if (Utils.checkStoragePermission(MediaActivity.this, STORAGE_PERMISSION_CODE)) {
//                sMediaMgr.downMultimedia(mItemList.get(mCurrentSelect));
                DownMediaDialog.showDialog(MediaActivity.this, mItemList.get(mCurrentSelect));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (Utils.requestPermissionsResult(grantResults)) {
//                sMediaMgr.downMultimedia(mItemList.get(mCurrentSelect));
                DownMediaDialog.showDialog(this, mItemList.get(mCurrentSelect));
            } else {
                Toast.makeText(AlbumPlugin.gContext, getString(R.string.err_hint_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
