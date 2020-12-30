package plugin.album.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuli on 18/4/2.
 */

public class LimitClickUtils {
    private Map<String, OneClick> mClickMap = new HashMap<>();

    public boolean check(Object object) {
        return checkForTime(object, OneClick.MIN_CLICK_DELAY_TIME);
    }

    public boolean check() {
        return check(null);
    }

    public boolean checkForTime(Object object, int limitTime) {
        String flag = null;
        if (object == null) {
            flag = Thread.currentThread().getStackTrace()[2].getMethodName();
        } else {
            flag = object.toString();
        }
        if (mClickMap.get(flag) == null) {
            mClickMap.put(flag, new OneClick(limitTime));
        }
        return mClickMap.get(flag).check();
    }

    public boolean checkForTime(int limitTime) {
        return checkForTime(null, limitTime);
    }

    public void destory() {
        mClickMap.clear();
    }

    private class OneClick {
        public static final int MIN_CLICK_DELAY_TIME = 1000;

        private long mLastClickTime = 0;
        private int mLimitTime = MIN_CLICK_DELAY_TIME;

        public OneClick(int limitTime) {
            mLimitTime = limitTime;
        }

        public boolean check() {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - mLastClickTime > mLimitTime) {
                mLastClickTime = currentTime;
                return false;
            } else {
                return true;
            }
        }
    }
}
