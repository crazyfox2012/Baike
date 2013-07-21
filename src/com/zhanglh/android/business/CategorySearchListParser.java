package com.zhanglh.android.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CategorySearchListParser {
	public static ArrayList<Map<String, String>> xml2string(URL url) {
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
		try {
			SAXReader reader = new SAXReader();

			Document doc = reader.read(url);

			Element root = doc.getRootElement();
			
			Element queryElement=root.element("query");
			Element allcategoriesElement = queryElement.element("allcategories");

			for (Iterator it = allcategoriesElement.elementIterator(); it.hasNext();) {
				Map<String, String> map = new HashMap<String, String>();
				Element categoryElement = (Element) it.next();
				map.put("category", categoryElement.getText());
				arrayList.add(map);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayList;
	}

}
