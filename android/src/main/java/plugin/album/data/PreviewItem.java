package plugin.album.data;

public class PreviewItem {
    public MediaItem mediaUir;
    public int index;
    public boolean isSelect;

    public PreviewItem(MediaItem mediaUir, int index, boolean isSelect) {
        this.mediaUir = mediaUir;
        this.index = index;
        this.isSelect = isSelect;
    }
}
