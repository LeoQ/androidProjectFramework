/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.autotiming.csck.share.weiboapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.autotiming.csck.R;
import com.autotiming.csck.activity.AppApplication;
import com.autotiming.csck.utils.NetworkUtil;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.utils.Utility;

/**
 * 该类演示了第三方应用如何通过微博客户端分享文字、图片、视频、音乐等。
 * 执行流程： 从本应用->微博->本应用
 * 
 */
public class WBShareActivity extends Activity implements IWeiboHandler.Response {
    private static final String TAG = "WBShareActivity";
    
    /** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
    
    private static Bitmap mBitmap;
    private String title;
    private String description;
    private String shareUrl;
    private int type;
    private String content;
    private String bitmapUrl;
    /**
   	 * 分享页面
   	 * @param context 
   	 */
   	public static void actionWBShareActivity(Context context, int type, String content, Bitmap bitmap, String url
   			, String title, String des) {
   		Intent intent = new Intent(context , WBShareActivity.class);
   		intent.putExtra("type", type);
   		intent.putExtra("content", content);
   		intent.putExtra("url", url);
   		intent.putExtra("title", title);
   		intent.putExtra("des", des);
   		mBitmap = bitmap;
   		try {
   			context.startActivity(intent);
   		} catch (Exception e) {
   			e.printStackTrace();
   			Toast.makeText(context, R.string.weibosdk_toast_share_failed, Toast.LENGTH_SHORT).show();
   		}
   	}
   	
	public static void actionWBShareActivity(Context context, int type, String content, String bitmapUrl, String url
   			, String title, String des) {
   		Intent intent = new Intent(context , WBShareActivity.class);
   		intent.putExtra("type", type);
   		intent.putExtra("content", content);
   		intent.putExtra("url", url);
   		intent.putExtra("title", title);
   		intent.putExtra("des", des);
   		intent.putExtra("bitmapUrl", bitmapUrl);
   		try {
   			context.startActivity(intent);
   		} catch (Exception e) {
   			e.printStackTrace();
   			Toast.makeText(context, R.string.weibosdk_toast_share_failed, Toast.LENGTH_SHORT).show();
   		}
   	}
	
