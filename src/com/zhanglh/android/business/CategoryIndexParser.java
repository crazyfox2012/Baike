package com.zhanglh.android.business;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CategoryIndexParser {
	public static ArrayList<Map<String, Object>> html2string() {
		ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
		Document doc;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();			
			HttpUriRequest request = new HttpGet("http://zh.wikipedia.org/wiki/Wikipedia:%E5%88%86%E9%A1%9E%E7%B4%A2%E5%BC%95");
			HttpResponse response=httpClient.execute(request);
			HttpEntity entity=response.getEntity();
			InputStream is = entity.getContent();   
			String contentString="";
			BufferedInputStream bufferedInputStream=null;
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			
			byte[] buffer= new byte[1024];
			int readbytes=0;
			try{
				bufferedInputStream=new BufferedInputStream(is);
				while((readbytes=bufferedInputStream.read(buffer))!=-1)
					byteArrayOutputStream.write(buffer,0,readbytes);
			}finally{
				if(bufferedInputStream!=null)
					bufferedInputStream.close();
				if(byteArrayOutputStream!=null)
					byteArrayOutputStream.close();
			}
	        contentString = byteArrayOutputStream.toString();   
	        doc = Jsoup.parse(contentString);

			Element content = doc.getElementById("bodyContent");
			Elements tables = content.select("table table");
			for (Element table : tables) {
				Elements trs = table.select("tr");
				for (Element tr : trs) {
					Map<String, Object> tableMap = new HashMap<String, Object>();
					Element b = tr.select("b").first();
					if (b.text().compareTo("Ö÷î}Ê×í“") == 0)
						break;
					tableMap.put("title", b.html());
					String imgSrc = tr.getElementsByTag("img").attr("src");
					URL url = new URL(imgSrc);

					tableMap.put("image", getRemoteImage(url));
					
					Element p = tr.select("p").first();
					tableMap.put("content", p.html());

					arrayList.add(tableMap);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayList;
	}
	
	private static Bitmap getRemoteImage(URL aURL) throws IOException {
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
