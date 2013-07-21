package com.zhanglh.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zhanglh.android.R;
import com.zhanglh.android.business.CategoryIndexParser;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryIndexActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_result);
		setTitle("中文维基百科分类索引");
		context = this;

		ArrayList<Map<String, Object>> arrayList = CategoryIndexParser
				.html2string();

		String[] from = { "title", "content", "image" };
		int[] to = { R.id.multiple_title, R.id.multiple_summary, R.id.tag };
		CategoryIndexAdapter adapter = new CategoryIndexAdapter(this,
				arrayList, R.layout.content_search_result_item, from, to);

		setListAdapter(adapter);
	}

	public class CategoryIndexAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<Map<String, Object>> data;
		private int resource;
		private String[] from;
		private int[] to;

		public CategoryIndexAdapter(Context context,
				List<Map<String, Object>> data, int resource, String[] from,
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
				if (convertView.findViewById(to[i]) instanceof ImageView) {
					ImageView imageView = (ImageView) convertView
							.findViewById(to[i]);
					imageView.setImageBitmap((Bitmap) data.get(position).get(
							from[i]));
				} else {
					TextView tv = (TextView) convertView.findViewById(to[i]);
					String htmlLinkText = (String) data.get(position).get(
							from[i]);
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
			String title;
			try {
				title = URLDecoder.decode(mUrl.substring(15), "utf-8");
				Intent categoryIntent = new Intent(context, CategoryActivity.class);
				categoryIntent.putExtra("category", title);
				startActivity(categoryIntent);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Context context;
}