	public static void actionWBShareActivity(Context context, int type, String content,  String url
   			, String title, String des) {
   		Intent intent = new Intent(context , WBShareActivity.class);
   		intent.putExtra("type", type);
   		intent.putExtra("content", content);
   		intent.putExtra("url", url);
   		intent.putExtra("title", title);
   		intent.putExtra("des", des);
   		try {
   			context.startActivity(intent);
   		} catch (Exception e) {
   			e.printStackTrace();
   			Toast.makeText(context, R.string.weibosdk_toast_share_failed, Toast.LENGTH_SHORT).show();
   		}
   	}

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        initDatas();

        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, WeiboConstants.APP_KEY);
        
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        ;
        Log.d(TAG, "registerApp:" + mWeiboShareAPI.registerApp());
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
        	boolean isResp = mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
            Log.d(TAG, "can invoke OnResponse:" + isResp);
        }
        
        // 如果未安装微博客户端，使用api分享微博
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
            shareToSina(content, shareUrl, mBitmap);
        }else {
			//使用SDK调微博客户端分享微博
        	if((mBitmap != null) || (mBitmap == null && bitmapUrl == null)){
        		sendMessage();
        	}else {
        		new AsyncTask<String, Void, Bitmap>(){
        			ProgressDialog progress;
        			
        			protected void onPreExecute() {
        				progress = new ProgressDialog(WBShareActivity.this);
        				progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        				progress.setMessage("请稍后...");
        				progress.setCancelable(true);
        				progress.setIndeterminate(true);
        				progress.show();
        			};
        			
        			@Override
        			protected Bitmap doInBackground(String... params) {
//        				return ImageLoader.instance().getBitmap(params[0]);
        				return null;
        			}
        			
        			protected void onPostExecute(Bitmap result) {
        				try {
        					mBitmap = result;
        					if(progress != null && progress.isShowing()){
        						progress.dismiss();
        					}
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        				sendMessage();
        			};
        			
        		}.execute(bitmapUrl);
			}
		}
        
        /*new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				finish();
			}
		}, 10000);*/
    }
    
    private void initDatas() {
    	type = getIntent().getIntExtra("type", 0);
    	title = getIntent().getStringExtra("title");
    	description = getIntent().getStringExtra("des");
    	shareUrl = getIntent().getStringExtra("url");
    	content = getIntent().getStringExtra("content");
    	bitmapUrl = getIntent().getStringExtra("bitmapUrl");
	}

	/**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        boolean isResp = mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        Log.d(TAG, "onNewIntent can invoke OnResponse:" + isResp);
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * 
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
    	Log.d(TAG, "onResponse:" + baseResp.errMsg);
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(this, R.string.weibosdk_toast_share_success, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, R.string.weibosdk_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, 
                    getString(R.string.weibosdk_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
        
        finish();
    }
    
    @Override
    protected void onDestroy() {
    	Log.d(TAG, "onDestroy");
    	super.onDestroy();
    	if(mBitmap != null && !mBitmap.isRecycled()){
    		mBitmap.recycle();
    	}
    	mBitmap = null;
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage() {
        
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                sendMultiMessage();
            } else {
                sendSingleMessage();
            }
        } else {
            Toast.makeText(this, R.string.weibosdk_not_support_api_hint, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    private void sendMultiMessage() {
        
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj();
        weiboMessage.imageObject = getImageObj();
       
        switch (type) {
		case WeiboConstants.IMAGE_WEIBO_TYPE:
			weiboMessage.imageObject = getImageObj();
			break;
		case WeiboConstants.WEBPAGE_WEIBO_TYPE:
			weiboMessage.mediaObject = getWebpageObj();
			break;
		case WeiboConstants.MUSIC_WEIBO_TYPE:
			weiboMessage.mediaObject = getMusicObj();
			break;
		case WeiboConstants.VIDEO_WEIBO_TYPE:
			weiboMessage.mediaObject = getVideoObj();
			break;
		default:
			break;
		}
        
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    private void sendSingleMessage() {
        
        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        
        switch (type) {
		case WeiboConstants.IMAGE_WEIBO_TYPE:
			weiboMessage.mediaObject = getImageObj();
			break;
		case WeiboConstants.WEBPAGE_WEIBO_TYPE:
			weiboMessage.mediaObject = getWebpageObj();
			break;
		case WeiboConstants.MUSIC_WEIBO_TYPE:
			weiboMessage.mediaObject = getMusicObj();
			break;
		case WeiboConstants.VIDEO_WEIBO_TYPE:
			weiboMessage.mediaObject = getVideoObj();
			break;
		default:
			break;
		}
        
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }

    /**
     * 获取分享的文本模板。
     * 
     * @return 分享的文本模板
     */
    private String getSharedText() {
        return content;
    }

    /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        if (mBitmap != null) {
        	imageObject.setImageObject(mBitmap);
		}else {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher);
			imageObject.setImageObject(bitmapDrawable.getBitmap());
		}
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     * 
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        
        // 设置 Bitmap 类型的图片到视频对象里
        if (mBitmap != null) {
        	mediaObject.setThumbImage(mBitmap);
		}else {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher);
			mediaObject.setThumbImage(bitmapDrawable.getBitmap());
		}
        mediaObject.actionUrl = shareUrl;
        mediaObject.defaultText = description;
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     * 
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj() {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = title;
        musicObject.description = description;
        
        // 设置 Bitmap 类型的图片到视频对象里
        if (mBitmap != null) {
        	musicObject.setThumbImage(mBitmap);
		}else {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher);
			musicObject.setThumbImage(bitmapDrawable.getBitmap());
		}
        musicObject.actionUrl = shareUrl;
        musicObject.defaultText = description;
        musicObject.dataUrl = "www.autotiming.com";
        musicObject.dataHdUrl = "www.autotiming.com";
        musicObject.duration = 10;

        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     * 
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj() {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = title;
        videoObject.description = description;
        
        // 设置 Bitmap 类型的图片到视频对象里
        if (mBitmap != null) {
        	videoObject.setThumbImage(mBitmap);
		}else {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher);
			videoObject.setThumbImage(bitmapDrawable.getBitmap());
		}
        videoObject.actionUrl = shareUrl;
        videoObject.defaultText = description;
        videoObject.dataUrl = "www.autotiming.com";
        videoObject.dataHdUrl = "www.autotiming.com";
        videoObject.duration = 10;
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     * 
     * @return 多媒体（音乐）消息对象。
     */
    private VoiceObject getVoiceObj() {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
        voiceObject.identify = Utility.generateGUID();
//        voiceObject.title = mShareVoiceView.getTitle();
//        voiceObject.description = mShareVoiceView.getShareDesc();
//        
//        // 设置 Bitmap 类型的图片到视频对象里
//        voiceObject.setThumbImage(mShareVoiceView.getThumbBitmap());
//        voiceObject.actionUrl = mShareVoiceView.getShareUrl();
//        voiceObject.dataUrl = "www.weibo.com";
//        voiceObject.dataHdUrl = "www.weibo.com";
//        voiceObject.duration = 10;
//        voiceObject.defaultText = "Voice 默认文案";
        return voiceObject;
    }
    
    /**
	 * 分享到微博
	 * @param shareContent
	 * @param shareUrl
	 * @param imgUrl
	 */
	public void shareToSina(String shareContent, String shareUrl, Bitmap bitmap) {
		if (!NetworkUtil.isNetworkAvailable(AppApplication.getAppContext())) {
			Toast.makeText(AppApplication.getAppContext(), "网络不给力",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		try {
			StatusesAPI statusesAPI = new StatusesAPI(
					AccessTokenKeeper.readAccessToken(AppApplication
							.getAppContext()));
			String share = shareContent + " " + shareUrl;
			if (share.length() > 140) {
				share = shareContent.substring(0, shareContent.length()
						- (share.length() - 139))
						+ "... " + shareUrl;
			} else {
				share = shareContent + " " + shareUrl;
			}
			if(bitmapUrl != null && bitmapUrl.length() > 0){
				//高级接口 需要通过认证后申请
//				statusesAPI.uploadUrlText(share, bitmapUrl, null,null, null, requestListener);
//				statusesAPI.upload(share, ImageLoader.instance().getBitmap(bitmapUrl), null, null, requestListener);
			}else if (bitmap != null) {
				statusesAPI.upload(share, bitmap, null, null, requestListener);
			} else {
				statusesAPI.update(share, null, null, requestListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finish();
	}

	private RequestListener requestListener = new RequestListener() {

		@Override
		public void onComplete(String arg0) {
			try {
				Toast.makeText(AppApplication.getAppContext(), R.string.weibosdk_toast_share_success, Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			try {
				Log.e(TAG, arg0.getMessage());
				 Toast.makeText(AppApplication.getAppContext(), 
		                    getString(R.string.weibosdk_toast_share_failed) + "Error Message: " + arg0.getMessage(), 
		                    Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			finish();
			break;
		}

		return super.onTouchEvent(event);
	}
}
