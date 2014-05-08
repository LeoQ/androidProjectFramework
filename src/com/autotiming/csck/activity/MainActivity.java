package com.autotiming.csck.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.autotiming.csck.R;
import com.autotiming.csck.bean.FragmentIndexBean;
import com.autotiming.csck.netapi.image.ImageCacheManager;
import com.autotiming.csck.netapi.request.FragmentIndexV2Request;
import com.autotiming.csck.utils.DLog;

public class MainActivity extends BaseActivity{
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		ImageCacheManager.getInstance().getImageLoader().get("http://219.232.161.206:80/data/userdata/vismam/downfile/201307/01192727vd6B.jpeg"
//				, ImageCacheManager.getInstance().getImageLoader().getImageListener((ImageView)findViewById(R.id.img), R.drawable.about_ecloud_icon, R.drawable.about_ecloud_icon));
		ImageCacheManager.getInstance().getImageLoader().get("http://219.232.161.206:80/data/userdata/vismam/downfile/201307/01192727vd6B.jpeg"
				, ImageCacheManager.getInstance().getImageLoader().getImageListener((ImageView)findViewById(R.id.img), R.drawable.about_ecloud_icon, R.drawable.about_ecloud_icon)
				, 100,100);
		findViewById(R.id.img).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentIndexV2Request.getInstance().getFragmentIndex(createListener(), createErrorListener(), 1, 10);
				FragmentIndexV2Request.getInstance().postFragmentIndex(createListener(), createErrorListener(), 1, 10);
			}
		});
		
	}
	
	private ErrorListener createErrorListener() {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(MainActivity.this, "onErrorResponse", Toast.LENGTH_SHORT).show();
				DLog.e(TAG, "onErrorResponse-->" + error.getMessage());
			}
		};
	}

	private Listener<FragmentIndexBean> createListener() {
		return new Listener<FragmentIndexBean>() {

			@Override
			public void onResponse(FragmentIndexBean response) {
				Toast.makeText(MainActivity.this, response.msgcode + " onResponse", Toast.LENGTH_SHORT).show();
//				DLog.d(TAG, response.msgcode + " " + response.response.data.get(0).cover);
			}
		};
	}

	@Override
	protected void onDestroy() {
		System.out.println("onDestroy");
		Log.d(TAG, "onDestroy log v");
		super.onDestroy();
		FragmentIndexV2Request.getInstance().cancleRequest();
	}
}
