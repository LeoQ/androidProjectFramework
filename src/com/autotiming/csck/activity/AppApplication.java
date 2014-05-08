package com.autotiming.csck.activity;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;

import com.autotiming.csck.netapi.RequestManager;
import com.autotiming.csck.netapi.image.ImageCacheManager;
import com.autotiming.csck.netapi.image.ImageCacheManager.CacheType;
import com.autotiming.csck.utils.DLog;
import com.autotiming.csck.utils.ErrorEx;

public class AppApplication  extends Application{
	public static final String TAG = AppApplication.class.getSimpleName();
	
	private static Context context;
	public static AppApplication app;
	private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
	
	/**
	 * 获取应用程序context
	 * */
	public static Context getAppContext()
	{
		return context;
	}
	
	@Override
	public void onCreate() {

		AppApplication.context = getApplicationContext();
		DLog.d(TAG, "App Application onCreate!");
		app = this;
       
		//初始化错误处理工具类
		ErrorEx.getInstance().init(this);
		
		//初始化网络请求
		RequestManager.init(this);
		createImageCache();
	}
	
	/**
	 * 初始化图片缓存容器
	 */
	private void createImageCache(){
		ImageCacheManager.getInstance().init(this,
				DISK_IMAGECACHE_SIZE
				, DISK_IMAGECACHE_COMPRESS_FORMAT
				, DISK_IMAGECACHE_QUALITY
				, CacheType.MEMORY_DISK);
	}

	@Override
	public void onTerminate()
	{
		DLog.d("AppApplication", "onTerminate!!!");
	}
	
	public static void showTost(String text){
		Toast.makeText(context, text, 0).show(); 
	}
	
	public void setInternalActivityParam(String key, Object object) {
		mActivityParamsMap.put(key, object);
	}

	public Object receiveInternalActivityParam(String key) {
		return mActivityParamsMap.remove(key);
	}
	
	public void cleanParamMap(){
		mActivityParamsMap.clear();
	}

	private HashMap<String, Object> mActivityParamsMap = new HashMap<String, Object>();
	
}
