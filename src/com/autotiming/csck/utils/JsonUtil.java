package com.autotiming.csck.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * @author qiuli
 */
public class JsonUtil {
	private static Gson mGson = new Gson();
	
	public static synchronized <T> T fromJsonString(String jsonString, TypeToken<T> typeToken) {
		try {
			return (T)mGson.fromJson(jsonString, typeToken.getType());
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static synchronized <T> T fromJsonString(String jsonString, Class<T> objClass) {
		try {
			return (T)mGson.fromJson(jsonString, objClass);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static synchronized <T> T fromJsonElement(JsonElement element, Class<T> objClass) {
		try {
			return (T)mGson.fromJson(element, objClass);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
