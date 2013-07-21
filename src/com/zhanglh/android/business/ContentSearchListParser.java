package com.zhanglh.android.business;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.util.Log;

public class ContentSearchListParser {
	public static ArrayList<Map<String, String>> xml2string(URL url) {
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
		try {
			SAXReader reader = new SAXReader();

			Document doc = reader.read(url);

			Element root = doc.getRootElement();
			// Element
			// searchSuggestionElement=root.element("SearchSuggestion");//
			Element sectionElement = root.element("Section");

			for (Iterator it = sectionElement.elementIterator(); it.hasNext();) {
				Map<String, String> map = new HashMap<String, String>();
				Element itemElement = (Element) it.next();
				Element textElement = itemElement.element("Text");
				Element descriptionElement = itemElement.element("Description");
				Element imageElement = itemElement.element("Image");

				map.put("Text", textElement.getText());
				map.put("Description", descriptionElement.getText());
				if (imageElement != null) {
					Attribute attribute = imageElement.attribute("source");
					Log.i("DOM", attribute.getText());
					map.put("Image", attribute.getText());
				}
				arrayList.add(map);

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayList;
	}

}
