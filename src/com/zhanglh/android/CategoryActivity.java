package com.zhanglh.android;

import java.io.*;
import java.net.*;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.zhanglh.android.R;
import com.zhanglh.android.business.CategoryParser;

public class CategoryActivity extends Activity {
	
	private LinearLayout ln_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category);
		context=this;
		
		ln_main = (LinearLayout) this.findViewById(R.id.mainlayout);
		TextView sonCategoryTextView = (TextView) findViewById(R.id.sonCategoryTextView);
		TextView pageTextView = (TextView) findViewById(R.id.pageTextView);
		
		String category = getIntent().getStringExtra("category");
		setTitle("∑÷¿‡£∫" + category);

		URL url;

		try {
			String search = URLEncoder.encode(category, "utf-8");

			ListView sonCategoryListView = (ListView) ln_main.findViewById(R.id.sonCategoryListView);
			url = new URL(
					"http://zh.wikipedia.org/w/api.php?action=query&list=categorymembers"
							+ "&cmtitle=Category:"+search 
							+ "&cmlimit=500&cmtype=subcat&format=xml");
			final ArrayList<Map<String, String>> sonCategoryArrayList = CategoryParser
					.xml2string(url,true);
			if(sonCategoryArrayList!=null && !sonCategoryArrayList.isEmpty())
				sonCategoryTextView.setText(R.string.son_category);
			else 
				sonCategoryTextView.setVisibility(View.GONE);
			
			String[] from = { "category" };
			int[] to = { R.id.category_item };
			SimpleAdapter sonCategoryAdapter = new SimpleAdapter(this,
					sonCategoryArrayList, R.layout.category_search_result_item, from, to);

			sonCategoryListView.setAdapter(sonCategoryAdapter);

			sonCategoryListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> adapterView,
								View view, int position, long id) {
							Intent category_intent=new Intent(CategoryActivity.this, CategoryActivity.class);
							
							category_intent.putExtra("category", sonCategoryArrayList.get(position).get("category"));
				        	startActivity(category_intent);
							System.out.println("id----------" + id);
							System.out.println("position----" + position);

						}
					});
			
			ListView pageListView = (ListView) ln_main.findViewById(R.id.pageListView);
			url = new URL(
					"http://zh.wikipedia.org/w/api.php?action=query&list=categorymembers"
							+ "&cmtitle=Category:"+search 
							+ "&cmlimit=500&cmtype=page&format=xml");

			final ArrayList<Map<String, String>> pageArrayList = CategoryParser
					.xml2string(url,false);
//			if(pageArrayList!=null && !pageArrayList.isEmpty())
				pageTextView.setText(R.string.page_in_category);
//			else 
//				pageTextView.setVisibility(View.GONE);
			
			String[] from2 = { "category" };
			int[] to2 = { R.id.category_item };
			SimpleAdapter pageAdapter = new SimpleAdapter(this,
					pageArrayList, R.layout.category_search_result_item, from2, to2);

			pageListView.setAdapter(pageAdapter);

			pageListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> adapterView,
								View view, int position, long id) {
							Intent pageIntent = new Intent(context, WikiPageActivity.class);
							pageIntent.putExtra("page", pageArrayList.get(position).get("category"));
							startActivity(pageIntent);
							System.out.println("category----" + pageArrayList.get(position).get("category"));
							System.out.println("position----" + position);

						}
					});			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private Bitmap getRemoteImage(URL aURL) throws IOException {
		URLConnection conn = aURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		Bitmap bm = BitmapFactory.decodeStream(bis);
		bis.close();
		is.close();
		return bm;
	}
	
	private Context context;

}