package com.zhanglh.android;

import java.io.*;
import java.net.*;
import java.util.*;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zhanglh.android.R;
import com.zhanglh.android.business.BaiduSearchListParser;
import com.zhanglh.android.business.BaikePrefs;
import com.zhanglh.android.business.CategoryIndexParser;
import com.zhanglh.android.business.CategorySearchListParser;
import com.zhanglh.android.business.ContentSearchListParser;

public class SearchResultActivity extends ListActivity {

	private ContentSearchAdapter contentSearchResultAdapter;
	private SimpleAdapter categorySearchResultAdapter;
	private String query;
	private boolean categorySearch = false;
	private ArrayList<Map<String, String>> arrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setTitle("ËÑË÷½á¹û");

		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			query = queryIntent.getStringExtra(SearchManager.QUERY);

			doSearchQuery(queryIntent);
		}

	}

	@Override
	public void onNewIntent(final Intent newIntent) {
		super.onNewIntent(newIntent);

		// get and process search query here
		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			doSearchQuery(queryIntent);
		}
	}

	private void doSearchQuery(final Intent queryIntent) {

		final String queryString = queryIntent
				.getStringExtra(SearchManager.QUERY);
		// mQueryText.setText(queryString);

		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
				SearchSuggestionSampleProvider.AUTHORITY,
				SearchSuggestionSampleProvider.MODE);
		suggestions.saveRecentQuery(queryString, null);

		final Bundle bundle = queryIntent
				.getBundleExtra(SearchManager.APP_DATA);

		if (bundle != null)
			categorySearch = bundle.getBoolean("category_search");

		URL url;

		if (BaikePrefs.getDataSource() == "wiki") {
			try {
				String search = URLEncoder.encode(query, "utf-8");

				if (!categorySearch) {
					setContentView(R.layout.search_result);
					url = new URL(
							"http://zh.wikipedia.org/w/api.php?action=opensearch&search="
									+ search + "&format=xml");

					arrayList = ContentSearchListParser.xml2string(url);

					String[] from = { "Text", "Description" };
					int[] to = { R.id.multiple_title, R.id.multiple_summary };
					contentSearchResultAdapter = new ContentSearchAdapter(this,
							arrayList, R.layout.content_search_result_item,
							from, to);

					setListAdapter(contentSearchResultAdapter);

				} else {
					setContentView(R.layout.search_result);

					url = new URL(
							"http://zh.wikipedia.org/w/api.php?action=query&list=allcategories&aclimit=30&acprefix="
									+ search + "&format=xml");

					arrayList = CategorySearchListParser.xml2string(url);

					String[] from = { "category" };
					int[] to = { R.id.category_item };
					categorySearchResultAdapter = new SimpleAdapter(this,
							arrayList, R.layout.category_search_result_item,
							from, to);

					setListAdapter(categorySearchResultAdapter);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			setContentView(R.layout.search_result);
			arrayList = BaiduSearchListParser.html2string(query);
			String[] from = { "title", "content" };
			int[] to = { R.id.baidu_title, R.id.baidu_content };
			BaiduSearchAdapter adapter = new BaiduSearchAdapter(this,
					arrayList, R.layout.baidu_search_result_item, from, to);

			setListAdapter(adapter);
		}

	}

	public class ContentSearchAdapter extends SimpleAdapter {

		Map<Integer, Boolean> map;

		LayoutInflater mInflater;

		private List<? extends Map<String, ?>> mList;

		public ContentSearchAdapter(Context context,
				List<Map<String, String>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			map = new HashMap<Integer, Boolean>();
			mInflater = LayoutInflater.from(context);
			mList = data;
			for (int i = 0; i < data.size(); i++) {
				map.put(i, false);
			}
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.content_search_result_item, null);
			}
			TextView tN = (TextView) convertView
					.findViewById(R.id.multiple_title);
			tN.setText((String) mList.get(position).get("Text"));

			TextView tP = (TextView) convertView
					.findViewById(R.id.multiple_summary);
			tP.setText((String) mList.get(position).get("Description"));

			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.tag);

			try {
				String urlStr = (String) mList.get(position).get("Image");

				if (urlStr != null && urlStr != "") {
					Log.i("SearchResult", position + urlStr);
					URL url = new URL(urlStr);
					imageView.setImageBitmap(getRemoteImage(url));
				} else {
					imageView.setImageBitmap(null);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;
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

	}

	public class BaiduSearchAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<Map<String, String>> data;
		private int resource;
		private String[] from;
		private int[] to;

		public BaiduSearchAdapter(Context context,
				List<Map<String, String>> data, int resource, String[] from,
				int[] to) {
			this.mInflater = LayoutInflater.from(context);
			this.data = data;
			this.resource = resource;
			this.from = from;
			this.to = to;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(resource, null);
			for (int i = 0; i < from.length; i++) {

				TextView tv = (TextView) convertView.findViewById(to[i]);
				String htmlLinkText = (String) data.get(position).get(from[i]);
				tv.setText(Html.fromHtml(htmlLinkText));
				tv.setMovementMethod(LinkMovementMethod.getInstance());
				CharSequence text = tv.getText();
				if (text instanceof Spannable) {
					int end = text.length();
					Spannable sp = (Spannable) tv.getText();
					URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
					SpannableStringBuilder style = new SpannableStringBuilder(
							text);
					style.clearSpans();
					for (URLSpan url : urls) {
						MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
						style.setSpan(myURLSpan, sp.getSpanStart(url), sp
								.getSpanEnd(url),
								Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					}
					tv.setText(style);
				}

			}
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
	}

	private class MyURLSpan extends ClickableSpan {

		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			
				
				Intent categoryIntent = new Intent(context,
						BaiduPageActivity.class);
				categoryIntent.putExtra("page", mUrl);
				startActivity(categoryIntent);
			
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if (!categorySearch) {
			Intent pageIntent = new Intent(this, WikiPageActivity.class);
			pageIntent.putExtra("page", arrayList.get(position).get("Text"));
			startActivity(pageIntent);
		} else {
			Intent categoryIntent = new Intent(this, CategoryActivity.class);
			categoryIntent.putExtra("category", arrayList.get(position).get(
					"category"));
			startActivity(categoryIntent);
		}
	}

	private Context context;
}