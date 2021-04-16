package com.example.demo.util;

import com.example.demo.domain.Content;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HttpParseUtils {

	public static void main(String[] args) throws IOException {
		List<Content> list = parseJD("hello");
		list.forEach(System.out::println);
	}

	public static List<Content> parseJD(String keyword) throws IOException {
		List<Content> list = new ArrayList<>();
		String url = "https://search.jd.com/Search?keyword=" + keyword
				+ "&enc=utf-8&wq=java&pvid=404eb7c72ed74828862f2c3bcbef029d";
		// 解析网页
		Document document = Jsoup.parse(new URL(url), 30000);
		Element element = document.getElementById("J_goodsList");
//		System.out.println(element.html());
		Elements elements = element.getElementsByTag("li");
		// 获取元素中的内容，这组eL就是每一个Li标签了！
		for (Element el : elements) {
			String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
			String price = el.getElementsByClass("p-price").eq(0).text();
			String title = el.getElementsByClass("p-name").eq(0).text();
			Content content = new Content(img, price, title);
			list.add(content);
		}
		return list;
	}
}
