package plugin.album.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import plugin.album.CacheMgr;
import plugin.album.R;


public class ProgressView extends FrameLayout implements SVGAParser.ParseCompletion {
    private SVGAImageView mSvgaView;

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, this);
        mSvgaView = findViewById(R.id.iv_anim_preview);
        mSvgaView.setLoops(-1);
        mSvgaView.setImageDrawable(null);
        CacheMgr.getInstance().loadLoadingRes(getContext(),this);
    }

    @Override
    public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
        SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
        mSvgaView.setImageDrawable(drawable);
        mSvgaView.startAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override
    public void onError() {
    }
}
