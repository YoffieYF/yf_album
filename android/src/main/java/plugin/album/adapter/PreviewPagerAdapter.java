package plugin.album.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import plugin.album.data.MediaItem;
import plugin.album.view.CustomImageView;
import plugin.album.view.ViewClickListener;
import plugin.album.view.alivideo.AliVideoView;

import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentPagerAdapter {
    private static final String POSITION_TYPE = "position";
    private static final String MEDIA_ITEM_TYPE = "media_item";
    private List<MediaItem> mListData = new ArrayList<>();
    private static ViewClickListener mListener;
    private static int mFirstShowIndex = -1;

    public PreviewPagerAdapter(@NonNull FragmentManager fm, int firstShowIndex, ViewClickListener listener) {
        super(fm);
        mFirstShowIndex = firstShowIndex;
        mListener = listener;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (mListData != null && mListData.size() > position) {
            MediaItem item = mListData.get(position);
            if (item.getType() == MediaItem.TYPE_IMAGE
                    || item.getType() == MediaItem.TYPE_GIF) {
                return PhotoFragment.newInstance(item, position);
            } else {
                return VideoFragment.newInstance(item, position);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mListData != null) {
            return mListData.size();
        }
        return 0;
    }

    public void setData(List<MediaItem> datas, boolean bRefresh) {
        mListData.clear();
        if (datas != null || datas.isEmpty()) {
            mListData.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public static class PhotoFragment extends Fragment {
        private MediaItem mItem;
        private int mPosition;
        private CustomImageView mPhotoView;

        public static PhotoFragment newInstance(MediaItem item, int position) {
            Bundle args = new Bundle();
            args.putSerializable(MEDIA_ITEM_TYPE, item);
            args.putInt(POSITION_TYPE, position);
            PhotoFragment fragment = new PhotoFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mPosition = arguments.getInt(POSITION_TYPE);
                mItem = (MediaItem) arguments.getSerializable(MEDIA_ITEM_TYPE);
            }

            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mPhotoView = new CustomImageView(getContext());
            mPhotoView.setData(mItem, mListener);
            mPhotoView.hideBottomController();
            return mPhotoView;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (mPhotoView != null) {
                if (!isVisibleToUser) {
                    mPhotoView.resetMatrix();
                }
            }
        }
    }

    public static class VideoFragment extends Fragment {
        private MediaItem mItem;
        private AliVideoView mSimpleVideoView;
        private int mPosition;

        public static VideoFragment newInstance(MediaItem item, int position) {
            Bundle args = new Bundle();
            args.putSerializable(MEDIA_ITEM_TYPE, item);
            args.putInt(POSITION_TYPE, position);
            VideoFragment fragment = new VideoFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mPosition = arguments.getInt(POSITION_TYPE);
                mItem = (MediaItem) arguments.getSerializable(MEDIA_ITEM_TYPE);
            }
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            boolean autoPlay = mPosition == mFirstShowIndex;
            if (autoPlay) mFirstShowIndex = -1;//补消费后重置
            mSimpleVideoView = new AliVideoView(getContext());
            if (mItem != null) {
                mSimpleVideoView.setSimpleModel(true);
                mSimpleVideoView.setUrl(mItem.getPath(), autoPlay, mListener);
            }
            return mSimpleVideoView;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (mSimpleVideoView != null) {
                mSimpleVideoView.resume();
            }
        }


        @Override
        public void onPause() {
            super.onPause();
            if (mSimpleVideoView != null) {
                mSimpleVideoView.pause();
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if (mSimpleVideoView != null) {
                mSimpleVideoView.stop();
            }
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (mSimpleVideoView != null) {
                if (!isVisibleToUser) {
                    mSimpleVideoView.pause();
                } else {
                    mSimpleVideoView.resume();
                }
            }
        }
    }
}
