package plugin.album.view;

import androidx.viewpager.widget.ViewPager;

public class ViewPagePageChange implements ViewPager.OnPageChangeListener {
    private enum Direction {LEFT, RIGHT, MIDDLE}
    private int mPositionOffsetPixels = -1;
    private Direction mDirection = Direction.MIDDLE;// 向右
    private int mLeftLoadPage = -10000;
    private int mRightLoadPage = 100000;
    private LoadDataListener mLoadDataListener;

    public ViewPagePageChange(LoadDataListener listener) {
        mLoadDataListener = listener;
    }

    public void setLeftLoadNotification(int left) {
        mLeftLoadPage = left;
    }

    public void setRightLoadNotification(int right) {
        mRightLoadPage = right;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset != 0) {
            if (mPositionOffsetPixels > positionOffsetPixels) {
                mDirection = Direction.LEFT; //左
            } else if (mPositionOffsetPixels < positionOffsetPixels) {
                mDirection = Direction.RIGHT; //右
            } else {
                mDirection = Direction.MIDDLE;
            }
        }
        mPositionOffsetPixels = positionOffsetPixels;
    }

    @Override
    public void onPageSelected(int position) {
        if (mLoadDataListener == null) return;

        mLoadDataListener.onPageSelected(position);

        if (mDirection == Direction.LEFT && position <= mLeftLoadPage) {
            mDirection = Direction.MIDDLE; //只响应一次
            mLoadDataListener.loadMoreLeftData(position);
        } else if (mDirection == Direction.RIGHT && position >= mRightLoadPage) {
            mDirection = Direction.MIDDLE; //只响应一次
            mLoadDataListener.loadMoreRightData(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    public static abstract class LoadDataListener {
        public void loadMoreLeftData(int position) {
        }

        public void loadMoreRightData(int position) {
        }

        public void onPageSelected(int position){
        }
    }
}
