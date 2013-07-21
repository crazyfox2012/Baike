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

public class CategoryParser{
	public static ArrayList<Map<String, String>> xml2string(URL url,boolean sonCategory) {
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
		
		try {
			SAXReader reader = new SAXReader();

			Document doc = reader.read(url);

			Element root=doc.getRootElement();    
			Element queryElement=root.element("query"); 
			Element categoryMembersElement=queryElement.element("categorymembers");

			
			for(Iterator it=categoryMembersElement.elementIterator();it.hasNext();){
				Map<String, String> map = new HashMap<String, String>();
				Element cmElement = (Element) it.next();
                Attribute attribute=cmElement.attribute("title");
                if(sonCategory)
                	map.put("category", attribute.getText().substring(9));
                else 
                	map.put("category", attribute.getText());
				arrayList.add(map);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayList;
	}

}
