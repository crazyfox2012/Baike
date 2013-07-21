package com.zhanglh.android;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import com.zhanglh.android.business.BaiduPageParser;
import com.zhanglh.android.business.WikiPageParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class BaiduPageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page);
		String page = getIntent().getStringExtra("page");
		String urlString = "http://baike.baidu.com" + page;
		map = BaiduPageParser.html2string(urlString);
		title = (String) map.get("title");
		setTitle(title);
		context = this;

		listView = (ListView) findViewById(R.id.pageListView);

		listView.setFastScrollEnabled(true);

		listView.setAdapter(new PageAdapter(this, title));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				Log.i("page", position + "");
				if (position != 0)
					((PageAdapter) listView.getAdapter()).toggle(position);
				// TODO Auto-generated method stub

			}
		});

	}

	private class PageAdapter extends BaseAdapter {
		private static final int TITLE = 0;
		private static final int CARD = 1;
		private static final int CONTENT = 2;

		public PageAdapter(Context context, String title) {
			mContext = context;
			for (int i = 0; i < mExpanded.length; i++)
				mExpanded[i] = false;

		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (position == 0)
				return 0;
			if (position == 1)
				return 1;
			else
				return 2;

		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return TOTAL_TYPE_COUNT;
		}

		public int getCount() {
			return 3;
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/**
		 * Make a SpeechView to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {

			switch (getItemViewType(position)) {
			case TITLE:
				TitleView titleView;
				if (convertView == null) {
					titleView = new TitleView(mContext, title);
				} else {
					titleView = (TitleView) convertView;
					titleView.setTitle(title);
				}
				return titleView;

			case CARD:
				String summary = (String) map.get("summary");
				ParagraphView cardView;
				cardView = new ParagraphView(mContext, summary, "百科名片",mExpanded[position]);
				cardView.setExpanded(mExpanded[position]);
				return cardView;

			case CONTENT:
				String content = (String) map.get("content");
				ParagraphView paragraphView;
				paragraphView = new ParagraphView(mContext, content, "详细信息",mExpanded[position]);
				paragraphView.setExpanded(mExpanded[position]);
				return paragraphView;

			default:
				return convertView;
			}
		}

		public void toggle(int position) {
			mExpanded[position] = !mExpanded[position];
			notifyDataSetChanged();
		}

		private Context mContext;
		private boolean[] mExpanded = new boolean[100];
		private static final int TOTAL_TYPE_COUNT = 3;

	}

	private class TitleView extends LinearLayout {
		public TitleView(Context context, String title) {
			super(context);

			this.setOrientation(VERTICAL);

			titleTextView = new TextView(context);
			titleTextView.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			titleTextView.setGravity(Gravity.CENTER);
			titleTextView.setPadding(5, 5, 5, 5);
			titleTextView.setText(title);
			titleTextView.setTextSize(25);
			titleTextView.setTextColor(0xff0000bf);

			addView(titleTextView, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		}

		public void setTitle(String title) {
			titleTextView.setText(title);
		}

		private TextView titleTextView;
	}

	private class ParagraphView extends LinearLayout {
		public ParagraphView(Context context, String paragraph, String innerTitle, boolean expanded) {
			super(context);

			this.setOrientation(VERTICAL);

			innerTitleTextView = new TextView(context);
			innerTitleTextView.setText(innerTitle);
			innerTitleTextView.setPadding(5, 5, 5, 5);
			innerTitleTextView.setTextSize(20);
			innerTitleTextView.setTextColor(0xff000000);
			addView(innerTitleTextView, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			contentTextView = new TextView(context);
			contentTextView.setTextColor(0xff000000);
			contentTextView.setPadding(5, 5, 5, 5);
			addView(contentTextView, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			ImageGetter imgGetter = new Html.ImageGetter() {

				@Override
				public Drawable getDrawable(String source) {
					// TODO Auto-generated method stub

					Drawable drawable = null;
					URL url;
					try {
						url = new URL(source);
						drawable = Drawable.createFromStream(url.openStream(),
								"");
					} catch (Exception e) {
						return null;
					}
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					return drawable;
				}
			};


			contentTextView.setText(Html.fromHtml(paragraph, imgGetter, null));
			contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
			CharSequence textCharSequence = contentTextView.getText();

			if (textCharSequence instanceof Spannable) {
				int end = textCharSequence.length();
				Spannable sp = (Spannable) contentTextView.getText();
				URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
				SpannableStringBuilder style = new SpannableStringBuilder(
						textCharSequence);
				for (URLSpan url : urls) 
					style.removeSpan(url);// should clear old spans
				for (URLSpan url : urls) {
					String urlString = url.getURL();
					MyURLSpan myURLSpan = new MyURLSpan(urlString);
					if (urlString.startsWith("/view/")) {
						style.setSpan(myURLSpan, sp.getSpanStart(url), sp
								.getSpanEnd(url),
								Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					} else {
						style.setSpan(new URLSpan(urlString), sp
								.getSpanStart(url), sp.getSpanEnd(url),
								Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

					}
				}
				contentTextView.setText(style);
			}
			contentTextView.setVisibility(expanded ? VISIBLE : GONE);

		}
		
		public void setExpanded(boolean expanded) {
			
			contentTextView.setVisibility(expanded ? VISIBLE : GONE);
		}

		private TextView innerTitleTextView;
		private TextView contentTextView; 
	}
	
	private class MyURLSpan extends ClickableSpan {

		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			
			Intent pageIntent = new Intent(context, WikiPageActivity.class);
			pageIntent.putExtra("page", mUrl);
			startActivity(pageIntent);

		}
	}

	private Map<String, Object> map;
	private String title;
	private ListView listView;
	private Context context;
}
