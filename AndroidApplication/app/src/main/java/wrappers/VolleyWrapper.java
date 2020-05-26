package wrappers;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyWrapper extends Application {

    private static VolleyWrapper mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static synchronized VolleyWrapper getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.getRequestQueue(), new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }

        return mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }
}
