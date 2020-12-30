package plugin.album.view.touchview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class ImageViewTouch extends ImageView {
    public ImageViewTouch(Context context) {
        super(context);
    }

    public ImageViewTouch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewTouch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageViewTouch(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }
}
