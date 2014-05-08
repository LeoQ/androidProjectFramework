package com.autotiming.csck.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AndroidEnv {
	public static boolean isDebuggable(Context context) {
		
		boolean bRet = false;
		
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_SHARED_LIBRARY_FILES);
			int flags = appInfo.flags;
    	    if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
    	        // development mode
    	    	bRet = true;
    	    } else {
    	        // release mode
    	    }
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return bRet;
	}
	
	/**
	 * 判断本APP是否处于栈顶
	 * @param context
	 * @return
	 */
	public static boolean isAppOnForeground(Context context) {
		
		ActivityManager mActivityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

		String mPackageName = context.getPackageName();
	    List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
	    if (tasksInfo.size() > 0) {
//	        Log.i("AndroidEnv", "top Activity = "
//	                + tasksInfo.get(0).topActivity.getPackageName());
	        // 应用程序位于堆栈的顶层
	        if (mPackageName.equals(tasksInfo.get(0).topActivity
	                .getPackageName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
