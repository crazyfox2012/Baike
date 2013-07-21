package com.zhanglh.android;

import com.zhanglh.android.R;

import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost mHost;
	private Intent homeIntent;
	private Intent searchIntent;
	private Intent catagoryIntent;
	private Intent featuredContentIntent;
	private Intent historyIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		Log.i("TabActivity", "success1");

		// ~~~~~~~~~~~~ 初始化
		this.homeIntent = new Intent(this, HomeActivity.class);
		this.searchIntent = new Intent(this, SearchActivity.class);
		this.featuredContentIntent = new Intent(this,
				FeaturedContentActivity.class);
		this.catagoryIntent = new Intent(this, CategoryIndexActivity.class);
		this.historyIntent = new Intent(this, HistoryActivity.class);
		initRadios();
		setupIntent();
	}

	/**
	 * 初始化底部按钮
	 */
	private void initRadios() {
		((RadioButton) findViewById(R.id.radio_button0))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button2))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button3))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button4))
				.setOnCheckedChangeListener(this);
	}

	/**
	 * 切换模块
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				this.mHost.setCurrentTabByTag("home_tab");
				break;
			case R.id.radio_button1:
				this.mHost.setCurrentTabByTag("search_tab");
				break;
			case R.id.radio_button2:
				this.mHost.setCurrentTabByTag("catagory_tab");
				break;
			case R.id.radio_button3:
				this.mHost.setCurrentTabByTag("featured_content_tab");
				break;
			case R.id.radio_button4:
				this.mHost.setCurrentTabByTag("history_tab");
				break;
			}
		}
	}

	private void setupIntent() {
		this.mHost = getTabHost();
		TabHost localTabHost = this.mHost;

		localTabHost.addTab(buildTabSpec("home_tab", R.string.home,
				R.drawable.icon_1_n, this.homeIntent));

		localTabHost.addTab(buildTabSpec("search_tab", R.string.search,
				R.drawable.icon_2_n, this.searchIntent));

		localTabHost.addTab(buildTabSpec("catagory_tab", R.string.main_news,
				R.drawable.icon_3_n, this.catagoryIntent));

		localTabHost.addTab(buildTabSpec("featured_content_tab",
				R.string.main_my_info, R.drawable.icon_4_n,
				this.featuredContentIntent));

		localTabHost.addTab(buildTabSpec("history_tab", R.string.history,
				R.drawable.icon_5_n, this.historyIntent));

	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}
}
