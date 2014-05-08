package com.autotiming.csck.netapi.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * 内存/SD卡双缓存
 * @author qiuli
 *
 */
public class BitmapDoubleCache implements ImageCache{
	private BitmapLruImageCache memoryCache;
	private DiskLruImageCache diskCache;
	
	public BitmapDoubleCache(Context context, int cacheSize,
			CompressFormat compressFormat, int quality) {
		memoryCache = new BitmapLruImageCache(cacheSize);
		diskCache = new DiskLruImageCache(context, compressFormat, quality);
	}

	@Override
	public Bitmap getBitmap(String url) {
		return memoryCache.getBitmap(url) != null ? memoryCache.getBitmap(url) : diskCache.getBitmap(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		memoryCache.putBitmap(url, bitmap);
		diskCache.putBitmap(url, bitmap);
	}

}
