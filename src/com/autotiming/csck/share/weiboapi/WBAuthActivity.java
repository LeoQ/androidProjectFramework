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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 该类主要进行授权、SSO登陆。
 * 
 * @author qiul1
 */
public class WBAuthActivity extends Activity {
    private static final String TAG = WBAuthActivity.class.getSimpleName();
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    /**
	 * 跳转到授权页面
	 * @param context 
	 */
	public static void actionWBAuthActivity(Context context) {
		Intent intent = new Intent(context , WBAuthActivity.class);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
		}
	}

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // 创建微博实例
        mWeiboAuth = new WeiboAuth(this, WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE);
       
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (!mAccessToken.isSessionValid()) {
        	 mSsoHandler = new SsoHandler(WBAuthActivity.this, mWeiboAuth);
             mSsoHandler.authorize(new AuthListener());
        }
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(WBAuthActivity.this, mAccessToken);
                //发送授权成功广播
                sendAuthSuccessBroadcast();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "Auth Failed";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(WBAuthActivity.this, message, Toast.LENGTH_LONG).show();
            }
            
            finish();
        }

		@Override
        public void onCancel() {
            Toast.makeText(WBAuthActivity.this, 
                    "Auth Cancel", Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WBAuthActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    @Override
    protected void onDestroy() {
    	Log.d(TAG, "onDestroy");
    	super.onDestroy();
    }
    
    /**
     * 授权成功发送广播通知
     */
    private void sendAuthSuccessBroadcast() {
		Intent intent = new Intent(WeiboConstants.Auth_Success_Broadcaset_Action);
		sendBroadcast(intent);
	}
    
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