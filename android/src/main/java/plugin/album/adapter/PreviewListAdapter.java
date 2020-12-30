package plugin.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import plugin.album.R;
import plugin.album.data.MediaItem;
import plugin.album.data.PreviewItem;
import plugin.album.utils.ImageLoader;
import plugin.album.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PreviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PreviewItem> mItemList = new ArrayList<>();
    private OnSelectListener listener;

    public PreviewListAdapter(List<PreviewItem> itemList, OnSelectListener listener) {
        this.mItemList = itemList; //数据由PreviewMgr统一管理
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_thumb_item, parent, false);
        return new ViewHolderImage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderImage vh = (ViewHolderImage) holder;
        PreviewItem previewItem = mItemList.get(position);
        final Context context = vh.item.getContext();
        vh.item.setTag(previewItem);
        if (previewItem.mediaUir.getType() == MediaItem.TYPE_VIDEO) {
            vh.viewDuration.setText(StringUtils.getMinuteTime(previewItem.mediaUir.getDuration()));
            vh.viewDuration.setVisibility(View.VISIBLE);
        } else {
            vh.viewDuration.setVisibility(View.GONE);
        }
        vh.ivCheck.setVisibility(previewItem.isSelect?View.VISIBLE:View.GONE);
        ImageLoader.loadCropImage(context, previewItem.mediaUir.getThumbPath(), vh.imgThumbImage);
        vh.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnClickListener(position);
                }
            }
        });
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder {
        View item;
        ImageView imgThumbImage;
        ImageView ivCheck;
        ImageView videoTag;
        TextView viewDuration;

        public ViewHolderImage(View view) {
            super(view);
            item = view;
            imgThumbImage = view.findViewById(R.id.img_thumb_image);
            ivCheck = view.findViewById(R.id.iv_select);
            ivCheck.setVisibility(View.VISIBLE);
            videoTag = view.findViewById(R.id.video_tag);
            viewDuration = view.findViewById(R.id.tv_duration);
        }
    }

    public interface OnSelectListener {
        void OnClickListener(int position);
    }
}
