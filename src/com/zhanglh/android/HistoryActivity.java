package com.zhanglh.android;

import com.zhanglh.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class HistoryActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
	}

}
