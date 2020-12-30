package plugin.album.view.touchview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class LinearLayoutTouch extends LinearLayout {
    public LinearLayoutTouch(Context context) {
        super(context);
    }

    public LinearLayoutTouch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutTouch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestDisallowInterceptTouchEvent(true);
        return true;
    }
}
