package com.autotiming.csck.share.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXAppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI api = WXAPIFactory.createWXAPI(context, WeiXinConstants.WX_APP_ID,false);

		// 将该app注册到微信
		boolean registerResult = api.registerApp(WeiXinConstants.WX_APP_ID);
		Log.i("AppRegister", "app注册结果=========》" + registerResult);
	}
}
