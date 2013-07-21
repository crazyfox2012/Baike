package com.zhanglh.android.business;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BaiduSearchListParser {
	public static ArrayList<Map<String, String>> html2string(String search) {
		ArrayList<Map<String, String>> arrayList=new ArrayList<Map<String,String>>();
		Document doc;
		try {
			String searchGBK = URLEncoder.encode(search, "gbk");
			doc = Jsoup.connect(
					"http://baike.baidu.com/w?ct=17&lm=0&tn=baiduWikiSearch&pn=0&rn=10&word="
							+ searchGBK + "&submit=search").timeout(10000).get();

			Elements results = doc.select("td.f");		
			
			for (Element result : results) {
				Map<String, String> map = new HashMap<String, String>();
				Elements fonts = result.getElementsByTag("font");

				Element title = fonts.get(0);
				map.put("title", title.html());
				Element content = fonts.get(2);
				map.put("content", content.html());
			
				arrayList.add(map);
			}		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arrayList;
	}
}
