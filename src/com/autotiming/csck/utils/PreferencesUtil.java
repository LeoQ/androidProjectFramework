package com.autotiming.csck.utils;

import com.autotiming.csck.activity.AppApplication;


public class PreferencesUtil {
	private static Preferences pref = new Preferences(AppApplication.getAppContext());
	
	public static int getInt(String name){
		return pref.getInt(name, -1);
	}
	
	public static int getInt(int resId){
		return pref.getInt(resId, -1);
	}
	
	public static String getString(String name){
		return pref.getString(name, null);
	}
	
	public static String getString(int resId){
		return pref.getString(resId, null);
	}
	
	public static boolean getBoolean(String name){
		return pref.getBoolean(name, false);
	}
	
	public static boolean getBoolean(int resId){
		return pref.getBoolean(resId, false);
	}
	
	public static long getLong(String name){
		return pref.getLong(name, -1);
	}
	
	public static long getLong(int resId){
		return pref.getLong(resId, -1);
	}
	
	public static void putInt(String key, int value){
		pref.putInt(key, value);
	}
	
	public static void putInt(int key, int value){
		pref.putInt(key, value);
	}
	
	public static void putLong(String key, long value){
		pref.putLong(key, value);
	}
	
	public static void putLong(int key, long value){
		pref.putLong(key, value);
	}
	
	public static void putBoolean(String key, boolean value){
		pref.putBoolean(key, value);
	}
	
	public static void putBoolean(int key, boolean value){
		pref.putBoolean(key, value);
	}
	
	public static void putString(String key, String value){
		pref.putString(key, value);
	}
	
	public static void putString(int key, String value){
		pref.putString(key, value);
	}
}
