package com.autotiming.csck.share.wxapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.autotiming.csck.R;
import com.autotiming.csck.activity.AppApplication;
import com.autotiming.csck.utils.NetworkUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeixinManager{
	private IWXAPI wxapi;
	private static final String API_ID = "wxc784ee30e33f3eb0";
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
//	private static final int THUMB_SIZE = 150;
//	private AsyncTask<String, Long, Boolean> task;
	
	
	protected WeixinManager() {
		wxapi = WXAPIFactory.createWXAPI(AppApplication.getAppContext(), API_ID);
		wxapi.registerApp(API_ID); //注册应用到微信
	}
	
	private static class SingletonHolder{
		private static WeixinManager instance = new WeixinManager();
	}
	
	public static WeixinManager getInstance(){
		return SingletonHolder.instance;
	}

	/**
	 * 分享到微信朋友圈
	 * @param title 标题
	 * @param des  描述信息
	 * @param shareUrl  分享链接
	 * @param bitmap  封面图
	 * @param type  多媒体消息类型  1:网页， 2:音乐，3:视频
	 * @param context
	 */
	public void shareToWXTimeline(String title, String des, String shareUrl, Bitmap bitmap, int type, Context context){
		
		if(!initWX(type, context)) return;
		
		WXEntryActivity.actionWXEntryActivity(context, type, bitmap, shareUrl, title, des, WeiXinConstants.WXSceneTimeline);
	}
	
	/**
	* 分享到微信朋友圈
	 * @param title 标题
	 * @param des  描述信息
	 * @param shareUrl  分享链接
	 * @param bitmapUrl  封面图链接
	 * @param type  多媒体消息类型  1:网页， 2:音乐，3:视频
	 * @param context
	 */
	public void shareToWXTimeline(String title, String des, String shareUrl, String bitmapUrl, int type, Context context){
		
		if(!initWX(type, context)) return;
		
		WXEntryActivity.actionWXEntryActivity(context, type, bitmapUrl, shareUrl, title, des, WeiXinConstants.WXSceneTimeline);
	}
	
	/**
	 * 分享到微信朋友
	 * @param title 标题
	 * @param des  描述信息
	 * @param shareUrl  分享链接
	 * @param bitmap  封面图
	 * @param type  多媒体消息类型  1:网页， 2:音乐，3:视频
	 * @param context
	 */
	public void shareToWXSession(String title, String des, String shareUrl, Bitmap bitmap, int type, Context context){
		
		if(!initWX(type, context)) return;
		
		WXEntryActivity.actionWXEntryActivity(context, type, bitmap, shareUrl, title, des, WeiXinConstants.WXSceneSession);
	}
	
	/**
	* 分享到微信朋友
	 * @param title 标题
	 * @param des  描述信息
	 * @param shareUrl  分享链接
	 * @param bitmapUrl  封面图链接
	 * @param type  多媒体消息类型  1:网页， 2:音乐，3:视频
	 * @param context
	 */
	public void shareToWXSession(String title, String des, String shareUrl, String bitmapUrl, int type, Context context){
		
		if(!initWX(type, context)) return;
		
		WXEntryActivity.actionWXEntryActivity(context, type, bitmapUrl, shareUrl, title, des, WeiXinConstants.WXSceneSession);
	}
	
	private boolean initWX(int type, Context context) {
		if (!NetworkUtil.isNetworkAvailable(AppApplication.getAppContext())) {// 网络不通
			Toast.makeText(AppApplication.getAppContext(),
					"网络不可用",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(!wxapi.isWXAppInstalled()){
			Toast.makeText(AppApplication.getAppContext(), "未安装微信", Toast.LENGTH_SHORT).show();
			Intent intent= new Intent(Intent.ACTION_VIEW);        
		    Uri content_url = Uri.parse("http://weixin.qq.com");   
		    intent.setData(content_url);  
		    try {
				context.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		if(type == WeiXinConstants.WXSceneTimeline){
			if(wxapi.getWXAppSupportAPI() < TIMELINE_SUPPORTED_VERSION){
				Toast.makeText(AppApplication.getAppContext(), "版本太低不支持分享到朋友圈！", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		return true;
	}

	public boolean isWXAppInstalled(){
		return wxapi.isWXAppInstalled();
	}
	
}
