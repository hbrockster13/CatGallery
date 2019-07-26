package com.example.catgallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.LoginFilter;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread
{
    public static final String TAG = "ThumbnailDownloader";
    public static final int MESSAGE_DOWNLOAD = 0;

    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloaderListener;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    public interface ThumbnailDownloadListener<T>
    {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloaderListener(ThumbnailDownloadListener<T> listener)
    {
        mThumbnailDownloaderListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler)
    {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    @Override
    protected void onLooperPrepared()
    {
        mRequestHandler = new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                if (message.what == MESSAGE_DOWNLOAD)
                {
                    T target = (T) message.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }

            }
        };
    }
    @Override
    public boolean quit()
    {
        mHasQuit = true;
        return super.quit();
    }
    public void queueThumbnail(T target, String url)
    {
        Log.i(TAG, "Got a URL: " + url);
        if (url == null)
        {
            mRequestMap.remove(target);
        }
        else
        {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }
    public void clearQueue()
    {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }
    private void handleRequest(final T target)
    {
        try
        {
            final String url = mRequestMap.get(target);

            if (url == null)
            {
                return;
            }

            byte[] bitmapBytes = new CatFetcher().getURLBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap has been created.");
            mResponseHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mRequestMap.get(target) != url || mHasQuit)
                    {
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloaderListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        }
        catch (IOException ieo)
        {
            Log.e(TAG, "Error downloading image", ieo);
        }
    }
}
