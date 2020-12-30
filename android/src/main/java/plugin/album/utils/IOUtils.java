package plugin.album.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by legend on 15/5/20.
 */
public class IOUtils {

    public static byte[] ungzip(byte[] data) throws IOException {
        GZIPInputStream zis = new GZIPInputStream(
                new ByteArrayInputStream(data));
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[1024];
        while (true) {
            int len = zis.read(buf, 0, buf.length);
            if (len == -1) {
                break;
            }
            os.write(buf, 0, len);
        }
        zis.close();
        return os.toByteArray();
    }

    // return null if no exist or is not dir
    public static File getDir(File rootDir, String... dirs) {
        File currentDir = rootDir;
        for (String dir : dirs) {
            currentDir = new File(currentDir, dir);
            if (!currentDir.exists() || !currentDir.isDirectory()) {
                return null;
            }
        }
        return currentDir;
    }

    // return null if no exist or is dir
    public static File getFile(File rootDir, String fileName, String... dirs) {
        File parentDir = getDir(rootDir, dirs);
        if (parentDir == null) {
            return null;
        }
        File file = new File(parentDir, fileName);
        return file.exists() && !file.isDirectory() ? file : null;
    }

    public static File createDirIfNoExist(File rootDir, String... dirs) {
        File currentDir = rootDir;
        for (String dir : dirs) {
            currentDir = new File(currentDir, dir);
            if ((currentDir.exists() && !currentDir.isDirectory())
                    || (!currentDir.exists() && !currentDir.mkdir())) {
                return null;
            }
        }
        return currentDir;
    }

    public static File createFileIfNoExist(File rootDir, String fileName, String... dirs) {
        File parentDir = createDirIfNoExist(rootDir, dirs);
        if (parentDir == null) {
            return null;
        }
        File file = new File(parentDir, fileName);
        if (file.isDirectory()) {
            return null;
        }
        try {
            return file.createNewFile() ? file : null;
        } catch (IOException e) {
            return null;
        }
    }

    public static long getFileSize(File file) {
        if (null == file) {
            return 0;
        }
        if (file.isDirectory()) {
            File fileList[] = file.listFiles();
            if (fileList == null) {
                return 0;
            }
            long size = 0;
            for (File item : fileList) {
                size += getFileSize(item);
            }
            return size;
        }
        return file.length();
    }

    public static boolean removeFile(File file, boolean deleteOwn) {
        if (file == null) {
            return false;
        }
        boolean success = true;
        if (file.isDirectory()) {
            File fileList[] = file.listFiles();
            if (fileList != null) {
                for (File item : fileList) {
                    success &= removeFile(item, true);
                }
            }
        }
        return success && (!deleteOwn || file.delete());
    }

    public static byte[] readBytes(File rootDir, String fileName, String... dirs) {
        return readBytes(getFile(rootDir, fileName, dirs));
    }

    public static String readString(File file) {
        return new String(readBytes(file));
    }

    public static byte[] readBytes(File file) {
        if (file == null) {
            return new byte[0];
        }
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            byte[] data = outputStream.toByteArray();
            return data;
        } catch (IOException e) {
            return new byte[0];
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static boolean writeBytes(File rootDir, String fileName, byte[] data
            , String... dirs) {
        return writeBytes(createFileIfNoExist(rootDir, fileName, dirs), data);
    }

    public static boolean writeBytes(File file, byte[] data) {
        if (file == null) {
            return false;
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

