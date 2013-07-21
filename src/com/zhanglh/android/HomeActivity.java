package com.zhanglh.android;

import com.zhanglh.android.R;
import com.zhanglh.android.business.BaikePrefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HomeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
		BaikePrefs.init(this);

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		wikiButton = (RadioButton) findViewById(R.id.radioButton1);
		baiduButton = (RadioButton) findViewById(R.id.radioButton2);
		enterButton = (Button) findViewById(R.id.button1);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (wikiButton.getId() == checkedId) {
					dataSource = "wiki";
				} else {
					dataSource = "baidu";
				}
			}
		});
		
		enterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BaikePrefs.setDataSource(dataSource);
				Intent searchIntent = new Intent(HomeActivity.this,SearchActivity.class);
				startActivity(searchIntent);
				
			}
		});
		
		
	}

	private RadioGroup radioGroup;
	private RadioButton wikiButton;
	private RadioButton baiduButton;
	private Button enterButton;
	private String dataSource="wiki";
}
