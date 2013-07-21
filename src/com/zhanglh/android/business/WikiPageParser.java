package com.zhanglh.android.business;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.R.integer;
import android.util.Log;

public class WikiPageParser {

	public static ArrayList<Map<String, Object>> html2string(String title) {
		ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();

		try {
			String titleUtf8 = URLEncoder.encode(title, "utf-8");

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(
					"http://zh.wikipedia.org/wiki/" + titleUtf8);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String contentString = "";
			BufferedInputStream bufferedInputStream = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int readbytes = 0;
			try {
				bufferedInputStream = new BufferedInputStream(is);
				while ((readbytes = bufferedInputStream.read(buffer)) != -1)
					byteArrayOutputStream.write(buffer, 0, readbytes);
			} finally {
				if (bufferedInputStream != null)
					bufferedInputStream.close();
				if (byteArrayOutputStream != null)
					byteArrayOutputStream.close();
			}
			contentString = byteArrayOutputStream.toString();

			Document doc;

			doc = Jsoup.parse(contentString);

			Element content = doc.getElementById("bodyContent");
			Elements contentOrTitles = content
					.select("p,h2>span.mw-headline,h3,div.thumbinner > a,table");
			// Elements elements = content.select("h2~p");

			boolean summary = true;
			Element contentOrTitle = null;
			Map<String, Object> map0 = new HashMap<String, Object>();
			map0.put("title", title);
			map0.put("type", "0");
			arrayList.add(map0);

			for (Iterator it = contentOrTitles.iterator(); it.hasNext();) {
				Map<String, Object> map = new HashMap<String, Object>();
				// if(contentOrTitle.nodeName() == "img")
				// text+=contentOrTitle.attr("src")+"\n";

				if (summary) {
					map.put("title", "¸ÅÊö");
					map.put("type", "1");
					summary = false;
				} else {
					if (contentOrTitle.text().compareTo("Ä¿Â¼") == 0)
						contentOrTitle = (Element) it.next();

					map.put("title", contentOrTitle.text());
					map.put("type", "1");
				}

				contentOrTitle = (Element) it.next();

				ArrayList<Map<String, Object>> contentArrayList = new ArrayList<Map<String, Object>>();
				String h3 = "";

				while (contentOrTitle.nodeName() != "span" && it.hasNext()) {
					Map<String, Object> smallTitleMap = new HashMap<String, Object>();
					Map<String, Object> paragraphMap = new HashMap<String, Object>();
					Map<String, Object> pictureMap = new HashMap<String, Object>();
					Map<String, Object> tableMap = new HashMap<String, Object>();

					if (contentOrTitle.nodeName() == "a") {
						String href = contentOrTitle.attr("href");
						String src = contentOrTitle.getElementsByTag("img")
								.attr("src");
						pictureMap.put("href", href);
						pictureMap.put("src", src);
						contentArrayList.add(pictureMap);
						contentOrTitle = (Element) it.next();
					}

					if (contentOrTitle.nodeName() == "h3") {
						h3 = contentOrTitle.text();
						smallTitleMap.put("h3", h3);
						contentArrayList.add(smallTitleMap);
						contentOrTitle = (Element) it.next();
					}

					if (contentOrTitle.nodeName() == "table") {
						String tableClassName = contentOrTitle.className();
						if (tableClassName.compareTo("toc") != 0
								&& tableClassName.compareTo("navbox") != 0
								&& !tableClassName.startsWith("nowraplinks")) {
							tableMap.put("class", tableClassName);
							// Log.i("className", tableClassName);
							Elements trs = contentOrTitle.select("tr");
							ArrayList<ArrayList<Map<String, String>>> trArrayList = new ArrayList<ArrayList<Map<String, String>>>();

							for (Element tr : trs) {
								ArrayList<Map<String, String>> thOrTdArrayList = new ArrayList<Map<String, String>>();
								Elements thOrTds = tr.select("th,td");

								for (Element thOrTd : thOrTds) {
									Map<String, String> thOrTdMap = new HashMap<String, String>();
									thOrTdMap.put("colspan", thOrTd
											.attr("colspan"));
									thOrTdMap
											.put("align", thOrTd.attr("align"));
									if (thOrTd.nodeName() == "th") {
										thOrTdMap.put("name", "th");
										thOrTdMap.put("text", thOrTd.html());
									} else {
										thOrTdMap.put("name", "td");
										thOrTdMap.put("text", thOrTd.html());
									}
									// Log.i("text", thOrTd.html());
									thOrTdArrayList.add(thOrTdMap);
								}
								trArrayList.add(thOrTdArrayList);
							}
							tableMap.put("table", trArrayList);
							contentArrayList.add(tableMap);
						}
						contentOrTitle = (Element) it.next();
					}
					String p = "";
					while (contentOrTitle.nodeName() != "span"
							&& contentOrTitle.nodeName() != "h3"
							&& contentOrTitle.nodeName() != "a"
							&& contentOrTitle.nodeName() != "table"
							&& it.hasNext()) {
						if (contentOrTitle.nodeName() == "p") {
							p += "<p>&nbsp;&nbsp;" + contentOrTitle.html()
									+ "</p>";
							contentOrTitle = (Element) it.next();
						}
					}
					paragraphMap.put("p", p);
					// Log.i("p", p);
					contentArrayList.add(paragraphMap);

				}
				map.put("content", contentArrayList);
				map.put("type", "1");
				arrayList.add(map);
				Log.i("pageParser", "finish");

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayList;
	}
}
