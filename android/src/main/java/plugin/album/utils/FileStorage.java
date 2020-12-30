package plugin.album.utils;

import android.os.Environment;

import java.io.File;

import plugin.album.AlbumPlugin;

/**
 * Created by legend on 15/6/19.
 */
public class FileStorage {
    public enum Location {
        Cache,
        SDCard
    }

    private static final FileStorage sInstance = new FileStorage();

    public static FileStorage getInstance() {
        return sInstance;
    }

    public static boolean isStoreExist(Location location) {
        return getInstance().getRootDir(location) != null;
    }

    private File mCacheDirectory;
    private File mSDCardDirectory;

    private FileStorage() {
        init();
    }

    public File getRootDir(Location location) {
        switch (location) {
            case Cache:
                return mCacheDirectory;
            case SDCard:
                return mSDCardDirectory;
            default:
                return null;
        }
    }

    public static File getCacheDir() {
        return getInstance().getRootDir(Location.Cache);
    }

    public static String getCacheDirPath() {
        return getCacheDir().getPath();
    }
    public static String getCacheSubDir(String dirName) {
        return getCacheDirPath() + File.separator + dirName + File.separator;
    }

    public static String tempFilePath(String fileName, boolean isClear) {
        return tempDir(isClear) + fileName;
    }

    public static String clearTempDir() {
        String tempPath = FileStorage.getCacheSubDir(".temp");
        FileUtils.removeDir(tempPath);
        return tempPath;
    }

    public static String tempDir(boolean isClear) {
        String tempPath = FileStorage.getCacheSubDir(".temp");
        if (isClear) {
            FileUtils.removeDir(tempPath);
        }
        FileUtils.ensureFileDirExists(tempPath);
        return tempPath;
    }

    public static String cameraTempPath(String fileName, boolean isClear) {
        String tempPath = FileStorage.getCacheSubDir(".tempCamera");
        if (isClear) {
            FileUtils.removeDir(tempPath);
        }
        FileUtils.ensureFileDirExists(tempPath);
        return tempPath + fileName;
    }

    public static String getVideoCacheDir() {
        String saveDir = getCacheSubDir("video");
        FileUtils.ensureFileDirExists(saveDir);
        return saveDir;
    }

    public static String getImageCacheDir() {
        String saveDir = getCacheSubDir("image");
        FileUtils.ensureFileDirExists(saveDir);
        return saveDir;
    }

    public static String getOriginalCacheDir() {
        String saveDir = getCacheSubDir("original");
        FileUtils.ensureFileDirExists(saveDir);
        return saveDir;
    }

    public static String getCacheFilePath(String... paths) {
        String cacheDirPath = getCacheDirPath();
        StringBuilder filePath = new StringBuilder(cacheDirPath);
        for (String path : paths) {
            filePath.append(File.separator);
            filePath.append(path);
        }
        return filePath.toString();
    }

    // sdcard/Android/data/[package]/files/[process]
    public static File getSDCardDir() {
        return getInstance().getRootDir(Location.SDCard);
    }

    public static String getSDCardDirPath(boolean ensureExists) {
        String path = getSDCardDir().getPath();

        if (ensureExists) {
            FileUtils.ensureFileDirExists(path);
        }
        return path;
    }

    public static String getSDCardSubDir(String dirName, boolean ensureExists) {
        return getSDCardDirPath(ensureExists) + File.separator + dirName + File.separator;
    }

    public static String getSDCardSubPath(String fileName, boolean ensureExists) {
        return getSDCardDirPath(ensureExists) + File.separator + fileName;
    }

    public static String getMediaDownloadPath(String fileName, boolean ensureExists) {
        File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String dir = dcimDir.getAbsolutePath() + File.separator + "Meet";
        if (ensureExists) {
            FileUtils.ensureDirExists(dir);
        }
        return dir + File.separator + fileName;
    }

    // sdcard/Android/data/[package]/files/
    public static File getStorageDir() {
        return getSDCardDir().getParentFile();
    }

    public long getSize(Location location) {
        return IOUtils.getFileSize(getRootDir(location));
    }

    public boolean clear(Location location) {
        return IOUtils.removeFile(getRootDir(location), false);
    }

