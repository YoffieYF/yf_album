package plugin.album.utils.downloader;

import java.io.File;

public class AsyncDownLoader {
    public static DownLoadTask downLoad(String url, File file, DownLoaderListener listener) {
        DownLoadTask download = new DownLoadTask(file, listener);
        download.executeOnExecutor(DownLoadTask.THREAD_POOL_EXECUTOR, url);
        return download;
    }

    public static DownLoadTask downLoad(String url, File file, DownLoaderListener listener, int readTimeOut, int connectTimeOut) {
        DownLoadTask download = new DownLoadTask(file, listener, readTimeOut, connectTimeOut);
        download.executeOnExecutor(DownLoadTask.THREAD_POOL_EXECUTOR, url);
        return download;
    }

    public interface DownLoaderListener {
        void onSuccess(File file);

        void onFailed(int code, File file);

        void onProgress(long current, long total);
    }
}
