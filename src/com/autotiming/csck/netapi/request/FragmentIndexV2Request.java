package com.autotiming.csck.netapi.request;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.autotiming.csck.bean.FragmentIndexBean;
import com.autotiming.csck.netapi.GsonRequest;
import com.autotiming.csck.netapi.RequestManager;
import com.autotiming.csck.netapi.URIConstants;

public class FragmentIndexV2Request {
	private static final String TAG = FragmentIndexV2Request.class.getSimpleName();
	
	public static FragmentIndexV2Request getInstance(){
		return SingleHolder.instance;
	}
	
	private static class SingleHolder{
		private static FragmentIndexV2Request instance = new FragmentIndexV2Request(); 
	}
	
	protected FragmentIndexV2Request(){
		
	}

	public void getFragmentIndex(Listener<FragmentIndexBean> listener,
			ErrorListener errorListener, int page, int limit) {
		
		Uri.Builder uriBuilder = Uri.parse(URIConstants.FragmnetIndex.FULL_PATH).buildUpon()
				.appendQueryParameter(URIConstants.FragmnetIndex.PARAMETER_PAGE, String.valueOf(page))
				.appendQueryParameter(URIConstants.FragmnetIndex.PARAMETER_LIMIT, String.valueOf(limit));

		String uri = uriBuilder.build().toString();
		Log.i(TAG, "getTweetForHashtag: uri = " + uri);

		GsonRequest<FragmentIndexBean> request = new GsonRequest<FragmentIndexBean>(Method.GET
				, uri
				, FragmentIndexBean.class
				, listener
				, errorListener);
		
		request.setTag(TAG);
		Log.v(TAG, request.toString());
		RequestManager.getRequestQueue().add(request);
		
	}

	public void postFragmentIndex(Listener<FragmentIndexBean> listener,
			ErrorListener errorListener, int page, int limit) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(URIConstants.FragmnetIndex.PARAMETER_PAGE, String.valueOf(page));
		params.put(URIConstants.FragmnetIndex.PARAMETER_LIMIT, String.valueOf(limit));
		
		GsonRequest<FragmentIndexBean> request = new GsonRequest<FragmentIndexBean>(Method.POST
				, URIConstants.FragmnetIndex.FULL_PATH
				, params
				, FragmentIndexBean.class
				, listener
				, errorListener);
		
		request.setTag(TAG);
		Log.v(TAG, request.toString());
		RequestManager.getRequestQueue().add(request);
	}
	
	/**
	 * 取消网络请求
	 */
	public void cancleRequest() {
		RequestManager.getRequestQueue().cancelAll(TAG);
	}
}
