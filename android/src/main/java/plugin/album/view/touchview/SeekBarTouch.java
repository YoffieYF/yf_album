package plugin.album.view.touchview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class SeekBarTouch extends SeekBar {
    public SeekBarTouch(Context context) {
        super(context);
    }

    public SeekBarTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarTouch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SeekBarTouch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }
}
