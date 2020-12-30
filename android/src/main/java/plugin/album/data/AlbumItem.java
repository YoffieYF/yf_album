package plugin.album.data;

public class AlbumItem {
    final public long bucketId;
    final public String AlbumName;
    public int counter;
    public String thumbnailPath;


    public AlbumItem(long bucketId, String AlbumName, String thumbnailPath, int counter) {
        this.bucketId = bucketId;
        this.AlbumName = AlbumName;
        this.counter = counter;
        this.thumbnailPath = thumbnailPath;
    }
}
