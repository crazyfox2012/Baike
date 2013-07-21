package com.zhanglh.android.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BaikePrefs {
	private static SharedPreferences shared_prefs;
	
	public final static String KEY_DATA_SOURCE="data_source";
	
	public final static String DEFAULT_DATA_SOURCE="wiki";
	
	public static void init(Context context) {
		shared_prefs=PreferenceManager.getDefaultSharedPreferences(context)	;
	}

	public static String getDataSource() {
		return shared_prefs.getString(KEY_DATA_SOURCE, DEFAULT_DATA_SOURCE);
	}

	public static void setDataSource(String data_source) {
		shared_prefs.edit().putString(KEY_DATA_SOURCE, data_source).commit();
	}
	
}
