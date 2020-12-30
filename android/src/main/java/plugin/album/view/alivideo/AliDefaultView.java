package plugin.album.view.alivideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import plugin.album.R;
import plugin.album.utils.ImageLoader;

public class AliDefaultView extends RelativeLayout {
    private ImageView mImageView;

    public AliDefaultView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_alidefault_view, this);
        mImageView = findViewById(R.id.defaultImage);
    }


    public void setImagePath(String url) {
        ImageLoader.loadVideoScreenshot(getContext(), url, mImageView);
        setVisibility(VISIBLE);
    }

}
