package plugin.album;

public class Constants {
    public static final String CHANNEL_NAME = "flutter.plugin/customAlbum";
    public static final String OPEN_ALBUM = "openAlbum";
    public static final String GET_LATEST_MEDIA_FILE = "getLatestMediaFile";
    public static final String FILES_COMPRESS = "mediaCompress";

    public static final String TAKE_PHOTO = "takePhoto";

    public static final String COMPRESS_COMPLETION = "compressCompletion";

    public static final String IMAGES_PREVIEW = "imagesPreview";
    public static final String LOAD_HISTORY_Files = "loadHistoryFiles";
    public static final String ADD_HISTORY_FILES = "addHistoryFiles";

    public static final int REQUEST_CODE_OPEN_ALBUM = 5164;
    public static final int REQUEST_CODE_BIG_PICTURE = 5165;
    public static final int REQUEST_CODE_PREVIEW = 5166;
    public static final int REQUEST_CODE_CROP = 5167;
    public static final int REQUEST_CODE_CAMERA = 5168;

    public static final int RESULT_CODE_SEND = 5264;
    public static final int RESULT_CODE_SINGLE_IMAGE = 5265;

    public static final int CAMERA_TYPE_UNKNOWN = 0;
    public static final int CAMERA_TYPE_SEND = 1;
    public static final int CAMERA_TYPE_CROP = 2;

    public static final int albumTypeMultiple = 0;
    public static final int albumTypeRadioAddCrop = 1;
    public static final int albumTypeRadio = 2;

    public static final int TYPE_ALL_MEDIA = 0;
    public static final int TYPE_ALL_VIDEO = -1;
}
