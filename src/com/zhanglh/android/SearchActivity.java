package com.zhanglh.android;

import com.zhanglh.android.R;
import com.zhanglh.android.business.BaikePrefs;

import android.app.Activity;
import android.app.SearchManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SearchActivity extends Activity {

	private Button searchButton;
	private EditText preSearchText;
	private RadioGroup searchRadioGroup;
	private RadioButton contentSearchRadioButton;
	private RadioButton categorySearchRadioButton;
	private ImageView imageView;
	private boolean categorySearch=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		searchButton = (Button) findViewById(R.id.searchButton);
		preSearchText = (EditText) findViewById(R.id.searchText);
		searchRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		contentSearchRadioButton = (RadioButton) findViewById(R.id.radio0); 
		imageView = (ImageView) findViewById(R.id.imageView1);
		
		

		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSearchRequested();

			}
		});
		
		searchRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(contentSearchRadioButton.getId()==checkedId)
					categorySearch=false;
				else
					categorySearch=true;
			}
		});
	}
	
	

	@Override
	protected void onResume() {
		System.out.println(BaikePrefs.getDataSource());
		if(BaikePrefs.getDataSource()=="wiki"){
			imageView.setImageResource(R.drawable.wiki);
		}
			
		else {
			imageView.setImageResource(R.drawable.baidu);
			searchRadioGroup.setVisibility(View.GONE);
		}
		super.onResume();
	}



	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item;

		menu.removeItem(0);
		menu.removeItem(1);

		item = menu.add(0, 0, 0, "ËÑË÷");
		item.setAlphabeticShortcut(SearchManager.MENU_KEY);

		item = menu.add(0, 1, 0, "Çå³ýÀúÊ·");
		return true;
	}

	/** Handle the menu item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			onSearchRequested();
			break;

		case 1:
			clearSearchHistory();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onSearchRequested() {

		final String queryPrefill = preSearchText.getText().toString();
		
		Bundle bundle = new Bundle();
        bundle.putBoolean("category_search", categorySearch);
                
		startSearch(queryPrefill, false, bundle, false);

		return true;
	}

	private void clearSearchHistory() {
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
				SearchSuggestionSampleProvider.AUTHORITY,
				SearchSuggestionSampleProvider.MODE);
		suggestions.clearHistory();
	}

}
