package plugin.album.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import plugin.album.AlbumPlugin;
import plugin.album.PickerMgr;
import plugin.album.R;
import plugin.album.data.MediaItem;
import plugin.album.utils.ImageLoader;
import plugin.album.utils.StringUtils;

public class PickerGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnPhotoActionListener mActionListener;
    private PickerMgr mPickerMgr;
    private List<MediaItem> mItemList = new ArrayList<>();
    private int mType;

    public PickerGridAdapter(int type) {
        mPickerMgr = PickerMgr.getInstance();
        mType = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_thumb_item, parent, false);
        return new ViewHolderImage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolderImage vh = (ViewHolderImage) holder;
        final MediaItem item = mItemList.get(position);
        final Context context = vh.item.getContext();
        vh.item.setTag(item);
        if (item.getType() == MediaItem.TYPE_VIDEO) {
            vh.viewDuration.setText(StringUtils.getMinuteTime(item.getDuration()));
            vh.viewDuration.setVisibility(View.VISIBLE);
        } else {
            vh.viewDuration.setVisibility(View.GONE);
        }
        setThumbCount(mPickerMgr.getSelectedImages().indexOf(item), vh.textView);
        ImageLoader.loadCropImage(context, item.getThumbPath(), vh.imgThumbImage);
        vh.viewCountBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckStateChange(vh, item, context);
            }
        });
        vh.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onClick(item);
                }
            }
        });

        if (mType != 0) {
            vh.viewCountBg.setVisibility(View.GONE);
        }
    }

    public void setThumbCount(int selectIndex, TextView textView) {
        if (selectIndex != -1) {
            textView.setText(String.valueOf(selectIndex + 1));
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void onCheckStateChange(ViewHolderImage vh, MediaItem image, Context context) {
        ArrayList<MediaItem> pickedImages = mPickerMgr.getSelectedImages();
        boolean isContained = pickedImages.contains(image);
        if (pickedImages.size() >= 9 && !isContained) {
            Toast.makeText(AlbumPlugin.gContext, context.getString(R.string.err_hint_select_count), Toast.LENGTH_SHORT).show();
            return;
        } else if (!isContained && image.isLimitExceeded()) {
            Toast.makeText(AlbumPlugin.gContext, context.getString(R.string.err_hint_video_size), Toast.LENGTH_SHORT).show();
            return;
        } else if (!isContained && image.isBigVideo()) {
            Toast.makeText(AlbumPlugin.gContext, context.getString(R.string.err_hint_video_length), Toast.LENGTH_SHORT).show();
            return;
        } else if (!isContained && mType == 2 && image.getSize() >= 10 * 1024 * 1024) {
            Toast.makeText(AlbumPlugin.gContext, context.getString(R.string.err_hint_emoji_size), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isContained) {
            pickedImages.remove(image);
            setThumbCount(-1, vh.textView);
            if (mActionListener != null) mActionListener.onDeselect();
        } else {
            pickedImages.add(image);
            setThumbCount(pickedImages.size() - 1, vh.textView);
        }
    }

    public void refreshData(List<MediaItem> imageList) {
        mItemList.clear();
        if (imageList != null) {
            mItemList.addAll(imageList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public List getItemList() {
        return mItemList;
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder {
        View item;
        ImageView imgThumbImage;
        TextView textView;
        ImageView videoTag;
        View viewCountBg;
        TextView viewDuration;

        public ViewHolderImage(View view) {
            super(view);
            item = view;
            imgThumbImage = view.findViewById(R.id.img_thumb_image);
            textView = view.findViewById(R.id.tv_select);
            videoTag = view.findViewById(R.id.video_tag);
            viewCountBg = view.findViewById(R.id.view_count_bg);
            viewDuration = view.findViewById(R.id.tv_duration);
        }
    }

    public interface OnPhotoActionListener {
        void onDeselect();

        void onClick(MediaItem item);
    }

    public void setActionListener(OnPhotoActionListener actionListener) {
        mActionListener = actionListener;
    }
}
