package plugin.album.utils.downloader;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;

import plugin.album.utils.StringUtils;

public class DownLoadTask extends AsyncTask<String, Integer, String> {
    public static final int USER_CANCEL_CODE = 5164233;
    private static final String TAG = "DownLoadTask";

    private static final int TIMEOUT_CONNECT = 60000;
    private static final int TIMEOUT_READ = 60000;
    private static final int BufferSize = 4096;
    private int mReadTimeOut;
    private int mConnectTimeOut;

    private AsyncDownLoader.DownLoaderListener mListener;
    private File mFile;

    public DownLoadTask(File file, AsyncDownLoader.DownLoaderListener listener) {
        this(file, listener, TIMEOUT_READ, TIMEOUT_CONNECT);
    }

    public DownLoadTask(File file, AsyncDownLoader.DownLoaderListener listener, int readTimeOut, int connectTimeOut) {
        mListener = listener;
        mFile = file;
        if (!(mFile.getParentFile().exists())) {
            mFile.getParentFile().mkdirs();
        }
        mReadTimeOut = readTimeOut == 0 ? TIMEOUT_READ : readTimeOut;
        mConnectTimeOut = connectTimeOut == 0 ? TIMEOUT_CONNECT : connectTimeOut;
    }

    @Override
    protected String doInBackground(String... strings) {
        String urlPath = strings[0];
        if (StringUtils.isNullOrEmpty(urlPath)) {
            if (mListener != null)
                mListener.onFailed(0, mFile);
            return urlPath;
        }

        int errCode = -1;
        if (StringUtils.isHttpUrl(urlPath)) {

            HttpURLConnection connection = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(urlPath);
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(false);
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(mConnectTimeOut);
                connection.setReadTimeout(mReadTimeOut);

                connection.connect();

                errCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK != errCode) {
                    Log.e(TAG, "connect failed code " + errCode);
                    if (mListener != null) mListener.onFailed(errCode, mFile);
                    return urlPath;
                }

                final long totalLength;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    totalLength = connection.getContentLengthLong();
                } else {
                    totalLength = connection.getContentLength();
                }

                is = connection.getInputStream();
                fos = new FileOutputStream(mFile);

                byte buffer[] = new byte[BufferSize];
                int count;
                int written = 0;
                while (-1 != (count = is.read(buffer))) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        errCode = USER_CANCEL_CODE;
                        throw new Exception("down load canceled");
                    }
                    fos.write(buffer, 0, count);
                    if (0 < totalLength) {
                        written += count;
                        if (mListener != null)
                            mListener.onProgress(written, totalLength);
                    }
                }
                fos.close();
                is.close();
                if (mListener != null)
                    mListener.onSuccess(mFile);
            } catch (Exception e) {
                if (e instanceof InterruptedIOException) {
                    errCode = USER_CANCEL_CODE; // user cancel
                }
                try {
                    if (null != fos) {
                        fos.close();
                    }
                    if (null != is) {
                        is.close();
                    }
                } catch (IOException ioe) {
                }
                if (mListener != null)
                    mListener.onFailed(errCode, mFile);
                return urlPath;
            } finally {
                if (null != connection) {
                    connection.disconnect();
                }
            }
        } else {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                File inFile = new File(urlPath);
                final long totalLength = inFile.length();
                fis = new FileInputStream(inFile);
                fos = new FileOutputStream(mFile);
                byte buffer[] = new byte[BufferSize];
                int count;
                long written = 0;
                while ((count = fis.read(buffer)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        errCode = USER_CANCEL_CODE;
                        throw new Exception("down load canceled");
                    }
                    fos.write(buffer, 0, count);
                    if (0 < totalLength) {
                        written += count;
                        if (mListener != null)
                            mListener.onProgress(written, totalLength);
                    }
                }
                fis.close();
                fos.close();
            } catch (Exception e) {
                if (e instanceof InterruptedIOException) {
                    errCode = USER_CANCEL_CODE; // user cancel
                }
                try {
                    if (null != fis) {
                        fis.close();
                    }
                    if (null != fos) {
                        fos.close();
                    }
                } catch (IOException ioe) {
                }
                if (mListener != null)
                    mListener.onFailed(errCode, mFile);
                return urlPath;
            }
            if (mListener != null)
                mListener.onSuccess(mFile);
        }
        return urlPath;
    }
}
