package com.autotiming.csck.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
	private SharedPreferences m_preferences;
	private Context m_Context = null;
	
	public Preferences(Context context) {
		super();
		m_Context = context;
		m_preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public boolean getBoolean(int resId, boolean defValue) {
		return getBoolean(m_Context.getString(resId), defValue);
	}
	public boolean getBoolean(String key, boolean defValue) {
		return m_preferences.getBoolean(key, defValue);
	}
	
	public int getInt(int resId, int defValue) {
		return getInt(m_Context.getString(resId), defValue);
	}
	public int getInt(String key, int defValue) {
		return m_preferences.getInt(key, defValue);
	}
	
	public long getLong(int resId, long defValue) {
		return getLong(m_Context.getString(resId), defValue);
	}
	public long getLong(String key, long defValue) {
		return m_preferences.getLong(key, defValue);
	}
	
	public String getString(int resId, String defValue) {
		return getString(m_Context.getString(resId), defValue);
	}
	public String getString(String key, String defValue) {
		return m_preferences.getString(key, defValue);
	}
	
	public void putBoolean(int resId, boolean value) {
		putBoolean(m_Context.getString(resId), value);
	}
	public void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = m_preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
		editor = null;
	}
	
	public void putInt(int resId, int value) {
		putInt(m_Context.getString(resId), value);
	}
	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = m_preferences.edit();
		editor.putInt(key, value);
		editor.commit();
		editor = null;
	}
	
	public void putLong(int resId, long value) {
		putLong(m_Context.getString(resId), value);
	}
	public void putLong(String key, long value) {
		SharedPreferences.Editor editor = m_preferences.edit();
		editor.putLong(key, value);
		editor.commit();
		editor = null;
	}
	
	public void putString(int resId, String value) {
		putString(m_Context.getString(resId), value);
	}
	public void putString(String key, String value) {
		SharedPreferences.Editor editor = m_preferences.edit();
		editor.putString(key, value);
		editor.commit();
		editor = null;
	}
	
	
	
}
