package com.autotiming.csck.share.wxapi;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.autotiming.csck.R;
import com.autotiming.csck.activity.AppApplication;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "WXEntryActivity";

	private IWXAPI wxAPI;
	private static final int THUMB_SIZE = 150;
	private AsyncTask<String, Long, Boolean> task;
	private int shareTpye;
	private int wxType; // 微信分享类型 朋友圈；微信朋友
	private String title;
	private String des;
	private String shareUrl;
	private String bitmapUrl;
	private static Bitmap mBitmap;
	private Handler mHandler = new Handler();

	public static void actionWXEntryActivity(Context context, int type,
			Bitmap bitmap, String shareUrl, String title, String des,
			int wxscenetimeline) {
		Intent intent = new Intent(context, WXEntryActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("shareUrl", shareUrl);
		intent.putExtra("title", title);
		intent.putExtra("des", des);
		intent.putExtra("WXType", wxscenetimeline);
		mBitmap = bitmap;
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, R.string.weibosdk_toast_share_failed,
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void actionWXEntryActivity(Context context, int type,
			String bitmapUrl, String shareUrl, String title, String des,
			int wxscenetimeline) {
		Intent intent = new Intent(context, WXEntryActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("shareUrl", shareUrl);
		intent.putExtra("title", title);
		intent.putExtra("des", des);
		intent.putExtra("bitmapUrl", bitmapUrl);
		intent.putExtra("WXType", wxscenetimeline);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, R.string.weibosdk_toast_share_failed,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		wxAPI.handleIntent(getIntent(), this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		wxAPI = WXAPIFactory.createWXAPI(this, WeiXinConstants.WX_APP_ID);
		// 将该app注册到微信
		wxAPI.registerApp(WeiXinConstants.WX_APP_ID);
		wxAPI.handleIntent(getIntent(), this);

		if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
			task.cancel(true);
		}
		// 初始化数据
		initDatas();

		task = new ShareWXTask(wxType);
		task.execute("");

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				finish();
			}
		}, 6000);

	}

	private void initDatas() {
		wxType = getIntent().getIntExtra("WXType",
				WeiXinConstants.WXSceneTimeline);
		title = getIntent().getStringExtra("title");
		des = getIntent().getStringExtra("des");
		shareUrl = getIntent().getStringExtra("shareUrl");
		bitmapUrl = getIntent().getStringExtra("bitmapUrl");
		shareTpye = getIntent().getIntExtra("type",
				WeiXinConstants.WEBPAGE_WX_TYPE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
//		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		mBitmap = null;
		task = null;
		super.onDestroy();
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		// 从微信聊天窗口的附件栏中选择logo会跑此段代码
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			break;
		default:
			break;
		}
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onResp");
		String txt = "分享失败";
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			txt = "分享成功";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			break;
		default:
			break;
		}

		Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
		finish();
	}

	private class ShareWXTask extends AsyncTask<String, Long, Boolean> {
		private int type;
		ProgressDialog progress;

		public ShareWXTask(int mtype) {
			type = mtype;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(WXEntryActivity.this);
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setMessage("请稍后...");
			progress.setCancelable(true);
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;

			Log.d("WX", "doInBackground:" + shareUrl);
			WXMediaMessage msg = new WXMediaMessage();
			switch (shareTpye) {
			case WeiXinConstants.IMAGE_WX_TYPE:
				WXImageObject imageObject = new WXImageObject();
				imageObject.imageUrl = shareUrl;
				msg.mediaObject = imageObject;
				break;
			case WeiXinConstants.MUSIC_WX_TYPE:
				WXMusicObject musicObject = new WXMusicObject();
				musicObject.musicUrl = shareUrl;
				msg.mediaObject = musicObject;
				break;
			case WeiXinConstants.VIDEO_WX_TYPE:
				WXVideoObject videoObject = new WXVideoObject();
				videoObject.videoUrl = shareUrl;
				msg.mediaObject = videoObject;
				break;
			case WeiXinConstants.WEBPAGE_WX_TYPE:
				WXWebpageObject webpageObject = new WXWebpageObject();
				webpageObject.webpageUrl = shareUrl;
				msg.mediaObject = webpageObject;
				break;
			default:
				break;
			}

			msg.title = title;
			msg.description = des;
			Bitmap thumbBmp = null;

			if (mBitmap != null) {
				thumbBmp = Bitmap.createScaledBitmap(mBitmap, THUMB_SIZE,
						THUMB_SIZE, true);
			} else if (bitmapUrl != null) {
//				thumbBmp = Bitmap.createScaledBitmap(ImageLoader.instance()
//						.getBitmap(bitmapUrl), THUMB_SIZE, THUMB_SIZE, true);
			} else {
				thumbBmp = Bitmap.createScaledBitmap(BitmapFactory
						.decodeResource(AppApplication.getAppContext()
								.getResources(), R.drawable.ic_launcher),
						THUMB_SIZE, THUMB_SIZE, true);
			}

			msg.setThumbImage(thumbBmp);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = type == WeiXinConstants.WXSceneTimeline ? SendMessageToWX.Req.WXSceneTimeline
					: SendMessageToWX.Req.WXSceneSession;
			result = wxAPI.sendReq(req);

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if(!isFinishing() && progress != null && progress.isShowing()){
					progress.dismiss();
				}
				if (!result) {
					Toast.makeText(WXEntryActivity.this, "分享失败",
							Toast.LENGTH_SHORT).show();
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	public byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
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
