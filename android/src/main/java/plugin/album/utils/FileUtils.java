package plugin.album.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.Pair;
import android.util.Size;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private FileOutputStream mFileOutputStream;
    private BufferedOutputStream mBufferedOutputStream;
    private File mFile;

    public static String getExternalFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }
        return path;
    }

    public static boolean copyFileFromAssets(Context context, String fileName, boolean overwrite) {
        String path = getExternalFilePath(context, fileName);
        if (path != null) {
            File file = new File(path);
            if (file.exists() && overwrite) {
                file.delete();
            }
            if (!file.exists()) {
                try {
                    if (!ensureFileDirExists(path)) {
                        return false;
                    }
                    file.createNewFile();
                    InputStream in = context.getApplicationContext().getAssets().open(fileName);
                    if (in == null) {
                        return false;
                    }
                    //output
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    //
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static String getExternalStorageDirectoryAbsolutePath() {
        if (!externalStorageExist()) {
            Log.e(TAG, "system storage not mounted");
            return null;
        }

        File externalDir = Environment.getExternalStorageDirectory();
        if (null == externalDir) {
            Log.e(TAG, "external storage still null");
            return null;
        }

        String externalDirPath = externalDir.getAbsolutePath();
        String testDir = externalDirPath + "/test";
        boolean valid = createDir(testDir, false);

        if (valid) {
            return externalDirPath;
        }

        testDir = testDir.replace("0", "1");
        valid = createDir(testDir, false);
        if (valid) {
            return externalDirPath.replace("0", "1");
        }

        return null;
    }

    public static String getExternalStorageState() {
        String state = "";
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return state;
    }

    public static String getTxtFileContent(Context context, String fileName) {
        StringBuilder content = new StringBuilder();
        if (StringUtils.isNullOrEmpty(fileName)) {
            return content.toString();
        }
        File file = new File(fileName);
        if (file.isFile()) {
            InputStream instream = null;
            try {
                if (fileName.startsWith(context.getFilesDir().getPath())) {
                    instream = context.openFileInput(FileUtils.getFileName(fileName));
                } else {
                    instream = new FileInputStream(file);
                }
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    while ((line = buffreader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
            } catch (Exception e) {
                Log.e("getTxtFileContent", "read fail, e = " + e);
            } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (Exception ignored) { }
                }
            }
        }
        return content.toString();
    }

    public static File createFileOnSD(String dir, String name) {
        File file = null;
        if (isSDCardMounted()) {
            String dirPath = Environment.getExternalStorageDirectory().getPath() + dir;
            if (!createDir(dirPath, true)) {
                dirPath = dirPath.replace("0", "1");
                createDir(dirPath, true);
            }
            file = new File(dirPath + "/" + name);
            try {
                if (!file.exists() && !file.createNewFile()) {
                    file = null;
                }
            } catch (IOException e) {
                Log.e("FileUtils", "can not create file on SD card");
                file = null;
            }
        }
        return file;
    }

    public static String getFileExtension(String filePath) {
        String fileName = getFileName(filePath);
        if (StringUtils.isNullOrEmpty(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index);
        }
        return null;
    }

    public static String getFileName(String filePath) {
        if (filePath != null) {
            final String question = "?";
            final int posQuestion = filePath.lastIndexOf(question);
            if (posQuestion > 0) {
                filePath = filePath.substring(0, posQuestion);
            }
            final String slash = "/";
            final int pos = filePath.lastIndexOf(slash) + 1;
            if (pos > 0) {
                String name = filePath.substring(pos);
                return name.trim();
            }
        }
        return null;
    }

    public static String getFileNameWithoutExtension(String filePath) {
        String fileName = getFileName(filePath);
        if (StringUtils.isNullOrEmpty(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return null;
    }

    public static String getFileNameWithVer(String url) {
        String fileName;
        String versionName = "";
        String subName = getFileName(url);
        if (subName != null && subName.contains("?")) {
            String[] tempArr = subName.split("\\?");
            if (tempArr.length > 1) {
                subName = tempArr[0];
                versionName = tempArr[1];
            }
        }
        fileName = dropExt(subName);
        fileName = fileName + versionName;
        return fileName;
    }

    /* drop the extesion of a filename */
    public static String dropExt(String fname) {
        if (!StringUtils.isNullOrEmpty(fname)) {
            int pos = fname.lastIndexOf(".");
            if (pos != -1)
                return fname.substring(0, pos);
        }
        return fname;
    }

    public static boolean isFileExisted(String filePath) {
        if (StringUtils.isNullOrEmpty(filePath)) {
            return false;
        }
        try {
            File file = new File(filePath);
            return (file.exists() && file.length() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    public static void renameFile(String oldFile, String newFile) {
        try {
            File file = new File(oldFile);
            file.renameTo(new File(newFile));
        } catch (Exception ignored) { }
    }

    public static boolean copyFile(String inFileName, String outFileName) {
        boolean success = false;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File inFile = new File(inFileName);
            File outFile = new File(outFileName);

            fis = new FileInputStream(inFile);
            fos = new FileOutputStream(outFile);
            byte[] buf = new byte[2048];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
            success = true;
        } catch (Exception e) {
            success = false;
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (Exception e) {
                success = false;
            }
        }
        return success;
    }

    public static void removeFiles(List<Pair<Integer, String>> fileNames) {
        for (Pair<Integer, String> p : fileNames) {
            if (p.second != null) {
                removeFile(p.second);
            }
        }
    }

    public static void removeFile(String filename) {
        if (!StringUtils.isNullOrEmpty(filename)) {
            try {
                File file = new File(filename);
                file.delete();
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
            }
        }
    }

    public static void removeFile(String path, String filename) {
        if (!StringUtils.isNullOrEmpty(filename)) {
            try {
                File file = new File(path, filename);
                file.delete();
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
            }
        }
    }

    public static void removeDir(String dirPath) {
        File dir = new File(dirPath);
        removeDirOrFile(dir);
    }

    public static int getFileCount(File dir) {
        if (dir == null || !dir.exists())
            return 0;
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            int count = 0;
            for (File child : children) {
                count += getFileCount(child);
            }
            return count;
        } else {
            return 1;
        }
    }

    public static String BtoKBMB(long size) {
        if (size < 0) {
            size = 0;
        }
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else if (size < 1024 * 1024) {
            return String.valueOf(size / 1024) + "KB";
        } else {
            return String.format("%.1fMB", size * 1.0 / (1024 * 1024));
        }
    }

    public static String BtoKBMB(String sizeStr) {
        try {
            long size = Long.valueOf(sizeStr);
            return BtoKBMB(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }

    public static boolean removeDirOrFile(File file) {
        if (null == file) {
            return false;
        }
        if (file.isDirectory()) {
            String[] childList = file.list();
            if (childList != null && Array.getLength(childList) != 0) {
                for (String children : childList) {
                    boolean success = removeDirOrFile(new File(file, children));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    public static File getFileFromURL(String base, String url) {
        if (StringUtils.isNullOrEmpty(url)) {
            return null;
        }
        int idx = url.lastIndexOf('/');
        return new File(base, url.substring(idx + 1));
    }

    public static boolean isSDCardMounted() {
        return availableMemInSDcard();
    }

    public static boolean availableMemInSDcard() {
        if (!externalStorageExist()) {
            return false;
        }
        File sdcard = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(sdcard.getPath());
        long blockSize = statFs.getBlockSize();
        long avaliableBlocks = statFs.getAvailableBlocks();
        long total = avaliableBlocks * blockSize / 1024;
        return total >= 10;
    }

    public static boolean externalStorageExist() {
        return getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
    }

    public static String dropPrefix(String s, String prefix) {
        if (StringUtils.isNullOrEmpty(s)) {
            return "";
        }
        if (StringUtils.isNullOrEmpty(prefix)) {
            return s;
        }
        if (s.startsWith(prefix)) {
            if (prefix.length() > s.length()) {
                return "";
            }
            return s.substring(prefix.length());
        }
        return s;
    }

    /**
     * Safe concatenate paths no matter the first one ends with / or the second one starts with /.
     */
    public static String concatPath(String p1, String p2) {
        return p1.endsWith("/") ? p1 + dropPrefix(p2, "/") : p1 + "/" + dropPrefix(p2, "/");
    }

    public static String concatPaths(String... ss) {
        String path = "";
        for (String s : ss)
            path = concatPath(path, s);
        return path;
    }

    public static FileUtils openFile(String filePath) throws Exception {
        String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
        createDir(dirPath, true);

        File file = new File(filePath);
        if (!file.exists() && !file.createNewFile()) {
            file = null;
        }
        return new FileUtils(file, null);
    }

    private FileUtils(File file, FileOutputStream fileos) throws Exception {
        mFile = file;
        mFileOutputStream = fileos;
        if (mFile != null) {
            if (mFileOutputStream == null) {
                mFileOutputStream = new FileOutputStream(mFile);
            }
            mBufferedOutputStream = new BufferedOutputStream(mFileOutputStream);
        } else {
            throw new Exception("FileOutput, can not create file output stream");
        }
    }

    public static boolean createDir(String dirPath, boolean nomedia) {
        if (!ensureDirExists(dirPath)) {
            return false;
        }
        if (nomedia) {
            File nomediafile = new File(dirPath + "/.nomedia");
            try {
                nomediafile.createNewFile();
            } catch (IOException ignored) { }
        }
        return true;
    }

    public static boolean ensureDirExists(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            return dirFile.mkdirs();
        }
        return true;
    }

    /**
     * Ensure the parent directory of given file path exists.
     * make directories if need.
     *
     * @param filePath A file path.
     * @return True for success, false otherwise.
     */
    public static boolean ensureFileDirExists(String filePath) {
        String dir = getDirOfFilePath(filePath);
        if (StringUtils.isNullOrEmpty(dir)) {
            return false;
        }
        ensureDirExists(dir);
        return true;
    }

    public static String getDirOfFilePath(String filePath) {
        if (StringUtils.isNullOrEmpty(filePath)) {
            return null;
        }
        int sepPos = filePath.lastIndexOf(File.separatorChar);
        if (sepPos == -1) {
            return null;
        }
        return filePath.substring(0, sepPos);
    }

    public static File ensureFileExists(String fileName) {
        ensureFileDirExists(fileName);
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        }
        try {
            if (!file.exists() && !file.createNewFile()) {
                file = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "can not create file , path = " + fileName);
            file = null;
        }
        return file;
    }


    public void write(Bitmap bmp) {
        write(bmp, 80);
    }

    public void write(Bitmap bmp, int compressRate) {
        bmp.compress(Bitmap.CompressFormat.JPEG, compressRate, mBufferedOutputStream);
    }

    public void write(InputStream is) {
        int bytes = 0;
        byte[] buffer = new byte[4096];
        try {
            while ((bytes = is.read(buffer)) != -1) {
                mBufferedOutputStream.write(buffer, 0, bytes);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void write(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            write(fis);
            fis.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void write(byte[] buffer) {
        try {
            mBufferedOutputStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void write(byte[] buffer, int offset, int length) {
        try {
            mBufferedOutputStream.write(buffer, offset, length);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void close() {
        try {
            mBufferedOutputStream.flush();
            mBufferedOutputStream.close();
            mFileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public File getFile() {
        return mFile;
    }

    public static long getFileLength(String file) {
        File tmp = new File(file);
        if (null == tmp) {
            return 0;
        }
        return tmp.length();
    }

    public static long getFileSize(File file) {
        long size = 0;
        if (null == file || null == file.listFiles()) {
            return size;
        }
        File[] fileList = file.listFiles();
        for (File item : fileList) {
            if (item.isDirectory()) {
                size = size + getFileSize(item);
            } else {
                size = size + item.length();
            }
        }
        return size;
    }

    /**
     * Read file bytes and return.
     *
     * @param file Must not be null.
     * @return null if input is not a valid file.
     */
    public static byte[] fileToByteArray(File file) {
        if (!file.exists() || !file.canRead()) {
            return null;
        }

        try {
            return streamToBytes(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Convert input stream to byte array.
     *
     * @return null if failed.
     */
    public static byte[] streamToBytes(InputStream inputStream) {
        byte[] content = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);

        try {
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            content = baos.toByteArray();
            if (content.length == 0) {
                content = null;
            }

            baos.close();
            bis.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return content;
    }

    public static String storeVideoThumbBitmap(Bitmap bitmap, String id) {
        String fileName = id+".png";
        String videoThumbDir = FileStorage.getCacheSubDir(".videothumb");
        return storeImage(bitmap, concatPath(videoThumbDir, fileName));
    }

    public static String storeImage(Bitmap image, String filePath) {
        if (image == null) {
            return "";
        }
        String storeDirPath = getDirOfFilePath(filePath);
        createDir(storeDirPath, false);
        File pictureFile = new File(filePath);
        try {
            FileOutputStream outputStream = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            outputStream.close();
            return filePath;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error accessing file: " + e.getMessage());
        }
        return "";
    }

    /**
     * 使用给定密码解压指定的ZIP压缩文件到当前目录
     *
     * @param zip    指定的ZIP压缩文件
     * @param passwd ZIP文件的密码
     * @param dest   解压到的目录
     * @return 解压后文件数组
     * @throws IOException 压缩文件有损坏或者解压缩失败抛出
     */
    public static File[] unzip(String zip, String dest, String passwd) throws IOException {//ZipException {
        File zipFile = new File(zip);
        return unzip(zipFile, dest, passwd);
    }

    /**
     * 使用系统指定的ZIP解压文件到指定目录
     * <p/>
     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出
     *
     * @param zipFile 指定的ZIP压缩文件
     * @param dest    解压目录
     * @param passwd  ZIP文件的密码
     * @return 解压后文件数组
     * @throws IOException 有问题即抛此异常
     *                     warning: java自带的zip功能不支持密码，所以请勿使用带密码的压缩包，还有路径含有中文名也要注意 modify by wangsong 6/17/2016
     */
    public static File[] unzip(File zipFile, String dest, String passwd) throws IOException {
        if (StringUtils.isNullOrEmpty(dest)) {
            throw new IOException();
        }
        if (!dest.endsWith(File.separator)) {
            dest = dest + File.separator;
        }
        File destDir = new File(dest);
        if (!destDir.isDirectory() || !destDir.exists()) {//fix bug, 下面注释unzip方法里面的判断是错误的
            destDir.mkdir();
        }

        List<File> extractedFileList = new ArrayList<File>();
        ZipInputStream inZip = null;
        try {
            inZip = new ZipInputStream(new FileInputStream(zipFile.getPath()));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(destDir + File.separator + szName);
                    folder.mkdirs();
                } else {
                    FileOutputStream out = null;
                    try {
                        File file = new File(destDir + File.separator + szName);
                        file.createNewFile();
                        out = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = inZip.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                            out.flush();
                        }
                        out.close();
                        extractedFileList.add(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IOException(e);
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FileUtils", e.toString());
            throw new IOException(e);
        } finally {
            if (inZip != null) {
                inZip.close();
            }
        }
        File[] extractedFiles = new File[extractedFileList.size()];
        extractedFileList.toArray(extractedFiles);
        return extractedFiles;
    }

    /**
     * 使用系统指定的ZIP解压文件到指定目录
     * <p/>
     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出
     *
     * @param context   context
     * @param assetName asset下资源名
     * @param dest      解压目录
     * @return 解压后文件数组
     * @throws IOException 有问题即抛此异常
     *                     warning: java自带的zip功能不支持密码，所以请勿使用带密码的压缩包，还有路径含有中文名也要注意 modify by wangsong 6/17/2016
     */
    public static File[] unzip(Context context, String assetName, String dest) throws IOException {
        if (StringUtils.isNullOrEmpty(dest)) {
            throw new IOException();
        }
        if (!dest.endsWith(File.separator)) {
            dest = dest + File.separator;
        }
        File destDir = new File(dest);
        if (!destDir.isDirectory() || !destDir.exists()) {//fix bug, 下面注释unzip方法里面的判断是错误的
            destDir.mkdir();
        }
        InputStream inputStream = context.getAssets().open(assetName);
        ZipInputStream inZip = new ZipInputStream(inputStream);
        ZipEntry zipEntry;
        String szName = "";
        List<File> extractedFileList = new ArrayList<File>();
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(destDir + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(destDir + File.separator + szName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
                extractedFileList.add(file);
            }
        }
        inZip.close();
        File[] extractedFiles = new File[extractedFileList.size()];
        extractedFileList.toArray(extractedFiles);
        return extractedFiles;
    }

    private static final String[] mCs = new String[]{"/", "\\", "?", "*", ":", "<", ">", "|", "\""};

    public static boolean isFileNameCorrect(String fileName) {
        if (null == fileName) {
            return false;
        }

        fileName = fileName.trim();
        if (fileName.length() == 0) {
            return false;
        }

        //目前该方法只用于文件管理逻辑中，当名称包含"|"时，文件会创建失败 ，添加“|”的过滤
        //去掉|的限制
        for (String c : mCs) {
            if (fileName.contains(c)) {
                return false;
            }
        }

        if (containsSurrogateChar(fileName)) {
            return false;
        }

        return true;
    }

    /**
     * 把文件名中不合法的字符删掉
     */
    public static String fixFileName(final String fileName) {
        if (null == fileName) {
            return null;
        }
        String result = fileName.toString();


        for (String c : mCs) {
            result = result.replace(c, "");
        }

        if (containsSurrogateChar(result)) {
            result = removeSurrogateChars(result);
        }
        return result;
    }

    /**
     * 删除Unicode代理区字符
     * android java String 内部使用UTF-16编码,不能识别路径有这种字符的文件
     * 代理区代码:范围为 [0xD800, 0xDFFF] 用于表示UTF-16编码中Unicode编码大于等于0x10000范围的字符
     * 参考:http://zh.wikipedia.org/wiki/UTF-16
     */
    private static final char UNICODE_SURROGATE_START_CHAR = 0xD800;
    private static final char UNICODE_SURROGATE_END_CHAR = 0xDFFF;

    public static String removeSurrogateChars(String string) {
        if (StringUtils.isNullOrEmpty(string)) {
            return string;
        }
        int length = string.length();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (c < UNICODE_SURROGATE_START_CHAR || c > UNICODE_SURROGATE_END_CHAR) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    public static boolean containsSurrogateChar(String string) {
        if (StringUtils.isNullOrEmpty(string)) {
            return false;
        }
        int length = string.length();
        boolean hasSurrogateChar = false;
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (UNICODE_SURROGATE_START_CHAR <= c && c <= UNICODE_SURROGATE_END_CHAR) {
                hasSurrogateChar = true;
                break;
            }
        }
        return hasSurrogateChar;
    }

    public static Size getImageWidthHeight(String pathName) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            return new Size(options.outWidth, options.outHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Size(0, 0);
    }

    public static String saveNetFile(String urlPath, String outPath) {
        if (StringUtils.isHttpUrl(urlPath)) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                URL url = new URL(urlPath);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return "";

                inputStream = connection.getInputStream();
                fileOutputStream = new FileOutputStream(new File(outPath));
                byte[] buffer = new byte[2048];
                int count;
                while (-1 != (count = inputStream.read(buffer))) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
                inputStream.close();
                connection.disconnect();
                return outPath;
            } catch (Exception exception) {
                try {
                    if (null != fileOutputStream) {
                        fileOutputStream.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                    if (null != connection) {
                        connection.disconnect();
                    }
                } catch (Exception e) {}
            }
        } else {
            boolean bRet = copyFile(urlPath, outPath);
            if (bRet) {
                return outPath;
            }
        }
        return "";
    }
}
