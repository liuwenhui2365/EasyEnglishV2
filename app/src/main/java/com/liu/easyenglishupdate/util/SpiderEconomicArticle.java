package com.liu.easyenglishupdate.util;

import android.util.Log;

import com.liu.easyenglishupdate.entity.Article;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取分享经济学类网站的内容，并按要求获取相应的内容
 */
public class SpiderEconomicArticle implements ISpiderArticle {

	final private Pattern titlePat = Pattern.compile("<h1.*?>(.*?)</h1>");
	final private Pattern timePat = Pattern.compile("<time.*?>(.*?)</time>");
	final private Pattern catalogyPat = Pattern.compile("<div class=\"w980 greyTxt9 titleTxt22 pt20 pb10\"><a.*?>(.*?)</a>",Pattern.MULTILINE|Pattern.DOTALL);
	final private Pattern contentPat = Pattern.compile("<div.*?itemprop=\"articleBody\">(.*?)</article>",Pattern.MULTILINE|Pattern.DOTALL);
	final private Pattern contentFilter = Pattern.compile("<p>(<.*?>)?(.*?)(<.*?>)?</p>");


	@Override
	public ArrayList<String> getUrlList(String catalogy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessage(String url) throws IOException{
		// TODO Auto-generated method stub
        String message = null;
//            HttpClient httpClient = new HttpClient(url);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = null;
        httpResponse = httpClient.execute(httpGet);
        if (httpResponse.getStatusLine().getStatusCode() == 200){
            HttpEntity entity = httpResponse.getEntity();
            message = EntityUtils.toString(entity, "UTF-8");
        }
//            Log.d("网页内容",message);
        return message;
	}

	@Override
	public String getTitle(String message) {
		// TODO Auto-generated method stub
		String title = null;
		Matcher mat = titlePat.matcher(message);
		if(mat.find()){
			title = mat.group(1);
		}
		return title;
	}

	@Override
	public StringBuilder getContent(String message) {
		// TODO Auto-generated method stub
		StringBuilder content = new StringBuilder();
		StringBuilder bodyItem = new StringBuilder();
		Matcher mat = contentPat.matcher(message);
		if (mat.find()) {	
			content.append(mat.group(1));
			//System.out.println("Content:" + content);
			Matcher matFilter = contentFilter.matcher(content.toString());
			while (matFilter.find()) {
				bodyItem.append(matFilter.group(2));
				bodyItem.append("^");
			}
		}
//      替换掉不需要的的标签
        String objBody = bodyItem.toString().replace("<span>","");
        objBody = objBody.replace("</span>","");
//        objBody = objBody.replace("")
        content.setLength(0);
        content = new StringBuilder(objBody);
		return content;
	}

	@Override
	public String getTime(String message) {
		// TODO Auto-generated method stub
		String time = null;
		Matcher mat = timePat.matcher(message);
		if(mat.find()){
			time = mat.group(1);
		}
		return time;
	}

	@Override
	public String getCatalogy(String message) {
		// TODO Auto-generated method stub

		return "默认";
	}

    //    初始化分享经济学文章对象
    public Article getPassage(String url) throws IOException {
        String message = getMessage(url);
//            Log.d("网页内容",message);
        String title = getTitle(message);
//            Log.d("标题完成", title);
        String content = getContent(message).toString();
        Log.d("内容完成", content.toString());
        String catalogy = getCatalogy(message);
        String time = getTime(message);
//            Log.d("时间完成",time);
        Article article = new Article(title, content, catalogy);
        article.setTime(time);
//            Log.d("文章对象初始化完成！","恭喜");
        return article;
    }

}
