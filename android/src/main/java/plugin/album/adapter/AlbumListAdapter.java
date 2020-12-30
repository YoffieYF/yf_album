package plugin.album.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import plugin.album.R;
import plugin.album.data.AlbumItem;
import plugin.album.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends BaseAdapter {
    private int lastSelected = 0;
    private List<AlbumItem> albumItemList = new ArrayList<>();

    public AlbumListAdapter() {
    }

    public void refreshData(List<AlbumItem> folders) {
        albumItemList.clear();
        if (folders != null) {
            albumItemList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return albumItemList.size();
    }

    @Override
    public AlbumItem getItem(int position) {
        return albumItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.album_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AlbumItem folder = getItem(position);
        holder.folderName.setText(folder.AlbumName);
        holder.imageCount.setText("(" + folder.counter + ")");
        ImageLoader.loadCropImage(convertView.getContext(), folder.thumbnailPath, holder.cover);

        if (lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) {
            return;
        }
        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        public ViewHolder(View view) {
            cover = view.findViewById(R.id.iv_cover);
            folderName = view.findViewById(R.id.tv_folder_name);
            imageCount = view.findViewById(R.id.tv_image_count);
            folderCheck = view.findViewById(R.id.iv_folder_check);
            view.setTag(this);
        }
    }
}
