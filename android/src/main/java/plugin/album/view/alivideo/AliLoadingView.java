package plugin.album.view.alivideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import plugin.album.R;


public class AliLoadingView extends RelativeLayout {
    public AliLoadingView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_aliloading_view, this);
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    public void show(boolean isShow) {
        setVisibility(isShow ? VISIBLE : GONE);
    }
}
