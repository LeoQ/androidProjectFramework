package com.autotiming.csck.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.autotiming.csck.utils.DLog;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity{
	private long mInTime;
	private long mOutTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInTime = System.currentTimeMillis()/1000;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//统计页面
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		//统计时长
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mOutTime = System.currentTimeMillis()/1000;
	}
	
	/**获得activity界面停留时间*/
	public String getStayTime(){
		mOutTime = System.currentTimeMillis()/1000;
		DLog.i(getClass().getSimpleName(), "mOutTime - mInTime >>>" + mOutTime + "-"+mInTime +"=" + (mOutTime-mInTime));
		return String.valueOf(mOutTime - mInTime);
	}
	
	
}
