package plugin.album.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import plugin.album.R;


public class ImageLoader {
    public static void loadCropImage(Context context, String imagePath, ImageView view) {
        Glide.with(context).load(imagePath)
                .apply(new RequestOptions().centerCrop().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE))
                .placeholder(R.mipmap.default_image)
                .fallback(R.mipmap.default_image)
                .error(R.mipmap.error_image)
                .into(view);

    }
    public static void loadImage(Context context, String imagePath, ImageView view, final ImageLoadListener listener) {
        RequestOptions requestOptions;
        if (StringUtils.isHttpUrl(imagePath)) {
            requestOptions = new RequestOptions().centerInside().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.DATA);
        } else {
            //本地图片不再存磁盘
            requestOptions = new RequestOptions().centerInside().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE);
        }

        Glide.with(context).load(imagePath)
                .apply(requestOptions)
                .format(DecodeFormat.PREFER_RGB_565)
                .override(Target.SIZE_ORIGINAL)
                .error(R.mipmap.error_image_big)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadSuccess();
                        }
                        return false;
                    }
                })
                .into(view);
    }

    public static void loadVideoScreenshot(final Context context, String imagePath, ImageView imageView) {
        RequestOptions requestOptions;
        if (StringUtils.isHttpUrl(imagePath)) {
            imagePath += "?x-oss-process=video/snapshot,t_0,f_jpg"; //阿里云支持取图片，直接使用会把整个视频下载下来
            requestOptions = new RequestOptions().centerInside().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.DATA);
        } else {
            //本地视频
            requestOptions = new RequestOptions().centerInside().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        Glide.with(context).load(imagePath)
                .apply(requestOptions)
                .format(DecodeFormat.PREFER_RGB_565)
                .override(Target.SIZE_ORIGINAL)
                .error(R.mipmap.error_image_big)
                .into(imageView);
    }


    public interface ImageLoadListener {
        void onLoadFailed();
        void onLoadSuccess();
    }

    public static void cleanMemory(Context context) {
        try {
            Glide.get(context).clearMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void trimMemory(Context context, int level) {
        try {
            Glide.get(context).trimMemory(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    }
