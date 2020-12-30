package plugin.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import plugin.album.activity.MediaActivity;
import plugin.album.activity.PickerActivity;
import plugin.album.utils.FileStorage;
import plugin.album.utils.MediaCompression;
import plugin.album.utils.MediaInfoExtract;
import plugin.album.utils.Utils;

/**
 * AlbumPlugin
 */
public class AlbumPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler, PluginRegistry.ActivityResultListener {
    public static Context gContext;
    private MethodChannel channel;
    private Activity mActivity;
    private Map<String, Result> mMethodResultMgt = new HashMap<>();
    private int mOpenAlbumType = 0;

    private int mCameraType = Constants.CAMERA_TYPE_UNKNOWN;
    private String mCameraFilePath = "";
    private String mCropSource = "";
    private String mCropFilePath = "";

    public AlbumPlugin() {
    }

    public AlbumPlugin(MethodChannel channel) {
        this.channel = channel;
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        gContext = flutterPluginBinding.getApplicationContext();
        PickerMgr.getInstance().initAlbumPlugin(this);
        MediaMgr.getInstance().initAlbumPlugin(this);

        channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), Constants.CHANNEL_NAME);
        channel.setMethodCallHandler(this);
        flutterPluginBinding.getApplicationContext();
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), Constants.CHANNEL_NAME);
        AlbumPlugin albumPlugin = new AlbumPlugin(channel);
        AlbumPlugin.gContext = registrar.context();
        PickerMgr.getInstance().initAlbumPlugin(albumPlugin);
        MediaMgr.getInstance().initAlbumPlugin(albumPlugin);

        albumPlugin.setContext(registrar.activity());
        channel.setMethodCallHandler(albumPlugin);
        registrar.addActivityResultListener(albumPlugin);
    }

    public void onSendMediaFile(Map<String, Object> entity) {
        channel.invokeMethod(Constants.COMPRESS_COMPLETION, entity);
    }

    public void loadHistoryFile() {
        channel.invokeMethod(Constants.LOAD_HISTORY_Files, "");
    }

    @Override
    public void onMethodCall(@NonNull final MethodCall methodCall, @NonNull Result result) {
        addMethodResult(methodCall.method, result);

        if (Constants.OPEN_ALBUM.equals(methodCall.method)) {
            mOpenAlbumType = (int) methodCall.arguments;
            if (Utils.checkStoragePermission(mActivity, -1)) {
                Intent intent = new Intent(mActivity, PickerActivity.class);
                intent.putExtra("type", mOpenAlbumType);
                mActivity.startActivityForResult(intent, Constants.REQUEST_CODE_OPEN_ALBUM);
            } else {
                onError(methodCall.method, "1", "没有权限！");
            }
        } else if (Constants.GET_LATEST_MEDIA_FILE.equals(methodCall.method)) {
            int count = (int) methodCall.arguments;
            PickerMgr.getInstance().getLatestMediaFile(count, new MediaInfoExtract.OnGetInfoListener() {
                @Override
                public void onGetInfoComplete(List<Map<String, Object>> dataList) {
                    onSuccess(methodCall.method, dataList);
                }
            });
        } else if (Constants.TAKE_PHOTO.equals(methodCall.method)) {
            mCameraType = (int) methodCall.arguments;
            mCameraFilePath = FileStorage.cameraTempPath(System.currentTimeMillis() + ".jpg", true);
            if (Utils.checkCameraPermission(mActivity, -1)) {
                Utils.captureImage(mCameraFilePath, mActivity, Constants.REQUEST_CODE_CAMERA);
            }
        } else if (Constants.FILES_COMPRESS.equals(methodCall.method)) {
            boolean bRet = PickerMgr.getInstance().onMediaFileCompress(methodCall.arguments);
            if (bRet) {
                onSuccess(methodCall.method, null);
            } else {
                onError(methodCall.method, "1", "参数错误！");
            }
        } else if (Constants.IMAGES_PREVIEW.equals(methodCall.method)) {
            if (methodCall.arguments instanceof HashMap) {
                HashMap<String, Object> arguments = (HashMap<String, Object>) methodCall.arguments;
                ArrayList<String> urlList = (ArrayList<String>) arguments.get("data");
                int selectIndex = (int) arguments.get("idx");
                boolean bRet = MediaMgr.getInstance().setMultimediaItems(urlList, selectIndex);
                if (bRet) {
                    Intent intent = new Intent(mActivity, MediaActivity.class);
                    mActivity.startActivityForResult(intent, Constants.REQUEST_CODE_BIG_PICTURE);
                } else {
                    onError(methodCall.method, "0", "data error");
                }
            } else {
                onError(methodCall.method, "0", "data error");
            }
        } else if (Constants.ADD_HISTORY_FILES.equals(methodCall.method)) {
            if (methodCall.arguments instanceof ArrayList) {
                ArrayList<String> urlList = (ArrayList<String>) methodCall.arguments;
                MediaMgr.getInstance().insertData(urlList);
            }
            onSuccess(methodCall.method, null);
        } else {
            onError(methodCall.method, "0", "not method name");
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_OPEN_ALBUM) {
            if (resultCode == Constants.RESULT_CODE_SEND) {
                onSuccess(Constants.OPEN_ALBUM, PickerMgr.getInstance().getSelectedPath());
            } else if (resultCode == Constants.RESULT_CODE_SINGLE_IMAGE && data != null) {
                onSuccess(Constants.OPEN_ALBUM, data.getStringExtra("path"));
            } else {
                onSuccess(Constants.OPEN_ALBUM, Collections.emptyList());
            }
            PickerMgr.getInstance().clear();
        } else if (requestCode == Constants.REQUEST_CODE_CAMERA) {
            if (Utils.checkFileExists(mCameraFilePath)) {
                if (mCameraType == Constants.CAMERA_TYPE_SEND) {
                    PickerMgr.getInstance().mediaItemCompress(mCameraFilePath,
                            new MediaCompression.OnCompressionListener() {
                                @Override
                                public void onCompressionComplete(Map<String, Object> data) {
                                    if (data != null) {
                                        onSuccess(Constants.TAKE_PHOTO, data);
                                    } else {
                                        onError(Constants.TAKE_PHOTO, "1", "data error");
                                    }
                                }
                            });
                } else if (mCameraType == Constants.CAMERA_TYPE_CROP) {
                    mCropSource = Constants.TAKE_PHOTO;
                    mCropFilePath = FileStorage.tempFilePath(System.currentTimeMillis() + ".jpg", true);
                    Utils.startCropActivity(mCameraFilePath, mCropFilePath, mActivity, Constants.REQUEST_CODE_CROP);
                }
            } else {
                onError(Constants.TAKE_PHOTO, "1", "data error");
            }
        } else if (requestCode == Constants.REQUEST_CODE_CROP) {
            if (Utils.checkFileExists(mCropFilePath)) {
                onSuccess(Constants.TAKE_PHOTO, mCropFilePath);
            } else {
                onError(Constants.TAKE_PHOTO, "1", "data error");
            }
        } else {
            return false;
        }

        return true;
    }


    private void onSuccess(String methodName, Object object) {
        if (mMethodResultMgt.containsKey(methodName)) {
            mMethodResultMgt.get(methodName).success(object);
            mMethodResultMgt.remove(methodName);
        }
    }

    private void onError(String methodName, String errorCode, String errorMessage) {
        if (mMethodResultMgt.containsKey(methodName)) {
            mMethodResultMgt.get(methodName).error(errorCode, errorMessage, null);
            mMethodResultMgt.remove(methodName);
        }
    }

    private boolean addMethodResult(String methodName, MethodChannel.Result result) {
        if (mMethodResultMgt.containsKey(methodName)) {
            return false;
        }
        mMethodResultMgt.put(methodName, result);
        return true;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        setContext(binding.getActivity());
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        setContext(binding.getActivity());
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivity() {
    }

    public void setContext(Activity activity) {
        this.mActivity = activity;
    }
}
