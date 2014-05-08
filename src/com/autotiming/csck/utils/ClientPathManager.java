package com.autotiming.csck.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.autotiming.csck.activity.AppApplication;

/**
 * 客户端路径管理类
 * @author qiuli
 *
 */
public class ClientPathManager {
	
	private static final String TAG = ClientPathManager.class.getSimpleName();
	
	/**
	 * 根目录,如/mnt/sdcard
	 */
	private static String rootPath;
	
	private static boolean useInternalStore = false;
	
	private static String storageVolumeState;
	
	static{
		resetRootPath();
	}
	
	private ClientPathManager(){
		resetRootPath();
	}
	
	static public void resetRootPath(){
		try {
			init();
			initDiretory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static private void init() {
		//1、使用系统api判断是否有外部存储，这里的外部存储理解为“可插拔的sdcard”和“不可插拔的并【可移除】的内置存储”；
		rootPath = null;
		if(existExternalStroage()){
			storageVolumeState = Environment.MEDIA_MOUNTED;
			rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		//2、部分手机通过系统的api无法获取外部存储,针对Android4.0的系统可通过反射【尝试获取】。
		try {
			do{
				String path = null;
				String state = null;
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (TextUtils.isEmpty(rootPath)
						// 前提是Android4.0以上
						&& currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					StorageManager storageMan = null;
					if(AppApplication.app != null){
						storageMan = (StorageManager) AppApplication.app
								.getSystemService(Context.STORAGE_SERVICE);
					}
					if (storageMan == null) {
						break;
					}
				
					Object[] r = (Object[]) ReflectionHelper.invokeMethod(storageMan, "getVolumeList", null);
					
					//SDcard
					StorageVolumeWrapper storageVolumeWrapperSD = null;
					//手机内存
					StorageVolumeWrapper storageVolumeWrapperInner = null;
					
					int count = r.length;
					for(int i=0;i<count;i++) {
						StorageVolumeWrapper storageVolumeWrapper = new StorageVolumeWrapper(r[i]);
						if(storageVolumeWrapper.isRemovable()) {
							storageVolumeWrapperSD = storageVolumeWrapper;
							if(storageVolumeWrapperSD!=null){
								path = storageVolumeWrapperSD.getPath();
								System.out.println("removeable storageVolume path : "+path);
								Object[] arg = new Object[]{path};
								state = (String)ReflectionHelper.invokeMethod(storageMan, "getVolumeState", arg);
								System.out.println("removeable storageVolume path state : "+state.toString());
								if(state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
									storageVolumeState = Environment.MEDIA_MOUNTED;
								}else if(state.equalsIgnoreCase(Environment.MEDIA_SHARED)){
									storageVolumeState = Environment.MEDIA_SHARED;
								}else if(state.equalsIgnoreCase(Environment.MEDIA_REMOVED)){
									storageVolumeState = Environment.MEDIA_REMOVED;
								}
								if(path != null && state != null && state.equals(Environment.MEDIA_MOUNTED) ) {
									rootPath = path;
									//如有多个存储，只获取第一个
									break;
								}
							}
						}
						else {
							storageVolumeWrapperInner = storageVolumeWrapper;
						}
					}
					
					if (storageVolumeWrapperInner != null) {
						path = storageVolumeWrapperInner.getPath();
						System.out.println("unremoveable storageVolume path : "+path);
						Object[] argInner = new Object[] { path };
						state = (String) ReflectionHelper.invokeMethod(storageMan,
								"getVolumeState", argInner);
						System.out.println("unremoveable storageVolume path : "+state.toString());
						if(state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
							storageVolumeState = Environment.MEDIA_MOUNTED;
						}else if(state.equalsIgnoreCase(Environment.MEDIA_SHARED)){
							storageVolumeState = Environment.MEDIA_SHARED;
						}else if(state.equalsIgnoreCase(Environment.MEDIA_REMOVED)){
							storageVolumeState = Environment.MEDIA_REMOVED;
						}
						if (path != null && state != null
								&& state.equals(Environment.MEDIA_MOUNTED)) {
							rootPath = path;
						}
					}
					
				}
			}while (false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//3、externalStorageDirectory如果还是为空，那么将使用内存/data/data/xxx/cache作为根目录;
		if(TextUtils.isEmpty(rootPath)){
			if(AppApplication.app !=  null){
				rootPath = AppApplication.app.getCacheDir().getAbsolutePath();
			}
			storageVolumeState = Environment.MEDIA_REMOVED;
			useInternalStore = true;
		}
		if(TextUtils.isEmpty(storageVolumeState)){
			storageVolumeState = Environment.MEDIA_REMOVED;
		}
		Log.i(TAG,"storageVolumeState : "+storageVolumeState);
		Log.i(TAG,"RootPath : "+getRootPath());
	}
	
	/**
	 * 
	 * @return 根目录，如‘/mnt/sdcard’或“/mnt/storage”或“/data/data/xxx/cache”
	 */
	static public String getRootPath() {
		if(rootPath == null){
			Log.e(TAG, "rootPath is null ");
		}
		return rootPath;
	}
	
	/**
	 * 
	 * @return 程序运行时文件存储的主目录
	 */
	static public String getMainPath(){
		return getRootPath()+"/autotiming/csck/";
	}
	
	
	public static String getFinishedPath(){
		return getMainPath()+"file/download/finished/";
	}
	
	public static String getUnfinishedPath(){
		return getMainPath()+"file/download/unfinished/";
	}
	
	public static String getUplaodFinishedPath(){
		return getMainPath()+"file/upload/finished/";
	}
	
	public static String getUplaodUnfinishedPath(){
		return getMainPath()+"file/upload/unfinished/";
	}
	
	public static String getMusicCachePath(){
		return getMainPath()+"file/cache/music/";
	}
	
	public static String getAudioCachePath(){
		return getMainPath() + "file/cache/audio/";
	}
	
	public static String getSplashCachePath(){
		return getMainPath()+"file/splashs/";
	}
	
	public static String getCamImgCachePath(){
		return getMainPath()+"file/camera/";
	}
	
	public static String getImgCachePath(){
		return getMainPath()+"imageCache/";
	}
	
	public static String getImageSavePath(){
		return getMainPath()+"file/saveImage/";
	}
	
	public static String getContactsCachePath(){
		return getMainPath()+"file/contacts/";
	}
	
	public static String getLogPath(){
		return getMainPath()+"file/logs/";
	}
	
	public static String getReportPath(){
		return getMainPath() + "file/reports/";
	}
	
	static private boolean existExternalStroage() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 创建目录
	 */
	static private void initDiretory() {
		File file = new File(getMainPath());
		if(!file.exists()){
			file.mkdirs();
		}
		
	}
	
	/**
	 * 判断是否使用内存存储/data/data/xxx/cache作为程序运行的主目录
	 * @return
	 */
	public boolean isUseInternalStore() {
		return useInternalStore;
	}
	
	public String getStorageVolumeState() {
		return storageVolumeState;
	}

	public void setStorageVolumeState(String storageVolumeState) {
		this.storageVolumeState = storageVolumeState;
		resetRootPath();
	}
}
