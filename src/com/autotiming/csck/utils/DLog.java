package com.autotiming.csck.utils;

import com.autotiming.csck.BuildConfig;

/**
 * 重写LOG，用于控制统一打开/关闭日志功能
 * */
public class DLog {
	
	private static boolean logIsInit = false;
	
	private static boolean LOG = true;
	
	private static void initLog(){
		if(!logIsInit){
			LOG = BuildConfig.DEBUG;
			logIsInit =true;
		}
	}
	
	public static void i(String tag,String string){
		initLog();
		if(tag == null)tag="tagnull";
		if(string == null)string ="stringnull";
		if(LOG)android.util.Log.i(tag, string); 
	}
	
	public static void e(String tag,String string){
		
		initLog();
		if(tag == null)tag="tagnull";
		if(string == null)string ="stringnull";
		if(LOG)android.util.Log.e(tag, string); 
		//记录log信息，以提交服务器
//		LogItem logItem = new LogItem();
//		logItem.actionCode = Log_info.LOG_E.code();
//		logItem.msg = "tag:"+tag+"|msg:"+string;
//		logItem.webappId = "";
//		logItem.webappVersionCode = 0L;
//		LogerDAO.getInstance().InsertLog(logItem);
		
		//发生错误，启动提交服务
//		ClientUtil.startLogerService();
	}
	
	public static void b(String tag,String string){
		initLog();
		if(tag == null)tag="tagnull";
		if(string == null)string ="stringnull";
		if(LOG)android.util.Log.i(tag, string); 
		//记录log信息，以提交服务器
//		LogItem logItem = new LogItem();
//		logItem.actionCode = Log_info.LOG_I.code();
//		logItem.msg = "tag:"+tag+"|msg:"+string;
//		logItem.webappId = "";
//		logItem.webappVersionCode = 0L;
//		LogerDAO.getInstance().InsertLog(logItem);
	}
	
	public static void d(String tag,String string){
		initLog();
		if(tag == null)tag="tagnull";
		if(string == null)string ="stringnull";
		if(LOG)android.util.Log.d(tag, string); 
	}
	
	public static void v(String tag,String string){
		initLog();
		if(tag == null)tag="tagnull";
		if(string == null)string ="stringnull";
		if(LOG)android.util.Log.v(tag, string); 
	}
	
	public static void w(String tag,String string){
		initLog();
		if(tag == null)tag="tagnull";
		if(string == null)string ="stringnull";
		if(LOG)android.util.Log.w(tag, string); 
	}


}
