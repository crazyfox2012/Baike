package com.zhanglh.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import com.zhanglh.android.business.WikiPageParser;

import android.R.integer;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class WikiPageActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page);
		ctx = this;
		title = getIntent().getStringExtra("page");
		setTitle(title);

		// ScrollView scrollView = (ScrollView)findViewById(R.id.scollView);
		listView = (ListView) findViewById(R.id.pageListView);

		listView.setFastScrollEnabled(true);

		listView.setAdapter(new PageAdapter(this, title));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				// position -= listView.getHeaderViewsCount();
				Log.i("page", position + "");
				if (position != 0)
					((PageAdapter) listView.getAdapter()).toggle(position);
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * A sample ListAdapter that presents content from arrays of speeches and
	 * text.
	 * 
	 */
	private class PageAdapter extends BaseAdapter {
		private static final int TITLE = 0;
		private static final int PARAGRAPH = 1;

		public PageAdapter(Context context, String title) {
			mContext = context;
			pageArrayList = WikiPageParser.html2string(title);
			expandPosition=0;

		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			String str = (String) pageArrayList.get(position).get("type");
			return Integer.parseInt(str);
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return TOTAL_TYPE_COUNT;
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return pageArrayList.size();
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pageArrayList.get(position);
		}

		/**
		 * Make a SpeechView to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {

			String title = (String) pageArrayList.get(position).get("title");

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

			case PARAGRAPH:
				contentArrayList = (ArrayList<Map<String, Object>>) pageArrayList
						.get(position).get("content");

				ParagraphView paragraphView;

				paragraphView = new ParagraphView(mContext, title,
						contentArrayList);

				paragraphView.setTitle(title);
				if(position==expandPosition)
					paragraphView.setContentText();

				return paragraphView;

			default:
				return convertView;
			}

		}

		public void toggle(int position) {
			expandPosition=position;
			notifyDataSetChanged();
		}

		private Context mContext;
		private ArrayList<Map<String, Object>> contentArrayList;
		private int expandPosition;
		private static final int TOTAL_TYPE_COUNT = 2;

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
		private Context context;
		
		public ParagraphView(Context context, String title,
				ArrayList<Map<String, Object>> contentArrayList
				) {
			super(context);

			this.contentArrayList=contentArrayList;
			this.context=context;
			
			this.setOrientation(VERTICAL);

			innerTitleTextView = new TextView(context);
			innerTitleTextView.setText(title);
			innerTitleTextView.setPadding(5, 5, 5, 5);
			innerTitleTextView.setTextSize(20);
			innerTitleTextView.setTextColor(0xff000000);
			addView(innerTitleTextView, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			contentTextViews = new ArrayList<TextView>();
		}
		
		private void setContentText()
		{

			
			tables = new ArrayList<TableLayout>();

			String pictureSrc = "";
			for (int i = 0; i < contentArrayList.size(); i++) {
				TextView contenTextView = new TextView(context);
				String text = "";

				if (contentArrayList.get(i).get("p") != null) {
					text = (String) (contentArrayList.get(i).get("p"));
					Log.i("pageText", text);
					
					contenTextView.setText(Html.fromHtml(text));
					contenTextView.setMovementMethod(LinkMovementMethod
							.getInstance());
					CharSequence textCharSequence = contenTextView.getText();

					if (textCharSequence instanceof Spannable) {
						int end = textCharSequence.length();
						Spannable sp = (Spannable) contenTextView.getText();
						URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
						SpannableStringBuilder style = new SpannableStringBuilder(
								textCharSequence);
						style.clearSpans();// should clear old spans
						for (URLSpan url : urls) {
							String urlString = url.getURL();
							MyURLSpan myURLSpan = new MyURLSpan(urlString);
							if (urlString.startsWith("/wiki/")) {
								style.setSpan(myURLSpan, sp.getSpanStart(url),
										sp.getSpanEnd(url),
										Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
							} else if (urlString.startsWith("/w/")) {
								;
							} else {
								style.setSpan(new URLSpan(urlString), sp
										.getSpanStart(url), sp.getSpanEnd(url),
										Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

							}
						}

						if (pictureSrc != "") {

							URL url;
							try {
								url = new URL(pictureSrc);
								Bitmap bitmap = getRemoteImage(url);
								ImageSpan imageSpan = new ImageSpan(bitmap,
										ImageSpan.ALIGN_BOTTOM);
								style.setSpan(imageSpan, 0, 1,
										Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						contenTextView.setText(style);
					}
					pictureSrc = "";
				}

				else if (contentArrayList.get(i).get("h3") != null) {
					pictureSrc = "";
					text = (String) contentArrayList.get(i).get("h3");
					contenTextView.setText(text);
				}

				else if (contentArrayList.get(i).get("table") != null) {
					ArrayList<ArrayList<Map<String, String>>> trArrayList = (ArrayList<ArrayList<Map<String, String>>>) contentArrayList
							.get(i).get("table");
					Log.i("pageActivity", "table");
					TableLayout tableLayout = new TableLayout(context);
					tableLayout.setStretchAllColumns(true);
					tableLayout.setShrinkAllColumns(true);
					for (ArrayList<Map<String, String>> thOrTdArrayList : trArrayList) {
						TableRow tableRow = new TableRow(context);
						for (Map<String, String> thOrTdMap : thOrTdArrayList) {
							TextView textView = new TextView(context);
							textView.setTextColor(0xff000000);
							if (thOrTdMap.get("colspan") != null
									&& thOrTdMap.get("colspan") != "") {
								int cosplan = Integer.parseInt(thOrTdMap
										.get("colspan"));
								Log.i("cosplan", cosplan + "");
								TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
								layoutParams.span = cosplan;
								textView.setLayoutParams(layoutParams);
							}
							if (thOrTdMap.get("align") != null
									&& thOrTdMap.get("align") != "") {
								if (thOrTdMap.get("align") == "center") {
									textView.setGravity(Gravity.CENTER);
									Log.i("center", "center");
								}
							}
							String tableText = thOrTdMap.get("text");
							Log.i("pageActivity", thOrTdMap.get("text"));
							ImageGetter imgGetter = new Html.ImageGetter() {

								@Override
								public Drawable getDrawable(String source) {
									// TODO Auto-generated method stub

									Drawable drawable = null;
									URL url;
									try {
										url = new URL(source);
										drawable = Drawable.createFromStream(
												url.openStream(), "");
									} catch (Exception e) {
										return null;
									}
									drawable.setBounds(0, 0, drawable
											.getIntrinsicWidth(), drawable
											.getIntrinsicHeight());
									return drawable;
								}
							};

							textView.setText(Html.fromHtml(tableText,
									imgGetter, null));
							textView.setMovementMethod(LinkMovementMethod
									.getInstance());
							CharSequence textCharSequence = textView.getText();

							if (textCharSequence instanceof Spannable) {
								int end = textCharSequence.length();
								Spannable sp = (Spannable) textView.getText();
								URLSpan[] urls = sp.getSpans(0, end,
										URLSpan.class);
								SpannableStringBuilder style = new SpannableStringBuilder(
										textCharSequence);
								for (URLSpan url : urls) 
									style.removeSpan(url);// should clear old spans
								for (URLSpan url : urls) {
									String urlString = url.getURL();
									MyURLSpan myURLSpan = new MyURLSpan(
											urlString);
									if (urlString.startsWith("/wiki/")) {
										style
												.setSpan(
														myURLSpan,
														sp.getSpanStart(url),
														sp.getSpanEnd(url),
														Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
									} else if (urlString.startsWith("/w/")) {
										;
									} else {
										style
												.setSpan(
														new URLSpan(urlString),
														sp.getSpanStart(url),
														sp.getSpanEnd(url),
														Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

									}
								}
								textView.setText(style);
							}
							tableRow.addView(textView);
						}
						tableLayout.addView(tableRow);
					}
					tables.add(tableLayout);
					addView(tableLayout,
							new LinearLayout.LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));

				} else {
					pictureSrc = (String) contentArrayList.get(i).get("src");
				}

				contenTextView.setPadding(5, 5, 5, 5);
				contenTextView.setTextColor(0xff000000);
				addView(contenTextView, new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				contentTextViews.add(contenTextView);
			}

		}

		public void setTitle(String title) {
			innerTitleTextView.setText(title);
		}

		public void setExpanded(boolean expanded) {
			if(expanded)
				setContentText();			
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

		private TextView innerTitleTextView;
		private ArrayList<TextView> contentTextViews;
		private ArrayList<TableLayout> tables;
		private ArrayList<Map<String, Object>> contentArrayList;
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
				title = URLDecoder.decode(mUrl.substring(6), "utf-8");
				Intent pageIntent = new Intent(ctx, WikiPageActivity.class);
				pageIntent.putExtra("page", title);
				startActivity(pageIntent);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}

	private static Context ctx;
	private ArrayList<Map<String, Object>> pageArrayList;
	private String title;
	private ListView listView;
}