    public boolean exist(Location location, String relativePath) {
        PathInfo pathInfo = parsePath(relativePath);
        return exist(location, pathInfo.fileName, pathInfo.dirs);
    }

    public byte[] readBytes(Location location, String relativePath) {
        PathInfo pathInfo = parsePath(relativePath);
        return readBytes(location, pathInfo.fileName, pathInfo.dirs);
    }

    public boolean writeBytes(Location location, String relativePath, byte[] data) {
        PathInfo pathInfo = parsePath(relativePath);
        return writeBytes(location, pathInfo.fileName, data, pathInfo.dirs);
    }

    public boolean exist(Location location, String fileName, String... dirs) {
        return getFile(location, fileName, dirs) != null;
    }

    public File getFile(Location location, String fileName, String... dirs) {
        return IOUtils.getFile(getRootDir(location), fileName, dirs);
    }

    public byte[] readBytes(Location location, String fileName, String... dirs) {
        return IOUtils.readBytes(getRootDir(location), fileName, dirs);
    }

    public boolean writeBytes(Location location, String fileName, byte[] data
            , String... dirs) {
        return IOUtils.writeBytes(getRootDir(location), fileName, data, dirs);
    }

    private void init() {
        mCacheDirectory = tryToGetCacheDir();
        mSDCardDirectory = tryToGetSDCardDir();

        if (mCacheDirectory == null && mSDCardDirectory != null) {
            // 可获取SD卡目录，无缓存目录
            mCacheDirectory = IOUtils.createDirIfNoExist(mSDCardDirectory, "cache");
        } else if (mCacheDirectory != null && mSDCardDirectory == null) {
            // 可获取缓存目录，无SD卡目录
            mSDCardDirectory = createOwnSDCardDirIfNoExist(mCacheDirectory);
        }
    }

    // 内置Cache目录
    private File tryToGetCacheDir() {
        File cacheDir = null;
        try {
            if (FileUtils.isSDCardMounted()) {
                cacheDir = AlbumPlugin.gContext.getExternalCacheDir();
            }
        } catch (Exception ignored) {}

        if (testDirectoryAvailable(cacheDir)) {
            // SD卡
            return cacheDir;
        } else {
            // 内置存储
            cacheDir = AlbumPlugin.gContext.getCacheDir();
            if (testDirectoryAvailable(cacheDir)) {
                return cacheDir;
            }
        }
        return null;
    }

    private File tryToGetSDCardDir() {
        File sdCardDir = null;
        try {
            sdCardDir = Environment.getExternalStorageDirectory();
        } catch (Exception e) {
            return null;
        }

        if (sdCardDir == null) {
            return null;
        }
        // 创建进程或Tag目录
        File ownSDCardDir = createOwnSDCardDirIfNoExist(sdCardDir);
        if (testDirectoryAvailable(ownSDCardDir)) {
            return ownSDCardDir;
        }
        // 处理多个SD卡的情况
        sdCardDir = new File(sdCardDir.getAbsolutePath().replace("0", "1"));
        ownSDCardDir = createOwnSDCardDirIfNoExist(sdCardDir);
        if (testDirectoryAvailable(ownSDCardDir)) {
            return ownSDCardDir;
        }
        return null;
    }

    private File createOwnSDCardDirIfNoExist(File root) {
        return IOUtils.createDirIfNoExist(root);
    }

    private boolean testDirectoryAvailable(File dir) {
        return dir != null && dir.isDirectory() && dir.canRead() && dir.canWrite();
    }

    private static PathInfo parsePath(String relativePath) {
        if (relativePath.startsWith(File.separator)) {
            relativePath = relativePath.substring(1);
        }
        if (relativePath.length() == 0) {
            throw new IllegalArgumentException("relativePath is empty");
        }
        String[] paths = relativePath.split(File.separator);
        int length = paths.length;
        PathInfo pathInfo = new PathInfo();
        if (length == 1) {
            pathInfo.fileName = relativePath;
            pathInfo.dirs = new String[0];
        } else {
            String[] dirs = new String[length - 1];
            ArrayUtils.remove(paths, dirs, paths.length - 1);
            pathInfo.fileName = paths[length - 1];
            pathInfo.dirs = dirs;
        }
        return pathInfo;
    }

    private static class PathInfo {
        public String fileName;
        public String[] dirs;
    }

}
