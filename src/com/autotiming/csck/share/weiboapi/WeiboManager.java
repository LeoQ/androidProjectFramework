package com.autotiming.csck.share.weiboapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.LogoutAPI;

public class WeiboManager {

	/**
	 * 判断微博授权信息是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSessionValid(Context context) {
		Oauth2AccessToken mAccessToken = AccessTokenKeeper
				.readAccessToken(context);
		return mAccessToken.isSessionValid();
	}

	/**
	 * 微博授权
	 * @param context
	 */
	public static void loginWeibo(Context context) {
		WBAuthActivity.actionWBAuthActivity(context);
	}
	
	/**
	 * 取消授权
	 * @param context
	 */
	public static boolean logoutWeibo(Context context){
		Oauth2AccessToken mAccessToken = AccessTokenKeeper
				.readAccessToken(context);
		 new LogoutAPI(mAccessToken).logout(null);
		 AccessTokenKeeper.clear(context);
		 return true;
	}
	
	/**
	 * 分享文本
	 * @param context
	 * @param text
	 */
	public static void shareTextMsg(Context context, String text){
		WBShareActivity.actionWBShareActivity(context, WeiboConstants.IMAGE_WEIBO_TYPE, text, null, null, null);
	}
	
	/**
	 * 分享 带图片的微博
	 * @param context
	 * @param content
	 * @param bitmap
	 */
	public static void shareImageMsg(Context context ,String content, Bitmap bitmap){
		WBShareActivity.actionWBShareActivity(context, WeiboConstants.IMAGE_WEIBO_TYPE, content, bitmap, null, null, null);
	}
	
	/**
	 * 分享 带图片的微博
	 * @param context
	 * @param content
	 * @param picUrl 图片链接
	 */
	public static void shareImageMsg(Context context ,String content, String picUrl){
		WBShareActivity.actionWBShareActivity(context, WeiboConstants.IMAGE_WEIBO_TYPE, content, picUrl, null, null, null);
	}
	
	/**
	 * 多媒体微博
	 * @param context
	 * @param type 多媒体消息类型  1:网页， 2:音乐，3:视频
	 * @param content 分享内容（微博内容）
	 * @param title 多媒体消息标题
	 * @param des	多媒体消息描述信息
	 * @param url	多媒体消息url
	 * @param bitmap 封面图
	 */
	public static void shareMediaMsg(Context context, int type, String content, String title, String des, String url, Bitmap bitmap){
		WBShareActivity.actionWBShareActivity(context, type, content, bitmap, url, title, des);
	}
	
	/**
	 * 
	 * @param context
	 * @param type 多媒体消息类型  1:网页， 2:音乐，3:视频
	 * @param content 分享内容（微博内容）
	 * @param title 多媒体消息标题
	 * @param des	多媒体消息描述信息
	 * @param url	多媒体消息url
	 * @param bitmapUrl 图片链接
	 */
	public static void shareMediaMsg(Context context, int type, String content, String title, String des, String url, String bitmapUrl){
		WBShareActivity.actionWBShareActivity(context, type, content, bitmapUrl, url, title, des);
	}
}
