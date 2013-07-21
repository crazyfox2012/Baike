package com.zhanglh.android.business;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BaiduPageParser {
	public static Map<String, Object> html2string(String url) {
		Document doc;
		Map<String, Object> map=new HashMap<String, Object>();
		try {			
			doc = Jsoup.connect(url).timeout(10000).get();
			
			Elements titles = doc.select("h1");
			String title = "";
			title = titles.first().text();
			map.put("title", title);
			
			Elements summaries = doc.select("div.card-summary*");
			String summary="";
			summary = summaries.first().html();
			map.put("summary", summary);
			
			Elements contents = doc.select("div.lemma-main-content");
			Elements tables = contents.first().select("table");
			tables.remove();
			map.put("content", contents.first().html());

		} catch (IOException e) {

			e.printStackTrace();
		}
		return map;
	}

}
