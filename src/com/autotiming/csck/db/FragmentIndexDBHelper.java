package com.autotiming.csck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.autotiming.csck.utils.DLog;

public class FragmentIndexDBHelper extends SQLiteOpenHelper{
	
	/** 数据库名 */
	public static final String	DATABASE_NAME = "_fragment_.db";
	/** 数据库版本号 */
	public static final int DATABASE_VERSION	= 1;

	public FragmentIndexDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public FragmentIndexDBHelper(Context context){
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_ACTION);
		DLog.d("UserActionDBHelper", "create database ecloud_user_action.db");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DELETE_TABLE_ACTION);
		onCreate(db);
	}
	
	private static final String CREATE_TABLE_ACTION = "CREATE TABLE ["+FragmentIndexTable.TABLE_NAME+"] ("+
			"["+FragmentIndexTable.id+"] integer primary key autoincrement,"+
			"["+FragmentIndexTable.author+"] varchar(25),"+
			"["+FragmentIndexTable.cover+"] varchar(255),"+
			"["+FragmentIndexTable.created_at+"] varchar(255),"+
			"["+FragmentIndexTable.deleted_at+"] varchar(255),"+
			"["+FragmentIndexTable.description+"] varchar(255),"+
			"["+FragmentIndexTable.end_time+"] varchar(255),"+
			"["+FragmentIndexTable.is_original+"] varchar(255),"+
			"["+FragmentIndexTable.language+"] varchar(255),"+
			"["+FragmentIndexTable.likes+"] varchar(255),"+
			"["+FragmentIndexTable.start_time+"] varchar(255),"+
			"["+FragmentIndexTable.subtitle+"] varchar(255),"+
			"["+FragmentIndexTable.title+"] varchar(255),"+
			"["+FragmentIndexTable.updated_at+"] varchar(255),"+
			"["+FragmentIndexTable.user_id+"] varchar(255),"+
			"["+FragmentIndexTable.video_id+"] varchar(255),"+
			"["+FragmentIndexTable.views+"] varchar(255),"+
			"["+FragmentIndexTable.guid+"] varchar(255));";
	private static final String DELETE_TABLE_ACTION = "drop table if exists ["+FragmentIndexTable.TABLE_NAME+"];";
			
}
