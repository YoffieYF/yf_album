package plugin.album.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import plugin.album.Constants;

import java.io.File;

public class Utils {
    public static final boolean isAndroidQ = Build.VERSION.SDK_INT >= 29;

    public static void captureImage(String outPath, Activity activity, int requestCode) {
        try {
            // 拍照到执行目录
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(outPath)));
            } else {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".fileprovider", new File(outPath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
        }
    }

    public static void startCropActivity(String imagePath, String outPath, Activity activity, int requestCode) {
        if (!checkFileExists(imagePath)) {
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".fileprovider", new File(imagePath));
            intent.setDataAndType(uri, "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(imagePath)), "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 720);
        intent.putExtra("outputY", 720);
        intent.putExtra("scale", true); //是否保留比例
        intent.putExtra("return-data", false); //是否将数据保留在Bitmap中返回
        intent.putExtra("scaleUpIfNeeded", true);//适应小图，去黑边
        intent.putExtra("noFaceDetection", true); //取消人脸识别
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(outPath)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.setAction("com.android.camera.action.CROP");
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkFileExists(String filePath) {
        return !StringUtils.isNullOrEmpty(filePath) && new File(filePath).exists();
    }

    public static boolean checkStoragePermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (requestCode > 0) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                return false;
            }
        }
        return true;
    }

    public static boolean checkCameraPermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (requestCode > 0) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA}, requestCode);
                }
                return false;
            }
        }

        return true;
    }

    public static boolean requestPermissionsResult(int[] grantResults) {
        boolean bRet = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                bRet = false;
            }
        }
        return bRet;
    }

    public static boolean isGif(String path) {
        if (path == null || path.isEmpty()) return false;
        path = path.toLowerCase();
        return path.indexOf(".gif") >= 0;
    }
}
