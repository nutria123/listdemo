package list.pack;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ImageCache {
	private static HashMap<String, SoftReference<Drawable>> imageCache;
	private ExecutorService executorService = Executors.newFixedThreadPool(15);   
	public ImageCache() {
		if (imageCache == null) {
			//Log.i(getClass().getName(), "缓存中cache不存在");
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		} else {
			//Log.i(getClass().getName(), "缓存中cache已存在");
		}
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};
		if (imageCache.containsKey(imageUrl)) {
			//Log.i(getClass().getName(), "缓存中已存在 imageUrl=" + imageUrl);
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
				return drawable;
			}
		}
		executorService.submit(new Runnable() {
            public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);         	
            }});
//		new Thread() {
//			@Override
//			public void run() {
//				Drawable drawable = loadImageFromUrl(imageUrl);
//				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
//				Message message = handler.obtainMessage(0, drawable);
//				handler.sendMessage(message);
//			}
//		}.start();
		return null;
	}

	public Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			Log.i(getClass().getName(), "new thread url=" + url);
			HttpURLConnection conn = (HttpURLConnection) m.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6 * 1000);

			i = conn.getInputStream();// (InputStream) m.getContent();
		} catch (Exception ex) {
			Log.e(getClass().getName(), ex.getMessage(), ex);
		}

		Drawable d = Drawable.createFromStream(i, "src");
		return d;

	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}
}